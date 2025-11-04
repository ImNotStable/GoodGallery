package org.goodgallery.gallery.properties;

public final class PropertyInstance<T> {

  private final PropertyKey<T> key;
  private T value;

  /**
   * Creates a PropertyInstance that holds the given property key and its associated value.
   *
   * @param key   the PropertyKey that identifies this property
   * @param value the value for the property
   */
  public PropertyInstance(PropertyKey<T> key, T value) {
    this.key = key;
    this.value = value;
  }

  /**
   * Produce a byte array representation of this instance's value using the property's key serializer.
   *
   * @return a byte array containing the serialized representation of the property's current value
   */
  public byte[] serialize() {
    return key.serialize(value);
  }

  /**
   * Retrieves the property key associated with this instance.
   *
   * @return the PropertyKey for this instance
   */
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