package org.goodgallery.gallery.data;

import org.goodgallery.gallery.Album;
import org.goodgallery.gallery.GalleryItem;
import org.goodgallery.gallery.Group;
import org.goodgallery.gallery.Photo;
import org.goodgallery.gallery.properties.PropertyInstance;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface GalleryData {

  void add(Photo photo);

  default boolean hasPhoto(Photo photo) {
    return hasPhoto(photo.getUniqueId());
  }

  boolean hasPhoto(UUID uniqueId);

  boolean hasPhoto(Path path);

  boolean hasPhoto(String name);

  Collection<Photo> getPhotos();

  Optional<Photo> getPhoto(UUID uniqueId);

  Optional<Photo> getPhoto(Path path);

  Optional<Photo> getPhoto(String name);

  void remove(Photo photo);

  void add(Album album);

  default boolean hasAlbum(Album album) {
    return hasAlbum(album.getUniqueId());
  }

  boolean hasAlbum(UUID uniqueId);

  boolean hasAlbum(String name);

  Collection<Album> getAlbums();

  Optional<Album> getAlbum(UUID uniqueId);

  Optional<Album> getAlbum(String name);

  void remove(Album album);

  void add(Group group);

  default boolean hasGroup(Group group) {
    return hasGroup(group.getUniqueId());
  }

  boolean hasGroup(UUID uniqueId);

  boolean hasGroup(String name);

  Collection<Group> getGroups();

  Optional<Group> getGroup(UUID uniqueId);

  Optional<Group> getGroup(String name);

  void remove(Group group);

  void updateProperty(GalleryItem galleryItem, PropertyInstance<?> property);

}