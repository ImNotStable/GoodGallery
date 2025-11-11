package org.goodgallery.gallery.properties;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class PropertiesImpl implements Properties {

  private final Map<PropertyKey<?>, PropertyInstance<?>> properties;

  public PropertiesImpl(SerializedProperties serializedProperties, PropertyKey<?>... keys) {
    if (serializedProperties == null)
      serializedProperties = new SerializedProperties(new HashMap<>());

    this.properties = new HashMap<>();
    for (PropertyKey<?> key : keys)
      register(serializedProperties, key);
  }

  private <T> void register(SerializedProperties serializedProperties, PropertyKey<T> key) {
    serializedProperties.getValueOrKeyDefault(key).ifPresent(value -> properties.put(key, new PropertyInstance<>(key, value)));
  }

  @SuppressWarnings("unchecked")
  public <T> PropertyInstance<T> get(PropertyKey<T> key) {
    return (PropertyInstance<T>) properties.get(key);
  }

  public <T> Optional<T> getValue(PropertyKey<T> key) {
    return get(key).value();
  }

}