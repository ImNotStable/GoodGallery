package org.goodgallery.gallery;

import com.google.gson.JsonObject;
import org.goodgallery.gallery.properties.PropertyInstance;
import org.goodgallery.gallery.properties.PropertyKey;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class Properties {

  public static final PropertyKey<String> NAME_KEY = new PropertyKey<>("name", String.class,
    (property, json) -> json.addProperty(property.key().getKey(), property.value()),
    json -> json.getAsJsonObject("name").getAsString()
  ).photoDefault(Photo::getFileName);
  public static final PropertyKey<Long> CREATION_TIMESTAMP_KEY = new PropertyKey<>("creation_timestamp", Long.class,
    (property, json) -> json.addProperty(property.key().getKey(), property.value()),
    json -> json.getAsJsonObject("creation_timestamp").getAsLong()
  ).photoDefault(_ -> System.currentTimeMillis())
    .albumDefault(_ -> System.currentTimeMillis())
    .groupDefault(_ -> System.currentTimeMillis());

  private final Map<PropertyKey<?>, PropertyInstance<?>> properties = new HashMap<>();

  protected abstract <T> void register(JsonObject json, PropertyKey<T> key);

  protected <T> void register(JsonObject json, PropertyKey<T> key, Photo photo) {
    register(json, key, key.getDefaultValue(photo));
  }

  protected <T> void register(JsonObject json, PropertyKey<T> key, Album album) {
    register(json, key, key.getDefaultValue(album));
  }

  protected <T> void register(JsonObject json, PropertyKey<T> key, Group group) {
    register(json, key, key.getDefaultValue(group));
  }

  private <T> void register(JsonObject json, PropertyKey<T> key, T defaultValue) {
    T value = key.deserialize(json);

    if (value == null)
      value = defaultValue;

    properties.put(key, new PropertyInstance<>(key, value));
  }

  Collection<PropertyInstance<?>> getProperties() {
    return properties.values();
  }

  <T> PropertyInstance<T> get(PropertyKey<T> key) {
    return (PropertyInstance<T>) properties.get(key);
  }

  <T> T getValue(PropertyKey<T> key) {
    return get(key).value();
  }

  <T> PropertyInstance<T> set(PropertyKey<T> key, T value) {
    return get(key).value(value);
  }

}
