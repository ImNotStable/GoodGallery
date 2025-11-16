package org.goodgallery.gallery;

import org.goodgallery.gallery.data.GalleryData;
import org.goodgallery.gallery.data.JsonGalleryData;
import org.goodgallery.gallery.data.SQLiteGalleryData;

import java.nio.file.Path;

public class GallerySettings {

  private String storage = "sqlite";
  private Path galleryPath = Path.of("gallery");

  public GallerySettings storage(String storage) {
    this.storage = storage;
    return this;
  }

  public GalleryData storage(Path path) throws Exception {
    return switch (storage) {
      case "sqlite" -> new SQLiteGalleryData(path);
      case "json" -> new JsonGalleryData(path);
      default -> throw new IllegalArgumentException("Unknown storage type: " + storage);
    };
  }

  public GallerySettings galleryPath(Path path) {
    this.galleryPath = path;
    return this;
  }

  public Path galleryPath() {
    return galleryPath;
  }

}
