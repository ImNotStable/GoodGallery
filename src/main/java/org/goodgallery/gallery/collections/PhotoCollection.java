package org.goodgallery.gallery.collections;

import org.goodgallery.gallery.Photo;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PhotoCollection {

  private final Map<UUID, Photo> photosByUUID;
  private final Map<Path, Photo> photosByPath;
  private final Map<String, Photo> photosByName;

  public PhotoCollection() {
    photosByUUID = new ConcurrentHashMap<>();
    photosByPath = new ConcurrentHashMap<>();
    photosByName = new ConcurrentHashMap<>();
  }

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
