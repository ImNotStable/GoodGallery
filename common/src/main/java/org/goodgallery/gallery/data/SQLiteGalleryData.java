package org.goodgallery.gallery.data;

import org.goodgallery.gallery.Album;
import org.goodgallery.gallery.GalleryItem;
import org.goodgallery.gallery.Group;
import org.goodgallery.gallery.Photo;
import org.goodgallery.gallery.properties.PropertiesImpl;
import org.goodgallery.gallery.properties.PropertyInstance;
import org.goodgallery.gallery.properties.SerializedProperties;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class SQLiteGalleryData extends AbstractGalleryData {

  private final String CONNECTION_URL;

  public SQLiteGalleryData(Path path) {
    path = path.resolve("gallery.sqlite");

    CONNECTION_URL = String.format("jdbc:sqlite:%s", path.toAbsolutePath());
    super(path);
  }

  private byte[] convertFromUUID(@NotNull UUID uniqueId) {
    return ByteBuffer.allocate(Long.BYTES * 2)
      .putLong(uniqueId.getMostSignificantBits())
      .putLong(uniqueId.getLeastSignificantBits())
      .array();
  }

  private UUID convertToUUID(byte @NotNull [] uniqueId) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(uniqueId);
    return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
  }

  private void deserializeAndStoreItem(UUID uniqueId, String itemType, Map<String, byte[]> rawProperties) {
    SerializedProperties serializedProperties = new SerializedProperties(rawProperties);
    switch (itemType) {
      case "photo" -> photosByUUID.put(uniqueId, new Photo(uniqueId, serializedProperties));
      case "album" -> albumsByUUID.put(uniqueId, new Album(uniqueId, serializedProperties));
      case "group" -> groupsByUUID.put(uniqueId, new Group(uniqueId, serializedProperties));
    }
  }

  private void createConnection(SQLConsumer<Connection> consumer, String failMessage) {
    try (Connection connection = DriverManager.getConnection(CONNECTION_URL)) {
      consumer.accept(connection);
    } catch (SQLException exception) {
      throw new RuntimeException(failMessage, exception);
    }
  }

  private void createConnectionWithPreparedStatement(String rawPreparedStatement, BiSQLConsumer<Connection, PreparedStatement> consumer, String failMessage) {
    createConnection(connection -> {
      try (PreparedStatement preparedStatement = connection.prepareStatement(rawPreparedStatement)) {
        consumer.accept(connection, preparedStatement);
      } catch (SQLException exception) {
        throw new RuntimeException(failMessage, exception);
      }
    }, failMessage);
  }

  private void createConnectionWithStatement(BiSQLConsumer<Connection, Statement> consumer, String failMessage) {
    createConnection(connection -> {
      try (Statement statement = connection.createStatement()) {
        consumer.accept(connection, statement);
      } catch (SQLException exception) {
        throw new RuntimeException(failMessage, exception);
      }
    }, failMessage);
  }

  @Override
  protected synchronized void load() {
    createConnectionWithStatement((_, statement) -> {
      try {
        statement.execute("CREATE TABLE IF NOT EXISTS gallery_items(unique_id BINARY(16) PRIMARY KEY, item_type VARCHAR(8));");
        statement.execute("""
            CREATE TABLE IF NOT EXISTS properties(
                unique_id BINARY(16), "key" VARCHAR(32), "data" BINARY(1024),
                FOREIGN KEY(unique_id) references gallery_items(unique_id) ON DELETE CASCADE,
                UNIQUE(unique_id, "key")
            );
        """);
      } catch (SQLException exception) {
        throw new RuntimeException("Failed to create tables for database", exception);
      }

      try (ResultSet resultSet = statement.executeQuery("""
        SELECT g.unique_id, g.item_type, p."key", p."data" FROM gallery_items g
        LEFT JOIN properties p ON g.unique_id = p.unique_id ORDER BY g.unique_id;
        """)) {
        UUID lastUniqueId = null;
        String lastItemType = null;
        Map<String, byte[]> lastItemProperties = new HashMap<>();

        while (resultSet.next()) {
          UUID currentUniqueId = convertToUUID(resultSet.getBytes("unique_id"));

          if (!currentUniqueId.equals(lastUniqueId)) {
            if (lastUniqueId != null)
              deserializeAndStoreItem(lastUniqueId, lastItemType, lastItemProperties);
            lastUniqueId = currentUniqueId;
            lastItemType = resultSet.getString("item_type");
            lastItemProperties = new HashMap<>();
          }

          String key = resultSet.getString("key");
          byte[] data = resultSet.getBytes("data");
          lastItemProperties.put(key, data);
        }
        if (lastUniqueId != null)
          deserializeAndStoreItem(lastUniqueId, lastItemType, lastItemProperties);
      }

      // Previous implementation for reference:
//      try (ResultSet galleryItemsResult = statement.executeQuery("SELECT * FROM gallery_items;")) {
//        while (galleryItemsResult.next()) {
//          byte[] uniqueIdBytes = galleryItemsResult.getBytes("unique_id");
//          UUID uniqueId = convertToUUID(uniqueIdBytes);
//          Map<String, byte[]> rawProperties = new HashMap<>();
//          try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM properties WHERE unique_id = ?")) {
//            preparedStatement.setBytes(1, uniqueIdBytes);
//            try (ResultSet propertiesResult = preparedStatement.executeQuery()) {
//              while (propertiesResult.next())
//                rawProperties.put(propertiesResult.getString("key"), propertiesResult.getBytes("data"));
//            }
//          }
//          SerializedProperties serializedProperties = new SerializedProperties(rawProperties);
//          switch (galleryItemsResult.getString("item_type")) {
//            case "photo" -> photosByUUID.put(uniqueId, new Photo(uniqueId, serializedProperties));
//            case "album" -> albumsByUUID.put(uniqueId, new Album(uniqueId, serializedProperties));
//            case "group" -> groupsByUUID.put(uniqueId, new Group(uniqueId, serializedProperties));
//          }
//        }
//      }
    }, "Failed to load gallery data");
  }

  @Override
  protected synchronized void insert(GalleryItem galleryItem) {
    createConnectionWithPreparedStatement("INSERT INTO gallery_items(unique_id, item_type) VALUES(?,?) ON CONFLICT DO NOTHING",
      (connection, insertItemStatement) -> {
        connection.setAutoCommit(false);

        insertItemStatement.setBytes(1, convertFromUUID(galleryItem.getUniqueId()));
        insertItemStatement.setString(2,
          switch (galleryItem) {
            case Photo _ -> "photo";
            case Album _ -> "album";
            case Group _ -> "group";
            default -> throw new IllegalStateException("Unexpected value: " + galleryItem);
          }
        );
        insertItemStatement.executeUpdate();
        try (PreparedStatement insertPropertyStatement = connection.prepareStatement("INSERT INTO properties(unique_id, \"key\", \"data\") VALUES (?,?,?)")) {
          for (PropertyInstance<?> property : ((PropertiesImpl) galleryItem.getProperties()).all()) {
            insertPropertyStatement.setBytes(1, convertFromUUID(galleryItem.getUniqueId()));
            insertPropertyStatement.setString(2, property.key().toString());
            insertPropertyStatement.setBytes(3, property.serialize());
            insertPropertyStatement.addBatch();
          }
          insertPropertyStatement.executeBatch();
        } catch (SQLException exception) {
          throw new RuntimeException("Failed to insert properties for \"%s\"".formatted(galleryItem), exception);
        }
        connection.commit();
        connection.setAutoCommit(true);
      }, "Failed to insert gallery item \"%s\"".formatted(galleryItem.getUniqueId()));
  }

  @Override
  protected synchronized void delete(GalleryItem galleryItem) {
    createConnectionWithPreparedStatement("DELETE FROM gallery_items WHERE unique_id = ?",
      (_, preparedStatement) -> {
        preparedStatement.setBytes(1, convertFromUUID(galleryItem.getUniqueId()));
        preparedStatement.executeUpdate();
      }, "Failed to delete gallery item \"%s\"".formatted(galleryItem.getUniqueId()));
  }

  @Override
  public synchronized void updateProperty(GalleryItem galleryItem, PropertyInstance<?> property) {
    createConnectionWithPreparedStatement("INSERT INTO properties(unique_id, \"key\", \"data\") VALUES (?,?,?) ON CONFLICT(unique_id, \"key\") DO UPDATE SET \"data\" = excluded.data",
      (_, preparedStatement) -> {
        preparedStatement.setBytes(1, convertFromUUID(galleryItem.getUniqueId()));
        preparedStatement.setString(2, property.key().toString());
        preparedStatement.setBytes(3, property.serialize());
        preparedStatement.executeUpdate();
      }, "Failed to update property \"%s\" for \"%s\"".formatted(property.key(), galleryItem)
    );
  }

  private interface SQLConsumer<STATEMENT> {
    void accept(STATEMENT t) throws SQLException;
  }

  private interface BiSQLConsumer<CONNECTION, STATEMENT> {
    void accept(CONNECTION connection, STATEMENT statement) throws SQLException;
  }

}
