package org.goodgallery.gallery;

import lombok.Getter;
import org.goodgallery.gallery.properties.Properties;
import org.goodgallery.gallery.properties.PropertyKey;
import org.goodgallery.gallery.properties.SerializedProperties;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Getter
public final class Group extends GalleryItem {

  private static final PropertyKey<?>[] DEFAULT_KEYS = {
    Properties.NAME_KEY, Properties.CREATION_TIMESTAMP_KEY, Properties.ALBUMS_KEY
  };

  /**
   * Create a Group with the given unique identifier and serialized properties.
   *
   * @param uniqueId             the unique identifier for the group
   * @param serializedProperties serialized properties used to initialize the group's properties
   * @return                     the newly created Group instance
   */
  public static Group create(UUID uniqueId, SerializedProperties serializedProperties) {
    return new Group(uniqueId, serializedProperties);
  }

  /**
   * Creates a Group with the given unique identifier and serialized properties.
   *
   * @param uniqueId the group's UUID
   * @param serializedProperties serialized property values used to initialize the group
   */
  Group(UUID uniqueId, SerializedProperties serializedProperties) {
    super(uniqueId, serializedProperties, DEFAULT_KEYS);
  }

  /**
   * Creates a Group initialized with the class default property keys.
   */
  Group() {
    super(DEFAULT_KEYS);
  }

  /**
   * Get the albums contained in this group.
   *
   * The returned collection is unmodifiable and represents the group's stored albums.
   *
   * @return an unmodifiable Collection of Album objects in this group
   */
  public Collection<Album> getAlbums() {
    return Collections.unmodifiableCollection(getProperties().getValue(Properties.ALBUMS_KEY));
  }

}