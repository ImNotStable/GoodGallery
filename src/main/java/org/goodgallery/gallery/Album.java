package org.goodgallery.gallery;

import lombok.Getter;
import org.goodgallery.gallery.properties.Properties;
import org.goodgallery.gallery.properties.PropertyKey;
import org.goodgallery.gallery.properties.SerializedProperties;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Getter
public final class Album extends GalleryItem {

  private static final PropertyKey<?>[] DEFAULT_KEYS = {
    Properties.NAME_KEY, Properties.CREATION_TIMESTAMP_KEY, Properties.PHOTOS_KEY
  };

  public Album(UUID uniqueId, SerializedProperties serializedProperties) {
    super(uniqueId, serializedProperties, DEFAULT_KEYS);
  }

  Album() {
    super(DEFAULT_KEYS);
  }

  public Collection<Photo> getPhotos() {
    return getProperties().getValue(Properties.PHOTOS_KEY).map(Collections::unmodifiableCollection).orElse(Collections.emptySet());
  }

}