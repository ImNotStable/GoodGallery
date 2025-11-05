package org.goodgallery.gallery.data;

import org.goodgallery.gallery.Album;
import org.goodgallery.gallery.Group;
import org.goodgallery.gallery.Photo;
import org.goodgallery.gallery.properties.PropertyHolder;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractGalleryData implements GalleryData {

  private final Map<UUID, Photo> photosByUUID;
  private final Map<Path, Photo> photosByPath;
  private final Map<String, Photo> photosByName;

  private final Map<UUID, Album> albumsByUUID;
  private final Map<String, Album> albumsByName;

  private final Map<UUID, Group> groupsByUUID;
  private final Map<String, Group> groupsByName;

  protected AbstractGalleryData() {
    photosByUUID = new ConcurrentHashMap<>();
    photosByPath = new ConcurrentHashMap<>();
    photosByName = new ConcurrentHashMap<>();

    albumsByUUID = new ConcurrentHashMap<>();
    albumsByName = new ConcurrentHashMap<>();

    groupsByUUID = new ConcurrentHashMap<>();
    groupsByName = new ConcurrentHashMap<>();

    load();
  }

  protected abstract void load();

  protected abstract void insert(PropertyHolder propertyHolder);

  protected abstract void delete(PropertyHolder propertyHolder);

  public void add(Photo photo) {
    insert(photo);
    photosByUUID.put(photo.getUniqueId(), photo);
    photosByPath.put(photo.getPath(), photo);
    photosByName.put(photo.getName(), photo);
  }

  public boolean hasPhoto(Photo photo) {
    return hasPhoto(photo.getUniqueId());
  }

  public boolean hasPhoto(UUID uniqueId) {
    return photosByUUID.containsKey(uniqueId);
  }

  public boolean hasPhoto(Path path) {
    return photosByPath.containsKey(path);
  }

  public boolean hasPhoto(String name) {
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
    delete(photo);
    photosByUUID.remove(photo.getUniqueId());
    photosByPath.remove(photo.getPath());
    photosByName.remove(photo.getName());
  }

  public void add(Album album) {
    insert(album);
    albumsByUUID.put(album.getUniqueId(), album);
    albumsByName.put(album.getName(), album);
  }

  public boolean hasAlbum(Album album) {
    return hasAlbum(album.getUniqueId());
  }

  public boolean hasAlbum(UUID uniqueId) {
    return albumsByUUID.containsKey(uniqueId);
  }

  public boolean hasAlbum(String name) {
    return albumsByName.containsKey(name);
  }

  public Collection<Album> getAlbums() {
    return albumsByUUID.values();
  }

  public Album getAlbum(UUID uniqueId) {
    return albumsByUUID.get(uniqueId);
  }

  public Album getAlbum(String name) {
    return albumsByName.get(name);
  }

  public void remove(Album album) {
    delete(album);
    albumsByUUID.remove(album.getUniqueId());
    albumsByName.remove(album.getName());
  }

  public void add(Group group) {
    insert(group);
    groupsByUUID.put(group.getUniqueId(), group);
    groupsByName.put(group.getName(), group);
  }

  public boolean hasGroup(Group group) {
    return hasGroup(group.getUniqueId());
  }

  public boolean hasGroup(UUID uniqueId) {
    return groupsByUUID.containsKey(uniqueId);
  }

  public boolean hasGroup(String name) {
    return groupsByName.containsKey(name);
  }

  public Collection<Group> getGroups() {
    return groupsByUUID.values();
  }

  public Group getGroup(UUID uniqueId) {
    return groupsByUUID.get(uniqueId);
  }

  public Group getGroup(String name) {
    return groupsByName.get(name);
  }

  public void remove(Group group) {
    delete(group);
    groupsByUUID.remove(group.getUniqueId());
    groupsByName.remove(group.getName());
  }

  protected abstract void save();

}
