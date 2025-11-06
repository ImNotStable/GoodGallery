package org.goodgallery.gallery;

import lombok.Getter;
import org.goodgallery.gallery.properties.Properties;
import org.goodgallery.gallery.properties.PropertiesImpl;
import org.goodgallery.gallery.properties.PropertyHolder;
import org.goodgallery.gallery.properties.PropertyKey;
import org.goodgallery.gallery.properties.SerializedProperties;

import java.util.UUID;

public abstract class GalleryItem implements PropertyHolder {

  @Getter
  private final UUID uniqueId;
  private final PropertiesImpl properties;

  protected GalleryItem(UUID uniqueId, SerializedProperties serializedProperties, PropertyKey<?>[] defaultKeys) {
    this.uniqueId = uniqueId;
    this.properties = new PropertiesImpl(serializedProperties, defaultKeys);
  }

  protected GalleryItem(PropertyKey<?>[] defaultKeys) {
    this(UUID.randomUUID(), null, defaultKeys);
  }

  public Properties getProperties() {
    return properties;
  }

  public String getName() {
    return getPropertyValue(Properties.NAME_KEY).orElse("");
  }

  @Override
  public String toString() {
    return uniqueId.toString();
  }

}