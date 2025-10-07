package org.goodgallery.gallery;

import java.io.IOException;
import java.nio.file.Path;

public final class GalleryInstance {

  private static Gallery GALLERY = null;

  public static void init(Path path) throws IOException {
    if (GALLERY != null)
      throw new IllegalStateException("Gallery instance has already been initialized");
    GALLERY = new Gallery(path);
  }

  public static Gallery get() {
    return GALLERY;
  }

}
