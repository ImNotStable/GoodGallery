package org.goodgallery.gallery.data;

import org.goodgallery.gallery.Album;
import org.goodgallery.gallery.Group;
import org.goodgallery.gallery.Photo;
import org.goodgallery.gallery.properties.PropertyHolder;
import org.goodgallery.gallery.properties.PropertyInstance;
import org.goodgallery.gallery.properties.SerializedProperties;

import java.nio.file.Path;
import java.util.Collection;
import java.util.UUID;

public interface GalleryData {

  default void createPhoto(UUID uniqueId, SerializedProperties serializedProperties) {
    add(new Photo(uniqueId, serializedProperties));
  }

  void add(Photo photo);

  default boolean hasPhoto(Photo photo) {
    return hasPhoto(photo.getUniqueId());
  }

  boolean hasPhoto(UUID uniqueId);

  boolean hasPhoto(Path path);

  boolean hasPhoto(String name);

  Collection<Photo> getPhotos();

  Photo getPhoto(UUID uniqueId);

  Photo getPhoto(Path path);

  Photo getPhoto(String name);

  void remove(Photo photo);

  default void createAlbum(UUID uniqueId, SerializedProperties serializedProperties) {
    add(new Album(uniqueId, serializedProperties));
  }

  void add(Album album);

  default boolean hasAlbum(Album album) {
    return hasAlbum(album.getUniqueId());
  }

  boolean hasAlbum(UUID uniqueId);

  boolean hasAlbum(String name);

  Collection<Album> getAlbums();

  Album getAlbum(UUID uniqueId);

  Album getAlbum(String name);

  void remove(Album album);

  default void createGroup(UUID uniqueId, SerializedProperties serializedProperties) {
    add(new Group(uniqueId, serializedProperties));
  }

  void add(Group group);

  default boolean hasGroup(Group group) {
    return hasGroup(group.getUniqueId());
  }

  boolean hasGroup(UUID uniqueId);

  boolean hasGroup(String name);

  Collection<Group> getGroups();

  Group getGroup(UUID uniqueId);

  Group getGroup(String name);

  void remove(Group group);

  void updateProperty(PropertyHolder propertyHolder, PropertyInstance<?> property);

}