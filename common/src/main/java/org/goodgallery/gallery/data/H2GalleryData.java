package org.goodgallery.gallery.data;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public final class H2GalleryData extends AbstractSQLGalleryData {

  public H2GalleryData(Path path) {
    super(path.resolve("gallery.sqlite"));
  }

  @Override
  protected @NotNull String getConnectionUrl() {
    return String.format("jdbc:h2:%s;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE", path.toAbsolutePath());
  }

  @Override
  protected @NotNull String getInsertItemStatement() {
    return "INSERT INTO gallery_items(unique_id, item_type) VALUES(?,?) ON CONFLICT DO NOTHING;";
  }

  @Override
  protected @NotNull String getDeleteItemStatement() {
    return "DELETE FROM gallery_items WHERE unique_id = ?";
  }

  @Override
  protected @NotNull String getUpdatePropertyStatement() {
    return "MERGE INTO properties(unique_id, \"key\", \"data\") KEY(unique_id, \"key\") VALUES (?,?,?)";
  }

}
