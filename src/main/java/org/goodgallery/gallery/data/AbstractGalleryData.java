package org.goodgallery.gallery.data;

public abstract class AbstractGalleryData implements GalleryData {

  /**
 * Persist the gallery's current state to permanent storage.
 *
 * Implementations should perform any necessary synchronization and ensure the saved state is durable.
 */
  protected abstract void save();

}
