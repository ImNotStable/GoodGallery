package org.goodgallery.gallery;

import org.goodgallery.gallery.data.GalleryData;
import org.goodgallery.gallery.data.JsonGalleryData;
import org.goodgallery.gallery.data.SQLiteGalleryData;

import java.nio.file.Path;

public class GallerySettings {

  private StorageType storage = StorageType.SQLITE;
  private Path galleryPath = Path.of("gallery");

  public GallerySettings storage(StorageType storage) {
    this.storage = storage;
    return this;
  }

  public GalleryData storage(Path path) throws Exception {
    return switch (storage) {
      case SQLITE -> new SQLiteGalleryData(path);
      case JSON -> new JsonGalleryData(path);
    };
  }

  public GallerySettings galleryPath(Path path) {
    this.galleryPath = path;
    return this;
  }

  public Path galleryPath() {
    return galleryPath;
  }

  public enum StorageType {
    SQLITE,
    JSON
  }

}
