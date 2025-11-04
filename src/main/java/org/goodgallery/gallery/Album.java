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
   * Create an Album initialized with the given unique identifier and serialized properties.
   *
   * @param uniqueId the unique identifier for the album
   * @param serializedProperties persisted properties used to initialize the album's state
   */
  Album(UUID uniqueId, SerializedProperties serializedProperties) {
    super(uniqueId, serializedProperties, DEFAULT_KEYS);
  }

  /**
   * Creates an Album initialized with the default property keys.
   *
   * <p>Package-private no-argument constructor that delegates to the superclass with DEFAULT_KEYS.</p>
   */
  Album() {
    super(DEFAULT_KEYS);
  }

  /**
   * Provide an unmodifiable view of the album's photos.
   *
   * @return an unmodifiable collection of Photo objects containing the album's photos
   */
  public Collection<Photo> getPhotos() {
    return Collections.unmodifiableCollection(getProperties().getValue(Properties.PHOTOS_KEY));
  }

}