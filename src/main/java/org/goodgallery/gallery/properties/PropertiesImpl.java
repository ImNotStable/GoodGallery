package org.goodgallery.gallery.properties;

import java.util.HashMap;
import java.util.Map;

public final class PropertiesImpl implements Properties {

  public static PropertiesImpl create(SerializedProperties serializedProperties, PropertyKey<?>... keys) {
    if (serializedProperties == null)
      serializedProperties = new SerializedProperties(new HashMap<>());
    return new PropertiesImpl(serializedProperties, keys);
  }

  private final Map<PropertyKey<?>, PropertyInstance<?>> properties;

  PropertiesImpl(SerializedProperties serializedProperties, PropertyKey<?>... keys) {
    this.properties = new HashMap<>();
    for (PropertyKey<?> key : keys)
      register(serializedProperties, key);
  }

  private <T> void register(SerializedProperties serializedProperties, PropertyKey<T> key) {
    T value = serializedProperties.getValueOrDefault(key, key.getDefaultValue(this));
    properties.put(key, new PropertyInstance<>(key, value));
  }

  @SuppressWarnings("unchecked")
  public <T> PropertyInstance<T> get(PropertyKey<T> key) {
    return (PropertyInstance<T>) properties.get(key);
  }

  public <T> PropertyInstance<T> set(PropertyKey<T> key, T value) {
    return get(key).value(value);
  }

  public <T> T getValue(PropertyKey<T> key) {
    return get(key).value();
  }

}
