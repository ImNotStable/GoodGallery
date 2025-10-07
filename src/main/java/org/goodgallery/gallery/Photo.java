package org.goodgallery.gallery;

import com.google.gson.JsonObject;
import lombok.Getter;
import org.goodgallery.gallery.properties.PropertyKey;

import java.nio.file.Path;

@Getter
public class Photo {

  private final Path path;
  private final Properties properties;

  Photo(Path path, JsonObject json) {
    this.path = path;
    this.properties = new PhotoProperties(this, json);
  }

  Photo(Path path) {
    this(path, null);
  }

  public String getFileName() {
    return path.getFileName().toString();
  }

  public <T> T getPropertyValue(PropertyKey<T> key) {
    T value = properties.getValue(key);
    return value != null ? value : key.getDefaultValue(this);
  }

  @Override
  public String toString() {
    return "%s".formatted(getFileName());
  }

  static class PhotoProperties extends Properties {

    private final Photo photo;

    PhotoProperties(Photo photo, JsonObject json) {
      this.photo = photo;
      register(json, NAME_KEY);
    }

    @Override
    protected <T> void register(JsonObject json, PropertyKey<T> key) {
      super.register(json, key, photo);
    }

  }

}
