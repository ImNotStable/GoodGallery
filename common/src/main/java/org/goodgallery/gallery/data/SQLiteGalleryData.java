package org.goodgallery.gallery.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.goodgallery.gallery.Album;
import org.goodgallery.gallery.GalleryItem;
import org.goodgallery.gallery.Group;
import org.goodgallery.gallery.Photo;
import org.goodgallery.gallery.properties.PropertyInstance;
import org.goodgallery.gallery.properties.SerializedProperties;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class SQLiteGalleryData extends AbstractGalleryData {

  private final HikariDataSource dataSource;

  public SQLiteGalleryData(Path path) {
    super(path.resolve("gallery.sqlite"));

    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(String.format("jdbc:sqlite:%s", super.path.toAbsolutePath()));
    dataSource = new HikariDataSource(config);

    try (Connection connection = dataSource.getConnection();
         Statement statement = connection.createStatement()) {
      statement.execute("CREATE TABLE IF NOT EXISTS gallery_items(unique_id BINARY(16) PRIMARY KEY, item_type VARCHAR(8));");
      statement.execute("""
        CREATE TABLE IF NOT EXISTS properties(
            unique_id BINARY(16) PRIMARY KEY,
            "key" VARCHAR(32),
            "data" BINARY(1024),
            FOREIGN KEY(unique_id) references gallery_items(unique_id) ON DELETE CASCADE,
            UNIQUE(unique_id, "key")
        );
""");
    } catch (SQLException exception) {
      throw new RuntimeException("Failed to create tables for database", exception);
    }
  }

  private byte[] convertFromUUID(UUID uniqueId) {
    return ByteBuffer.allocate(Long.BYTES * 2)
      .putLong(uniqueId.getMostSignificantBits())
      .putLong(uniqueId.getLeastSignificantBits())
      .array();
  }

  private UUID convertToUUID(byte[] uniqueId) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(uniqueId);
    return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
  }

  @Override
  protected synchronized void load() {
    try (Connection connection = dataSource.getConnection();
         Statement statement = connection.createStatement();
         ResultSet galleryItemsResult = statement.executeQuery("SELECT * FROM gallery_items;")) {

      while (galleryItemsResult.next()) {
        byte[] uniqueIdBytes = galleryItemsResult.getBytes("unique_id");
        UUID uniqueId = convertToUUID(uniqueIdBytes);
        Map<String, byte[]> rawProperties = new HashMap<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM properties WHERE unique_id = ?")) {
          preparedStatement.setBytes(1, uniqueIdBytes);
          try (ResultSet propertiesResult = preparedStatement.executeQuery()) {
            while (propertiesResult.next())
              rawProperties.put(propertiesResult.getString("key"), propertiesResult.getBytes("data"));
          }
        }
        SerializedProperties serializedProperties = new SerializedProperties(rawProperties);
        switch (galleryItemsResult.getString("item_type")) {
          case "photo" -> photosByUUID.put(uniqueId, new Photo(uniqueId, serializedProperties));
          case "album" -> albumsByUUID.put(uniqueId, new Album(uniqueId, serializedProperties));
          case "group" -> groupsByUUID.put(uniqueId, new Group(uniqueId, serializedProperties));
        }
      }

    } catch (SQLException exception) {
      throw new RuntimeException("Failed to load gallery data", exception);
    }
  }

  @Override
  protected synchronized void insert(GalleryItem galleryItem) {
    try (Connection connection = dataSource.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO gallery_items(unique_id, item_type) VALUES(?,?) ON CONFLICT DO NOTHING")) {
      preparedStatement.setBytes(1, convertFromUUID(galleryItem.getUniqueId()));
      preparedStatement.setString(2,
        switch (galleryItem) {
          case Photo _ -> "photo";
          case Album _ -> "album";
          case Group _ -> "group";
          default -> throw new IllegalStateException("Unexpected value: " + galleryItem);
        }
      );
      preparedStatement.executeUpdate();
    } catch (SQLException exception) {
      throw new RuntimeException("Failed to insert gallery item \"%s\"".formatted(galleryItem.getUniqueId()), exception);
    }
  }

  @Override
  protected synchronized void delete(GalleryItem galleryItem) {
    try (Connection connection = dataSource.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM gallery_items WHERE unique_id = ?")) {
      preparedStatement.setBytes(1, convertFromUUID(galleryItem.getUniqueId()));
      preparedStatement.executeUpdate();
    } catch (SQLException exception) {
      throw new RuntimeException("Failed to delete gallery item \"%s\"".formatted(galleryItem.getUniqueId()), exception);
    }
  }

  @Override
  public synchronized void updateProperty(GalleryItem galleryItem, PropertyInstance<?> property) {
    try (Connection connection = dataSource.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO properties(unique_id, \"key\", \"data\") VALUES (?,?,?) ON CONFLICT(unique_id, \"key\") DO UPDATE SET \"data\" = excluded.data")) {
      preparedStatement.setBytes(1, convertFromUUID(galleryItem.getUniqueId()));
      preparedStatement.setString(2, property.key().toString());
      preparedStatement.setBytes(3, property.serialize());
      preparedStatement.executeUpdate();
    } catch (SQLException exception) {
      throw new RuntimeException("Failed to update property \"%s\" for \"%s\"".formatted(property.key(), galleryItem), exception);
    }
  }

  @Override
  public synchronized void close() {
    if (dataSource == null)
      throw new IllegalStateException("Failed to close data source as it was already closed");
    dataSource.close();
  }

}
