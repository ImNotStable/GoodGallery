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

  <T> T getValue(PropertyKey<T> key);

  default <T> T getValueOrDefault(PropertyKey<T> key) {
    return getValueOrDefault(key, key.getDefaultValue(this));
  }

  default <T> T getValueOrDefault(PropertyKey<T> key, T defaultValue) {
    T value = getValue(key);
    return value != null ? value : defaultValue;
  }

  default <T, O> O getTransformedValue(PropertyKey<T> key, Transformer<T, O> transformer) {
    return getTransformedValueOrDefault(key, transformer, null);
  }

  default <T, O> O getTransformedValueOrDefault(PropertyKey<T> key, Function<T, O> transformer) {
    T value = getValueOrDefault(key);
    return transformer.apply(value);
  }

  default <T, O> O getTransformedValueOrDefault(PropertyKey<T> key, Transformer<T, O> transformer, O defaultValue) {
    T value = getValue(key);
    try {
      return value != null ? transformer.transform(value) : defaultValue;
    } catch (Throwable ignored) {
      return defaultValue;
    }
  }

}