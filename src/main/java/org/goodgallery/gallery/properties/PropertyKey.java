package org.goodgallery.gallery.properties;

import com.google.gson.JsonObject;
import org.goodgallery.gallery.Properties;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class PropertyKey<T> {

  private final String id;
  final BiConsumer<PropertyInstance<T>,  JsonObject> serializer;
  private final Function<JsonObject, T> deserializer;

  private Function<Properties, T> defaultProvider = _ -> null;

  public PropertyKey(String id, BiConsumer<PropertyInstance<T>, JsonObject> serializer, Function<JsonObject, T> deserializer) {
    this.id = id;
    this.serializer = serializer;
    this.deserializer = deserializer;
  }

  public T deserialize(JsonObject json) {
    try {
      return deserializer.apply(json);
    } catch (Throwable ignored) {
      return null;
    }
  }

  public PropertyKey<T> defaultProvider(Function<Properties, T> defaultProvider) {
    this.defaultProvider = defaultProvider;
    return this;
  }

  public T getDefaultValue(Properties properties) {
    return defaultProvider.apply(properties);
  }

  @Override
  public String toString() {
    return id;
  }

}
