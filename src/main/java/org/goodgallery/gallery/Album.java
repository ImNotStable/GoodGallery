package org.goodgallery.gallery;

import lombok.Getter;
import org.goodgallery.gallery.properties.PropertiesImpl;
import org.goodgallery.gallery.properties.PropertyKey;
import org.goodgallery.gallery.properties.SerializedProperties;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
public final class Album extends GalleryItem {

  private static final PropertyKey<?>[] DEFAULT_KEYS = {
    PropertiesImpl.NAME_KEY, PropertiesImpl.CREATION_TIMESTAMP_KEY
  };

  public static Album create(UUID uniqueId, SerializedProperties serializedProperties, Photo... photos) {
    return new Album(uniqueId, serializedProperties, photos);
  }

  private final Set<Photo> photos;

  Album(UUID uniqueId, SerializedProperties serializedProperties, Photo... photos) {
    super(uniqueId, serializedProperties, DEFAULT_KEYS);
    this.photos = new HashSet<>();
    this.photos.addAll(Arrays.asList(photos));
  }

  Album(Photo... photos) {
    super(DEFAULT_KEYS);
    this.photos = new HashSet<>();
    this.photos.addAll(Arrays.asList(photos));
  }

  public Collection<Photo> getPhotos() {
    return Collections.unmodifiableCollection(photos);
  }

  void addPhoto(Photo photo) {
    photos.add(photo);
  }

  void removePhoto(Photo photo) {
    photos.remove(photo);
  }

}
