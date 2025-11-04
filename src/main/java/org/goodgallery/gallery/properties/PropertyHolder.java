package org.goodgallery.gallery.properties;

public interface PropertyHolder {

  /**
 * Obtain the Properties object associated with this holder.
 *
 * @return the associated {@link Properties} instance, or {@code null} if no properties are present
 */
Properties getProperties();

  /**
   * Retrieve the value associated with the given typed property key, falling back to the key's default when the property is absent.
   *
   * @param <T> the type of the property value
   * @param key the typed key identifying the property
   * @return the value for the given key, the key's default value if the property is not present, or null if no Properties instance is available
   */
  default <T> T getPropertyValue(PropertyKey<T> key) {
    Properties properties = getProperties();
    if (properties == null)
      return null;
    return properties.getValueOrDefault(key);
  }

}