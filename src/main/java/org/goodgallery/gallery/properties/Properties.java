package org.goodgallery.gallery.properties;

import org.goodgallery.gallery.Album;
import org.goodgallery.gallery.GalleryInstance;
import org.goodgallery.gallery.Photo;
import org.goodgallery.gallery.util.Transformer;

import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

public interface Properties {

  PropertyKey<Path> PATH_KEY = new PropertyKey<>("path",
    path -> path.toString().getBytes(),
    data -> Path.of(new String(data))
  );

  PropertyKey<String> NAME_KEY = new PropertyKey<>("name",
    String::getBytes,
    String::new
  ).defaultProvider(
    properties -> properties.getTransformedValue(PATH_KEY, path -> path.getFileName().toString())
  );

  PropertyKey<Long> CREATION_TIMESTAMP_KEY = new PropertyKey<>("creation_timestamp",
    timestamp -> ByteBuffer.allocate(Long.BYTES).putLong(timestamp).array(),
    data -> ByteBuffer.wrap(data).getLong()
  ).defaultProvider(properties ->
    properties.getTransformedValueOrDefault(PATH_KEY,
      path -> Files.readAttributes(path, BasicFileAttributes.class).creationTime().toInstant().toEpochMilli(),
      System.currentTimeMillis()
    )
  );

  PropertyKey<Set<Photo>> PHOTOS_KEY = new PropertyKey<>("photos",
    photos -> {
      ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES * 2 * photos.size());
      for (Photo photo : photos) {
        UUID uniqueId = photo.getUniqueId();
        byteBuffer.putLong(uniqueId.getMostSignificantBits());
        byteBuffer.putLong(uniqueId.getLeastSignificantBits());
      }
      return byteBuffer.array();
    },
    data -> {
      ByteBuffer byteBuffer = ByteBuffer.wrap(data);
      Set<Photo> photos = new HashSet<>((data.length / (Long.BYTES * 2)) + 1, 1);

      while (byteBuffer.hasRemaining()) {
        UUID uniqueId = new UUID(byteBuffer.getLong(), byteBuffer.getLong());
        Photo photo = GalleryInstance.get().getPhoto(uniqueId);
        photos.add(photo);
      }

      return photos;
    }).defaultProvider(_ -> new HashSet<>());

  PropertyKey<Set<Album>> ALBUMS_KEY = new PropertyKey<>("albums",
    albums -> {
      ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES * 2 * albums.size());
      for (Album album : albums) {
        UUID uniqueId = album.getUniqueId();
        byteBuffer.putLong(uniqueId.getMostSignificantBits());
        byteBuffer.putLong(uniqueId.getLeastSignificantBits());
      }
      return byteBuffer.array();
    },
    data -> {
      ByteBuffer byteBuffer = ByteBuffer.wrap(data);
      Set<Album> albums = new HashSet<>((data.length / (Long.BYTES * 2)) + 1, 1);

      while (byteBuffer.hasRemaining()) {
        UUID uniqueId = new UUID(byteBuffer.getLong(), byteBuffer.getLong());
        Album album = GalleryInstance.get().getAlbum(uniqueId);
        albums.add(album);
      }

      return albums;
    }).defaultProvider(_ -> new HashSet<>());

  /**
 * Retrieve the value associated with a property key.
 *
 * @param key the property key to look up
 * @return the value associated with the key, or {@code null} if no value is present
 */
<T> T getValue(PropertyKey<T> key);

  /**
   * Retrieve the value associated with the given property key, falling back to the key's default value when absent.
   *
   * @param key the property key whose value to obtain
   * @return the value associated with {@code key}, or the default value provided by {@code key} when the stored value is null
   */
  default <T> T getValueOrDefault(PropertyKey<T> key) {
    return getValueOrDefault(key, key.getDefaultValue(this));
  }

  /**
   * Retrieve the value associated with the given property key, returning the provided default when no value is present.
   *
   * @param key the property key to look up
   * @param defaultValue the value to return when the property's value is null or absent
   * @return the property's value if non-null, otherwise `defaultValue`
   */
  default <T> T getValueOrDefault(PropertyKey<T> key, T defaultValue) {
    T value = getValue(key);
    return value != null ? value : defaultValue;
  }

  /**
   * Applies the given transformer to the value associated with the specified property key and returns the transformed result.
   *
   * @param <T> the type of the stored property value
   * @param <O> the type of the transformed result
   * @param key the property key whose value will be transformed
   * @param transformer the transformer to apply to the property's value
   * @return the transformed value, or `null` if the property is not present or if the transformation fails
   */
  default <T, O> O getTransformedValue(PropertyKey<T> key, Transformer<T, O> transformer) {
    return getTransformedValueOrDefault(key, transformer, null);
  }

  /**
   * Applies the given transformer to the property's value (using the key's default when the value is absent) and returns the transformed result.
   *
   * @param key the property key whose value will be obtained or defaulted
   * @param transformer function that maps the property's value to the desired result
   * @param <T> the type of the stored property value
   * @param <O> the type of the transformed result
   * @return the result of applying `transformer` to the property's value (or the key's default value)
   */
  default <T, O> O getTransformedValueOrDefault(PropertyKey<T> key, Function<T, O> transformer) {
    T value = getValueOrDefault(key);
    return transformer.apply(value);
  }

  /**
   * Apply a transformer to the property value associated with the given key, or return a fallback.
   *
   * @param <T> the property value type
   * @param <O> the result type produced by the transformer
   * @param key the property key whose value will be obtained
   * @param transformer a function that maps the property value to the desired result
   * @param defaultValue the value to return if the property is absent or if transformation fails
   * @return the transformed value when the property exists and transformation succeeds, otherwise `defaultValue`
   */
  default <T, O> O getTransformedValueOrDefault(PropertyKey<T> key, Transformer<T, O> transformer, O defaultValue) {
    T value = getValue(key);
    try {
      return value != null ? transformer.transform(value) : defaultValue;
    } catch (Throwable ignored) {
      return defaultValue;
    }
  }

}