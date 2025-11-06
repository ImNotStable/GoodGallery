package org.goodgallery.gallery.properties;

import java.util.Optional;

public interface PropertyHolder {

  Properties getProperties();

  default <T> Optional<T> getPropertyValue(PropertyKey<T> key) {
    Properties properties = getProperties();
    if (properties == null)
      return Optional.empty();
    return properties.getValueOrKeyDefault(key);
  }

}