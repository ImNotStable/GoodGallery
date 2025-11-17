package org.goodgallery.gallery.properties;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class PropertiesImpl implements Properties {

  private final Map<PropertyKey<?>, PropertyInstance<?>> properties;

  public PropertiesImpl(@NotNull SerializedProperties serializedProperties, PropertyKey<?> @NotNull ... keys) {
    this.properties = new HashMap<>();

    for (PropertyKey<?> key : keys)
      register(serializedProperties, key);
  }

  private <T> void register(SerializedProperties serializedProperties, PropertyKey<T> key) {
    serializedProperties.getValueOrKeyDefault(key).ifPresentOrElse(
      value -> properties.put(key, new PropertyInstance<>(key, value)),
      () -> properties.put(key, new PropertyInstance<>(key, null))
    );
  }

  public Collection<PropertyInstance<?>> all() {
    return properties.values();
  }

  public <T> PropertyInstance<T> get(PropertyKey<T> key) {
    PropertyInstance<?> instance = properties.get(key);
    if (instance != null)
      //noinspection unchecked
      return (PropertyInstance<T>) instance;
    PropertyInstance<T> typedInstance = new PropertyInstance<>(key, null);
    properties.put(key, typedInstance);
    return typedInstance;
  }

  public <T> Optional<T> getValue(PropertyKey<T> key) {
    return get(key).value();
  }

}