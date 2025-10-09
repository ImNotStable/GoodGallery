package org.goodgallery.gallery;

import com.google.gson.JsonObject;
import lombok.Getter;
import org.goodgallery.gallery.properties.PropertyHolder;

import java.nio.file.Path;
import java.util.UUID;

@Getter
public class Photo implements PropertyHolder {

  public static Photo create(UUID uniqueId, JsonObject json) {
    return new Photo(uniqueId, json);
  }

  private final UUID uniqueId;
  private final Properties properties;

  Photo(UUID uniqueId, JsonObject json) {
    this.uniqueId = uniqueId;
    this.properties = new PhotoProperties(this, json);
  }

  Photo(UUID uniqueId) {
    this(uniqueId, null);
  }

  Photo() {
    this(UUID.randomUUID());
  }

  public Path getPath() {
    return getPropertyValue(Properties.PATH_KEY);
  }

  public String getFileName() {
    return getPath().getFileName().toString();
  }

  @Override
  public String toString() {
    return uniqueId.toString();
  }

  static class PhotoProperties extends Properties {

    PhotoProperties(Photo photo, JsonObject json) {
      super(photo, json);
      register(PATH_KEY);
      register(NAME_KEY);
      register(CREATION_TIMESTAMP_KEY);
    }

  }

}
