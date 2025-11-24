package org.goodgallery.gallery.data;

import org.goodgallery.gallery.Album;
import org.goodgallery.gallery.GalleryItem;
import org.goodgallery.gallery.Group;
import org.goodgallery.gallery.Photo;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractGalleryData implements GalleryData {

  protected final Path path;

  protected final Map<UUID, Photo> photosByUUID;
  protected final Map<UUID, Album> albumsByUUID;
  protected final Map<UUID, Group> groupsByUUID;

  protected AbstractGalleryData(Path path) {
    this.path = path;
    this.photosByUUID = new ConcurrentHashMap<>();
    this.albumsByUUID = new ConcurrentHashMap<>();
    this.groupsByUUID = new ConcurrentHashMap<>();
  }

  public abstract void load();

  protected abstract void insert(GalleryItem galleryItem);

  protected abstract void delete(GalleryItem galleryItem);

  public void add(Photo photo) {
    insert(photo);
    photosByUUID.put(photo.getUniqueId(), photo);
  }

  public boolean hasPhoto(Photo photo) {
    return hasPhoto(photo.getUniqueId());
  }

  public boolean hasPhoto(UUID uniqueId) {
    return photosByUUID.containsKey(uniqueId);
  }

  public boolean hasPhoto(Path path) {
    return getPhoto(path).isPresent();
  }

  public boolean hasPhoto(String name) {
    return getPhoto(name).isPresent();
  }

  public Collection<Photo> getPhotos() {
    return photosByUUID.values();
  }

  public Optional<Photo> getPhoto(UUID uniqueId) {
    return Optional.ofNullable(photosByUUID.get(uniqueId));
  }

  public Optional<Photo> getPhoto(Path path) {
    return photosByUUID.values().stream().filter(photo -> photo.getPath().map(path::equals).orElse(false)).findFirst();
  }

  public Optional<Photo> getPhoto(String name) {
    return photosByUUID.values().stream().filter(photo -> photo.getName().map(name::equals).orElse(false)).findFirst();
  }

  public void remove(Photo photo) {
    delete(photo);
    photosByUUID.remove(photo.getUniqueId());
  }

  public void add(Album album) {
    insert(album);
    albumsByUUID.put(album.getUniqueId(), album);
  }

  public boolean hasAlbum(Album album) {
    return hasAlbum(album.getUniqueId());
  }

  public boolean hasAlbum(UUID uniqueId) {
    return albumsByUUID.containsKey(uniqueId);
  }

  public boolean hasAlbum(String name) {
    return getAlbum(name).isPresent();
  }

  public Collection<Album> getAlbums() {
    return albumsByUUID.values();
  }

  public Optional<Album> getAlbum(UUID uniqueId) {
    return Optional.ofNullable(albumsByUUID.get(uniqueId));
  }

  public Optional<Album> getAlbum(String name) {
    return albumsByUUID.values().stream().filter(album -> album.getName().map(name::equals).orElse(false)).findFirst();
  }

  public void remove(Album album) {
    delete(album);
    albumsByUUID.remove(album.getUniqueId());
  }

  public void add(Group group) {
    insert(group);
    groupsByUUID.put(group.getUniqueId(), group);
  }

  public boolean hasGroup(Group group) {
    return hasGroup(group.getUniqueId());
  }

  public boolean hasGroup(UUID uniqueId) {
    return groupsByUUID.containsKey(uniqueId);
  }

  public boolean hasGroup(String name) {
    return getGroup(name).isPresent();
  }

  public Collection<Group> getGroups() {
    return groupsByUUID.values();
  }

  public Optional<Group> getGroup(UUID uniqueId) {
    return Optional.ofNullable(groupsByUUID.get(uniqueId));
  }

  public Optional<Group> getGroup(String name) {
    return groupsByUUID.values().stream().filter(group -> group.getName().map(name::equals).orElse(false)).findFirst();
  }

  public void remove(Group group) {
    delete(group);
    groupsByUUID.remove(group.getUniqueId());
  }

}
