package org.goodgallery.gallery;

public final class GalleryInstance {

  private static Gallery GALLERY = null;

  public static synchronized Gallery init(GallerySettings properties) {
    if (GALLERY != null)
      throw new IllegalStateException("Gallery instance has already been initialized");
    try {
      return GALLERY = new Gallery(properties);
    } catch (Exception exception) {
      throw new RuntimeException("Failed to initialize Gallery", exception);
    }
  }

  public static synchronized Gallery get() {
    if (GALLERY == null)
      throw new IllegalStateException("Gallery has not been initialized. Call init() first.");
    return GALLERY;
  }

}
