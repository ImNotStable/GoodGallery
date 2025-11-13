package org.goodgallery.gallery;

import org.goodgallery.gallery.data.GalleryData;
import org.goodgallery.gallery.data.JsonGalleryData;
import org.goodgallery.gallery.data.SQLiteGalleryData;

import java.io.IOException;
import java.nio.file.Path;

public class GalleryProperties {

  private String storage = "sqlite";

  public GalleryProperties storage(String storage) {
    this.storage = storage;
    return this;
  }

  public GalleryData storage(Path path) {
    return switch (storage) {
      case "sqlite" -> new SQLiteGalleryData(path);
      case "json" -> {
        try {
          yield new JsonGalleryData(path);
        } catch (IOException exception) {
          throw new RuntimeException("Failed to start Json gallery data", exception);
        }
      }
      default -> throw new IllegalArgumentException("Unknown storage type: " + storage);
    };
  }

}
