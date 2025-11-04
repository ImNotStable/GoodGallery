package org.goodgallery.gallery;

import lombok.Getter;
import org.goodgallery.gallery.properties.Properties;
import org.goodgallery.gallery.properties.PropertyKey;
import org.goodgallery.gallery.properties.SerializedProperties;

import java.nio.file.Path;
import java.util.UUID;

import static org.goodgallery.gallery.properties.PropertiesImpl.PATH_KEY;

@Getter
public final class Photo extends GalleryItem {

  private static final PropertyKey<?>[] DEFAULT_KEYS = {
    Properties.PATH_KEY, Properties.NAME_KEY, Properties.CREATION_TIMESTAMP_KEY
  };

  /**
   * Create a Photo with the given unique identifier and serialized properties.
   *
   * @param uniqueId             the unique identifier to assign to the Photo
   * @param serializedProperties the serialized properties used to initialize the Photo
   * @return                      the created Photo initialized with the provided identifier and properties
   */
  public static Photo create(UUID uniqueId, SerializedProperties serializedProperties) {
    return new Photo(uniqueId, serializedProperties);
  }

  /**
   * Constructs a Photo initialized with the given unique identifier and serialized properties.
   *
   * @param uniqueId the UUID that uniquely identifies this photo
   * @param serializedProperties the serialized properties used to initialize the photo's state
   */
  Photo(UUID uniqueId, SerializedProperties serializedProperties) {
    super(uniqueId, serializedProperties, DEFAULT_KEYS);
  }

  /**
   * Constructs a Photo initialized with the default property keys.
   */
  Photo() {
    super(DEFAULT_KEYS);
  }

  /**
   * Retrieves the filesystem path of this photo.
   *
   * @return the Path representing the photo's filesystem location.
   */
  public Path getPath() {
    return getPropertyValue(PATH_KEY);
  }

  /**
   * Returns the file-name portion of this photo's path.
   *
   * @return the file name portion of the path as a String
   */
  public String getFileName() {
    return getPath().getFileName().toString();
  }

}