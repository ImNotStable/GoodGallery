package org.goodgallery.gallery.properties;

public final class PropertyInstance<T> {

  private final PropertyKey<T> key;
  private T value;

  /**
   * Creates a new PropertyInstance with the specified property key and initial value.
   *
   * @param key   the PropertyKey that identifies this property
   * @param value the initial value for the property
   */
  public PropertyInstance(PropertyKey<T> key, T value) {
    this.key = key;
    this.value = value;
  }

  /**
   * Produces a serialized representation of this instance's value using the associated property's serializer.
   *
   * @return a byte[] containing the serialized representation of the current property value
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

  /**
   * Set the property's value and return this instance.
   *
   * @param value the new property value to store
   * @return this PropertyInstance instance with its value set to the provided value
   */
  public PropertyInstance<T> value(T value) {
    this.value = value;
    return this;
  }

}