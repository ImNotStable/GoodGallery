package org.goodgallery.gallery.properties;

import com.google.gson.JsonObject;

public final class PropertyInstance<T> {

  private final PropertyKey<T> key;
  private T value;

  public PropertyInstance(PropertyKey<T> key, T value) {
    this.key = key;
    this.value = value;
  }

  public void appendJson(JsonObject json) {
    if (value == null)
      return;
    key.serializer.accept(this, json);
  }

  public PropertyKey<T> key() {
    return key;
  }

  public T value() {
    return value;
  }

  public PropertyInstance<T> value(T value) {
    this.value = value;
    return this;
  }

}
