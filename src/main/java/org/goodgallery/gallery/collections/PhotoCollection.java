package org.goodgallery.gallery.collections;

import org.goodgallery.gallery.Photo;
import org.goodgallery.gallery.properties.SerializedProperties;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PhotoCollection {

  private final Map<UUID, Photo> photosByUUID;
  private final Map<Path, Photo> photosByPath;
  private final Map<String, Photo> photosByName;

  /**
   * Creates an empty PhotoCollection with concurrent indices for lookup by UUID, file path, and name.
   */
  public PhotoCollection() {
    photosByUUID = new ConcurrentHashMap<>();
    photosByPath = new ConcurrentHashMap<>();
    photosByName = new ConcurrentHashMap<>();
  }

  /**
   * Creates a Photo from the given serialized properties and inserts it into the collection indexes.
   *
   * @param uniqueId the unique identifier to assign to the new Photo
   * @param serializedProperties serialized properties used to construct the Photo
   */
  public void createPhoto(UUID uniqueId, SerializedProperties serializedProperties) {
    add(Photo.create(uniqueId, serializedProperties));
  }

  /**
   * Adds a Photo to the collection and indexes it by UUID, filesystem path, and name.
   *
   * @param photo the Photo to add; its UUID, path, and name will be used as keys
   *              (existing mappings for those keys will be replaced)
   */
  public void add(Photo photo) {
    photosByUUID.put(photo.getUniqueId(), photo);
    photosByPath.put(photo.getPath(), photo);
    photosByName.put(photo.getName(), photo);
  }

  public boolean has(Photo photo) {
    return has(photo.getUniqueId());
  }

  public boolean has(UUID uniqueId) {
    return photosByUUID.containsKey(uniqueId);
  }

  public boolean has(Path path) {
    return photosByPath.containsKey(path);
  }

  public boolean has(String name) {
    return photosByName.containsKey(name);
  }

  public Collection<Photo> getPhotos() {
    return photosByUUID.values();
  }

  public Photo getPhoto(UUID uniqueId) {
    return photosByUUID.get(uniqueId);
  }

  public Photo getPhoto(Path path) {
    return photosByPath.get(path);
  }

  public Photo getPhoto(String name) {
    return photosByName.get(name);
  }

  public void remove(Photo photo) {
    photosByUUID.remove(photo.getUniqueId());
    photosByPath.remove(photo.getPath());
    photosByName.remove(photo.getName());
  }


}