package org.goodgallery.gallery.properties;

import java.util.Optional;

public final class PropertyInstance<T> {

  private final PropertyKey<T> key;
  private T value;

  public PropertyInstance(PropertyKey<T> key, T value) {
    this.key = key;
    this.value = value;
  }

  public byte[] serialize() {
    return key.serialize(value);
  }

  public PropertyKey<T> key() {
    return key;
  }

  public Optional<T> value() {
    return Optional.ofNullable(value);
  }

  public PropertyInstance<T> value(T value) {
    this.value = value;
    return this;
  }

}