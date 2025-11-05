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

  public Group(UUID uniqueId, SerializedProperties serializedProperties) {
    super(uniqueId, serializedProperties, DEFAULT_KEYS);
  }

  Group() {
    super(DEFAULT_KEYS);
  }

  public Collection<Album> getAlbums() {
    return Collections.unmodifiableCollection(getProperties().getValue(Properties.ALBUMS_KEY));
  }

}