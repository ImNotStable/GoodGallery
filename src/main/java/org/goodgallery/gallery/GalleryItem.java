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

  /**
   * Creates a GalleryItem with the given unique identifier and property state.
   *
   * @param uniqueId the UUID to assign to this item
   * @param serializedProperties serialized properties to restore into the item's PropertiesImpl, or {@code null} to initialize with defaults
   * @param defaultKeys property keys that should be present by default when creating the properties
   */
  protected GalleryItem(UUID uniqueId, SerializedProperties serializedProperties, PropertyKey<?>[] defaultKeys) {
    this.uniqueId = uniqueId;
    this.properties = PropertiesImpl.create(serializedProperties, defaultKeys);
  }

  /**
   * Create a GalleryItem with a newly generated UUID and no serialized properties.
   *
   * @param defaultKeys default property keys to initialize the item's properties; may be null
   */
  protected GalleryItem(PropertyKey<?>[] defaultKeys) {
    this(UUID.randomUUID(), null, defaultKeys);
  }

  /**
   * Accesses the properties container for this gallery item.
   *
   * @return the Properties instance that holds this item's property values
   */
  public Properties getProperties() {
    return properties;
  }

  /**
   * Retrieves the item's name stored in its properties.
   *
   * @return the name assigned to this gallery item, or null if no name is set
   */
  public String getName() {
    return getPropertyValue(PropertiesImpl.NAME_KEY);
  }

  /**
   * Return the UUID string representation of this GalleryItem.
   *
   * @return the item's uniqueId as a string
   */
  @Override
  public String toString() {
    return uniqueId.toString();
  }

}