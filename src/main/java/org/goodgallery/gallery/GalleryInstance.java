package org.goodgallery.gallery;

import java.io.IOException;
import java.nio.file.Path;

public final class GalleryInstance {

  private static Gallery GALLERY = null;

  public static Gallery init(Path path) {
    if (GALLERY != null)
      throw new IllegalStateException("Gallery instance has already been initialized");
    try {
      return GALLERY = new Gallery(path);
    } catch (IOException e) {
      System.err.println("What the hellie happened!?");
      throw new RuntimeException("Failed to initialize Gallery", e);
    }
  }

  public static Gallery get() {
    return GALLERY;
  }

}
