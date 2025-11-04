package org.goodgallery.gallery.properties;

import java.util.function.Function;

public final class PropertyKey<T> {

  private final String id;
  private final Function<T,  byte[]> serializer;
  private final Function<byte[], T> deserializer;

  private Function<Properties, T> defaultProvider = _ -> null;

  /**
   * Create a PropertyKey with the given identifier and binary serializer/deserializer.
   *
   * @param id          the unique identifier for this property key
   * @param serializer  function that converts a value of type T into a byte array for storage
   * @param deserializer function that converts a byte array back into a value of type T
   */
  public PropertyKey(String id, Function<T, byte[]> serializer, Function<byte[], T> deserializer) {
    this.id = id;
    this.serializer = serializer;
    this.deserializer = deserializer;
  }

  /**
   * Convert the given value to its binary representation.
   *
   * @param value the value to serialize; if `null`, an empty byte array is returned
   * @return the serialized bytes for the value, or an empty byte array if the value is `null`
   */
  public byte[] serialize(T value) {
    if (value == null)
      return new byte[0];

    return serializer.apply(value);
  }

  /**
   * Convert the given byte array into the property's value.
   *
   * @param serializedData the byte array to deserialize (typically produced by {@link #serialize})
   * @return the deserialized value, or `null` if deserialization fails
   */
  public T deserialize(byte[] serializedData) {
    try {
      return deserializer.apply(serializedData);
    } catch (Throwable ignored) {
      return null;
    }
  }

  /**
   * Sets the default-value provider used to compute this key's default from a Properties object.
   *
   * @param defaultProvider function that accepts the surrounding Properties and returns a default value for this key (may return {@code null})
   * @return this PropertyKey instance for method chaining
   */
  public PropertyKey<T> defaultProvider(Function<Properties, T> defaultProvider) {
    this.defaultProvider = defaultProvider;
    return this;
  }

  /**
   * Compute the default value for this property key from the provided Properties.
   *
   * @param properties the Properties context used to derive the default value
   * @return the default value for this key, or `null` if the provider supplies no value
   */
  public T getDefaultValue(Properties properties) {
    return defaultProvider.apply(properties);
  }

  /**
   * Provide a string representation of this property key.
   *
   * @return the identifier of the property key
   */
  @Override
  public String toString() {
    return id;
  }

}