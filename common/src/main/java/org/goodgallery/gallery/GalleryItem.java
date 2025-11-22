package org.goodgallery.gallery;

import lombok.Getter;
import org.goodgallery.gallery.properties.Properties;
import org.goodgallery.gallery.properties.PropertiesImpl;
import org.goodgallery.gallery.properties.PropertyKey;
import org.goodgallery.gallery.properties.SerializedProperties;

import java.util.Optional;
import java.util.UUID;

public abstract sealed class GalleryItem permits Album, Group, Photo {

  @Getter
  private final UUID uniqueId;
  private final PropertiesImpl properties;

  protected GalleryItem(UUID uniqueId, SerializedProperties serializedProperties, PropertyKey<?>[] defaultKeys) {
    this.uniqueId = uniqueId;
    this.properties = new PropertiesImpl(serializedProperties, defaultKeys);
  }

  protected GalleryItem(PropertyKey<?>[] defaultKeys) {
    this(UUID.randomUUID(), new SerializedProperties(), defaultKeys);
  }

  public Properties<?> getProperties() {
    return properties;
  }

  public <T> Optional<T> getPropertyValue(PropertyKey<T> key) {
    return getProperties().getValueOrKeyDefault(key);
  }

  public Optional<String> getName() {
    return getPropertyValue(Properties.NAME_KEY);
  }

  @Override
  public String toString() {
    return uniqueId.toString();
  }

}