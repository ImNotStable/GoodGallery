package org.goodgallery.gallery.properties;

import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.Getter;
import org.goodgallery.gallery.Album;
import org.goodgallery.gallery.Group;
import org.goodgallery.gallery.Photo;

import java.util.function.BiConsumer;
import java.util.function.Function;

@Getter
public class PropertyKey<T> {

  private final String key;
  private final Class<T> type;
  @Getter(AccessLevel.PACKAGE)
  private final BiConsumer<PropertyInstance<T>,  JsonObject> jsonAppender;
  private final Function<JsonObject, T> deserializer;

  // Default Providers
  private Function<Photo, T> photoDefProvider;
  private Function<Album, T> albumDefProvider;
  private Function<Group, T> groupDefProvider;

  public PropertyKey(String key, Class<T> type, BiConsumer<PropertyInstance<T>, JsonObject> jsonAppender, Function<JsonObject, T> deserializer) {
    this.key = key;
    this.type = type;
    this.jsonAppender = jsonAppender;
    this.deserializer = deserializer;
  }

  public PropertyKey<T> photoDefault(Function<Photo, T> defaultProvider) {
    this.photoDefProvider = defaultProvider;
    return this;
  }

  public PropertyKey<T> albumDefault(Function<Album, T> defaultProvider) {
    this.albumDefProvider = defaultProvider;
    return this;
  }

  public PropertyKey<T> groupDefault(Function<Group, T> defaultProvider) {
    this.groupDefProvider = defaultProvider;
    return this;
  }

  public T getDefaultValue(Photo photo) {
    return photoDefProvider.apply(photo);
  }

  public T getDefaultValue(Album album) {
    return albumDefProvider.apply(album);
  }

  public T getDefaultValue(Group group) {
    return groupDefProvider.apply(group);
  }

  public T deserialize(JsonObject json) {
    try {
      return deserializer.apply(json);
    } catch (Throwable ignored) {
      return null;
    }
  }

}
