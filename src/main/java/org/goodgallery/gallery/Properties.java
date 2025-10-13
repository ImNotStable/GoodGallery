package org.goodgallery.gallery;

import com.google.gson.JsonObject;
import org.goodgallery.gallery.properties.PropertyInstance;
import org.goodgallery.gallery.properties.PropertyKey;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

public final class Properties {

  public static final PropertyKey<Path> PATH_KEY = new PropertyKey<>("path",
    (property, json) -> json.addProperty(property.key().toString(), property.value().toString()),
    json -> Path.of(json.getAsJsonObject("path").getAsString())
  );
  public static final PropertyKey<String> NAME_KEY = new PropertyKey<>("name",
    (property, json) -> json.addProperty(property.key().toString(), property.value()),
    json -> json.getAsJsonObject("name").getAsString()
  ).defaultProvider(properties -> {
    Path path = properties.getValue(PATH_KEY);
    return path == null ? null : path.getFileName().toString();
  });
  public static final PropertyKey<Long> CREATION_TIMESTAMP_KEY = new PropertyKey<>("creation_timestamp",
    (property, json) -> json.addProperty(property.key().toString(), property.value()),
    json -> json.getAsJsonObject("creation_timestamp").getAsLong()
  ).defaultProvider(properties -> {
    Path path = properties.getValue(PATH_KEY);

    if (path == null)
      return System.currentTimeMillis();

    try {
      BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
      return attributes.creationTime().toInstant().toEpochMilli();
    } catch (Throwable ignored){
      return System.currentTimeMillis();
    }
  });

  public static Properties create(JsonObject json, PropertyKey<?>... keys) {
    return new Properties(json, keys);
  }

  private final Map<PropertyKey<?>, PropertyInstance<?>> properties;

  Properties(JsonObject json, PropertyKey<?>... keys) {
    this.properties = new HashMap<>();
    for (PropertyKey<?> key : keys)
      register(json, key);
  }

  private <T> void register(JsonObject json, PropertyKey<T> key) {
    T value = key.deserialize(json);

    if (value == null)
      value = key.getDefaultValue(this);

    properties.put(key, new PropertyInstance<>(key, value));
  }

  @SuppressWarnings("unchecked")
  <T> PropertyInstance<T> get(PropertyKey<T> key) {
    return (PropertyInstance<T>) properties.get(key);
  }

  public <T> T getValue(PropertyKey<T> key) {
    return get(key).value();
  }

  <T> PropertyInstance<T> set(PropertyKey<T> key, T value) {
    return get(key).value(value);
  }

}
