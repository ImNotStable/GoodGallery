package org.goodgallery.gallery.properties;

public interface PropertyHolder {

  Properties getProperties();

  default <T> T getPropertyValue(PropertyKey<T> key) {
    Properties properties = getProperties();
    if (properties == null)
      return null;
    return properties.getValueOrDefault(key);
  }

}