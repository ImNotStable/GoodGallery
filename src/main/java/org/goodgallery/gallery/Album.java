package org.goodgallery.gallery;

import lombok.Getter;
import org.goodgallery.gallery.properties.Properties;
import org.goodgallery.gallery.properties.PropertyKey;
import org.goodgallery.gallery.properties.SerializedProperties;

import java.util.Collection;
import java.util.UUID;

@Getter
public final class Album extends GalleryItem {

  private static final PropertyKey<?>[] DEFAULT_KEYS = {
    Properties.NAME_KEY, Properties.CREATION_TIMESTAMP_KEY, Properties.PHOTOS_KEY
  };

  public static Album create(UUID uniqueId, SerializedProperties serializedProperties) {
    return new Album(uniqueId, serializedProperties);
  }

  Album(UUID uniqueId, SerializedProperties serializedProperties) {
    super(uniqueId, serializedProperties, DEFAULT_KEYS);
  }

  Album() {
    super(DEFAULT_KEYS);
  }

  public Collection<Photo> getPhotos() {
    return getProperties().getValue(Properties.PHOTOS_KEY);
  }

  void addPhoto(Photo photo) {
    getPhotos().add(photo);
  }

  void removePhoto(Photo photo) {
    getPhotos().remove(photo);
  }

}
