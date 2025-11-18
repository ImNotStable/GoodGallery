package org.goodgallery.gallery.properties;

import org.goodgallery.gallery.Album;
import org.goodgallery.gallery.GalleryInstance;
import org.goodgallery.gallery.GalleryItem;
import org.goodgallery.gallery.Photo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

public interface Properties {

  PropertyKey<Path> PATH_KEY = new PropertyKey<>("path",
    path -> path.toString().getBytes(StandardCharsets.UTF_8),
    data -> Path.of(new String(data))
  );

  PropertyKey<String> NAME_KEY = new PropertyKey<>("name",
    name -> name.getBytes(StandardCharsets.UTF_8),
    data -> new String(data, StandardCharsets.UTF_8)
  ).defaultProvider(
    properties -> properties.getValue(PATH_KEY).map(Path::getFileName).map(Path::toString).orElse("unknown")
  );

  PropertyKey<Long> CREATION_TIMESTAMP_KEY = new PropertyKey<>("creation_timestamp",
    timestamp -> ByteBuffer.allocate(Long.BYTES).putLong(timestamp).array(),
    data -> ByteBuffer.wrap(data).getLong()
  ).defaultProvider(properties -> properties.getValue(PATH_KEY).map(
    path -> {
      try {
        return Files.readAttributes(path, BasicFileAttributes.class).creationTime().toInstant().toEpochMilli();
      } catch (IOException exception) {
        System.out.println("Failed to get creation time for path: " + path + ", using current time instead.");
        exception.printStackTrace(System.out);
        return System.currentTimeMillis();
      }
    }).orElse(System.currentTimeMillis())
  );

  PropertyKey<Set<Photo>> PHOTOS_KEY = new PropertyKey<>("photos",
    Properties::serializeGalleryItems,
    data -> deserializeGalleryItems(data, uniqueId -> GalleryInstance.get().getPhoto(uniqueId)))
    .defaultProvider(_ -> new HashSet<>());

  PropertyKey<Set<Album>> ALBUMS_KEY = new PropertyKey<>("albums",
    Properties::serializeGalleryItems,
    data -> deserializeGalleryItems(data, uniqueId -> GalleryInstance.get().getAlbum(uniqueId)))
    .defaultProvider(_ -> new HashSet<>());

  private static <T extends GalleryItem> byte[] serializeGalleryItems(Set<T> items) {
    ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES * 2 * items.size());
    for (T item : items) {
      UUID uniqueId = item.getUniqueId();
      byteBuffer.putLong(uniqueId.getMostSignificantBits());
      byteBuffer.putLong(uniqueId.getLeastSignificantBits());
    }
    return byteBuffer.array();
  }

  private static <T extends GalleryItem> Set<T> deserializeGalleryItems(byte[] data, Function<UUID, Optional<T>> deserializer) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(data);
    Set<T> items = new HashSet<>((data.length / (Long.BYTES * 2)));
    while (byteBuffer.hasRemaining())
      deserializer.apply(new UUID(byteBuffer.getLong(), byteBuffer.getLong())).ifPresent(items::add);
    return items;
  }

  <T> Optional<T> getValue(PropertyKey<T> key);

  default <T> Optional<T> getValueOrKeyDefault(PropertyKey<T> key) {
    return getValue(key).or(() -> key.getDefaultValue(this));
  }

}