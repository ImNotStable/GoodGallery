package org.goodgallery.gallery.properties;

import java.util.HashMap;
import java.util.Map;

public final class PropertiesImpl implements Properties {

  /**
   * Creates a PropertiesImpl initialized from the given serialized properties and the provided keys.
   *
   * @param serializedProperties the source of serialized values; if {@code null} an empty set of properties is used
   * @param keys                 property keys to register in the created PropertiesImpl
   * @return                     a PropertiesImpl populated from {@code serializedProperties} (or empty) and containing the provided keys
   */
  public static PropertiesImpl create(SerializedProperties serializedProperties, PropertyKey<?>... keys) {
    if (serializedProperties == null)
      serializedProperties = new SerializedProperties(new HashMap<>());
    return new PropertiesImpl(serializedProperties, keys);
  }

  private final Map<PropertyKey<?>, PropertyInstance<?>> properties;

  /**
   * Constructs a PropertiesImpl and registers the supplied property keys into its internal registry.
   *
   * Each key is initialized with a value from {@code serializedProperties} when present, or with the key's
   * default value otherwise.
   *
   * @param serializedProperties source of persisted property values used to initialize keys
   * @param keys                 the property keys to register and initialize
   */
  PropertiesImpl(SerializedProperties serializedProperties, PropertyKey<?>... keys) {
    this.properties = new HashMap<>();
    for (PropertyKey<?> key : keys)
      register(serializedProperties, key);
  }

  /**
   * Registers a property by loading its serialized value or the key's default and storing a new PropertyInstance in the internal map.
   *
   * @param serializedProperties source of persisted property values; may be empty
   * @param key the PropertyKey whose value to load and register
   */
  private <T> void register(SerializedProperties serializedProperties, PropertyKey<T> key) {
    T value = serializedProperties.getValueOrDefault(key, key.getDefaultValue(this));
    properties.put(key, new PropertyInstance<>(key, value));
  }

  /**
   * Get the PropertyInstance registered for the specified PropertyKey.
   *
   * @param key the property key to look up
   * @return the PropertyInstance for the key, or null if none is registered
   */
  @SuppressWarnings("unchecked")
  public <T> PropertyInstance<T> get(PropertyKey<T> key) {
    return (PropertyInstance<T>) properties.get(key);
  }

  /**
   * Set the value of the property identified by the given key.
   *
   * @param key   the property key identifying which property to update
   * @param value the new value for the property
   * @param <T>   the type of the property's value
   * @return      the PropertyInstance containing the updated value
   */
  public <T> PropertyInstance<T> set(PropertyKey<T> key, T value) {
    return get(key).value(value);
  }

  /**
   * Retrieve the current value for the specified property key.
   *
   * @param key the property key to read
   * @return the value associated with the specified key
   */
  public <T> T getValue(PropertyKey<T> key) {
    return get(key).value();
  }

}