package org.goodgallery.gallery.properties;

import java.util.Optional;
import java.util.function.Function;

public final class PropertyKey<T> {

  private final String id;
  private final Function<T,  byte[]> serializer;
  private final Function<byte[], T> deserializer;

  private Function<Properties, T> defaultProvider = _ -> null;

  public PropertyKey(String id, Function<T, byte[]> serializer, Function<byte[], T> deserializer) {
    this.id = id;
    this.serializer = serializer;
    this.deserializer = deserializer;
  }

  public byte[] serialize(T value) {
    if (value == null)
      return new byte[0];

    return serializer.apply(value);
  }

  public T deserialize(byte[] serializedData) {
    return deserializer.apply(serializedData);
  }

  public PropertyKey<T> defaultProvider(Function<Properties, T> defaultProvider) {
    this.defaultProvider = defaultProvider;
    return this;
  }

  public Optional<T> getDefaultValue(Properties properties) {
    return Optional.ofNullable(defaultProvider.apply(properties));
  }

  @Override
  public String toString() {
    return id;
  }

}