package org.goodgallery.gallery.data;

import lombok.AccessLevel;
import lombok.Getter;
import org.sqlite.SQLiteConfig;

import java.nio.file.Path;
import java.util.Properties;

@Getter(AccessLevel.PROTECTED)
public final class SQLiteGalleryData extends AbstractSQLGalleryData {

  private final String connectionUrl;
  private final Properties connectionProperties;
  private final String insertItemStatement = "INSERT INTO gallery_items(unique_id, item_type) VALUES(?,?) ON CONFLICT DO NOTHING;";
  private final String upsertPropertyStatement = "INSERT INTO properties(unique_id, \"key\", \"data\") VALUES (?,?,?) ON CONFLICT(unique_id, \"key\") DO UPDATE SET \"data\" = excluded.data";

  public SQLiteGalleryData(Path path) {
    path = path.resolve("gallery.sqlite");

    connectionUrl = String.format("jdbc:sqlite:%s", path.toAbsolutePath());

    SQLiteConfig config = new SQLiteConfig();
    config.setJournalMode(SQLiteConfig.JournalMode.WAL);
    config.setSynchronous(SQLiteConfig.SynchronousMode.NORMAL);
    config.enforceForeignKeys(true);
    config.setEncoding(SQLiteConfig.Encoding.UTF8);
    connectionProperties = config.toProperties();

    super(path);
  }

}
