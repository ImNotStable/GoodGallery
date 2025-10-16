package org.goodgallery.gallery.properties;

import org.goodgallery.gallery.util.Transformer;
import org.jetbrains.annotations.ApiStatus;

import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

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

  <T> T getValue(PropertyKey<T> key);

  default <T> T getValueOrDefault(PropertyKey<T> key, T defaultValue) {
    T value = getValue(key);
    return value != null ? value : defaultValue;
  }

  default <T, O> O getTransformedValue(PropertyKey<T> key, Transformer<T, O> transformer) {
    return getTransformedValueOrDefault(key, transformer, null);
  }

  default <T, O> O getTransformedValueOrDefault(PropertyKey<T> key, Transformer<T, O> transformer, O defaultValue) {
    T value = getValue(key);
    try {
      return value != null ? transformer.transform(value) : defaultValue;
    } catch (Throwable e) {
      return null;
    }
  }

}
