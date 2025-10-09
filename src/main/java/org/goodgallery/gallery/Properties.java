package org.goodgallery.gallery;

import com.google.gson.JsonObject;
import org.goodgallery.gallery.properties.PropertyHolder;
import org.goodgallery.gallery.properties.PropertyInstance;
import org.goodgallery.gallery.properties.PropertyKey;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

public abstract class Properties {

  public static final PropertyKey<Path> PATH_KEY = new PropertyKey<>("path",
    (property, json) -> json.addProperty(property.key().toString(), property.value().toString()),
    json -> Path.of(json.getAsJsonObject("path").getAsString())
  );
  public static final PropertyKey<String> NAME_KEY = new PropertyKey<>("name",
    (property, json) -> json.addProperty(property.key().toString(), property.value()),
    json -> json.getAsJsonObject("name").getAsString()
  ).defaultProvider(propertyHolder -> {
    Path path = propertyHolder.getPropertyValue(PATH_KEY);
    return path == null ? null : path.getFileName().toString();
  });
  public static final PropertyKey<Long> CREATION_TIMESTAMP_KEY = new PropertyKey<>("creation_timestamp",
    (property, json) -> json.addProperty(property.key().toString(), property.value()),
    json -> json.getAsJsonObject("creation_timestamp").getAsLong()
  ).defaultProvider(propertyHolder -> {
    Path path = propertyHolder.getPropertyValue(PATH_KEY);

    if (path == null)
      return System.currentTimeMillis();

    try {
      BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
      return attributes.creationTime().toInstant().toEpochMilli();
    } catch (Throwable ignored){
      return System.currentTimeMillis();
    }
  });

  private final Map<PropertyKey<?>, PropertyInstance<?>> properties;
  private final PropertyHolder propertyHolder;
  private final JsonObject json;

  protected Properties(PropertyHolder propertyHolder, JsonObject json) {
    this.properties = new HashMap<>();
    this.propertyHolder = propertyHolder;
    this.json = json;
  }

  protected <T> void register(PropertyKey<T> key) {
    register(key, key.getDefaultValue(propertyHolder));
  }

  private <T> void register(PropertyKey<T> key, T defaultValue) {
    T value = key.deserialize(json);

    if (value == null)
      value = defaultValue;

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
