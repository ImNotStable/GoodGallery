package org.goodgallery.gallery;

import com.google.gson.JsonObject;
import lombok.Getter;
import org.goodgallery.gallery.properties.PropertyHolder;
import org.goodgallery.gallery.properties.PropertyKey;

import java.nio.file.Path;
import java.util.UUID;

import static org.goodgallery.gallery.Properties.PATH_KEY;

@Getter
public class Photo implements PropertyHolder {

  private static final PropertyKey<?>[] DEFAULT_KEYS = {
    Properties.PATH_KEY, Properties.NAME_KEY, Properties.CREATION_TIMESTAMP_KEY
  };

  public static Photo create(UUID uniqueId, JsonObject json) {
    return new Photo(uniqueId, json);
  }

  private final UUID uniqueId;
  private final Properties properties;

  Photo(UUID uniqueId, JsonObject json) {
    this.uniqueId = uniqueId;
    this.properties = Properties.create(json, DEFAULT_KEYS);
  }

  Photo(UUID uniqueId) {
    this(uniqueId, null);
  }

  Photo() {
    this(UUID.randomUUID());
  }

  public Path getPath() {
    return getPropertyValue(PATH_KEY);
  }

  public String getFileName() {
    return getPath().getFileName().toString();
  }

  @Override
  public String toString() {
    return uniqueId.toString();
  }

}
