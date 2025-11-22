package org.goodgallery.gallery.data;

import lombok.AccessLevel;
import lombok.Getter;

import java.nio.file.Path;
import java.util.Properties;

@Getter(AccessLevel.PROTECTED)
public final class H2GalleryData extends AbstractSQLGalleryData {

  private final String connectionUrl;
  private final Properties connectionProperties = new Properties();
  private final String insertItemStatement = "INSERT INTO gallery_items(unique_id, item_type) VALUES(?,?) ON CONFLICT DO NOTHING;";
  private final String upsertPropertyStatement = "MERGE INTO properties(unique_id, \"key\", \"data\") KEY(unique_id, \"key\") VALUES (?,?,?)";

  public H2GalleryData(Path path) {
    path = path.resolve("gallery.h2");

    connectionUrl = String.format("jdbc:h2:%s;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;AUTO_SERVER=TRUE", path.toAbsolutePath());

    super(path);
  }

}
