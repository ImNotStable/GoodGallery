package org.goodgallery.gallery;

import lombok.Getter;
import org.goodgallery.gallery.properties.Properties;
import org.goodgallery.gallery.properties.PropertyKey;
import org.goodgallery.gallery.properties.SerializedProperties;

import java.nio.file.Path;
import java.util.UUID;

import static org.goodgallery.gallery.properties.PropertiesImpl.PATH_KEY;

@Getter
public final class Photo extends GalleryItem {

  private static final PropertyKey<?>[] DEFAULT_KEYS = {
    Properties.PATH_KEY, Properties.NAME_KEY, Properties.CREATION_TIMESTAMP_KEY
  };

  public static Photo create(UUID uniqueId, SerializedProperties serializedProperties) {
    return new Photo(uniqueId, serializedProperties);
  }

  Photo(UUID uniqueId, SerializedProperties serializedProperties) {
    super(uniqueId, serializedProperties, DEFAULT_KEYS);
  }

  Photo() {
    super(DEFAULT_KEYS);
  }

  public Path getPath() {
    return getPropertyValue(PATH_KEY);
  }

  public String getFileName() {
    return getPath().getFileName().toString();
  }

}
