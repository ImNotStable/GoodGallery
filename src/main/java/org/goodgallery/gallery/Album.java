package org.goodgallery.gallery;

import lombok.Getter;
import org.goodgallery.gallery.properties.Properties;
import org.goodgallery.gallery.properties.PropertyKey;
import org.goodgallery.gallery.properties.SerializedProperties;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Getter
public final class Album extends GalleryItem {

  private static final PropertyKey<?>[] DEFAULT_KEYS = {
    Properties.NAME_KEY, Properties.CREATION_TIMESTAMP_KEY, Properties.PHOTOS_KEY
  };

  /**
   * Create a new Album with the given unique identifier and serialized properties.
   *
   * @param uniqueId the album's unique identifier
   * @param serializedProperties properties used to initialize the album's state
   * @return the newly created Album initialized with the provided id and properties
   */
  public static Album create(UUID uniqueId, SerializedProperties serializedProperties) {
    return new Album(uniqueId, serializedProperties);
  }

  /**
   * Initialize an Album with the specified unique identifier and serialized properties.
   *
   * @param uniqueId the album's unique identifier
   * @param serializedProperties persisted properties used to initialize the album's state
   */
  Album(UUID uniqueId, SerializedProperties serializedProperties) {
    super(uniqueId, serializedProperties, DEFAULT_KEYS);
  }

  /**
   * Constructs an Album initialized with the class's default property keys.
   *
   * <p>Delegates to the superclass to register DEFAULT_KEYS.</p>
   */
  Album() {
    super(DEFAULT_KEYS);
  }

  /**
   * Get an unmodifiable view of this album's photos.
   *
   * @return an unmodifiable collection containing the album's Photo objects
   */
  public Collection<Photo> getPhotos() {
    return Collections.unmodifiableCollection(getProperties().getValue(Properties.PHOTOS_KEY));
  }

}