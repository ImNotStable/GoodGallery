package org.goodgallery.gallery.properties;

import org.goodgallery.gallery.Properties;

import java.util.UUID;

public interface PropertyHolder {

  UUID getUniqueId();

  Properties getProperties();

  default <T> T getPropertyValue(PropertyKey<T> key) {
    Properties properties = getProperties();
    if (properties == null)
      return null;
    T value = properties.getValue(key);
    return value != null ? value : key.getDefaultValue(this);
  }

  default String getName() {
    return getPropertyValue(Properties.NAME_KEY);
  }

}
