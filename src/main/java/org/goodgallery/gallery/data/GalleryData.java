package org.goodgallery.gallery.data;

import org.goodgallery.gallery.Album;
import org.goodgallery.gallery.Group;
import org.goodgallery.gallery.Photo;
import org.goodgallery.gallery.collections.AlbumCollection;
import org.goodgallery.gallery.collections.GroupCollection;
import org.goodgallery.gallery.collections.PhotoCollection;
import org.goodgallery.gallery.properties.PropertyHolder;
import org.goodgallery.gallery.properties.PropertyInstance;

public interface GalleryData {

  void loadGroups(GroupCollection groups);

  void addGroup(Group group);

  boolean hasGroup(Group group);

  void deleteGroup(Group group);

  void loadAlbums(AlbumCollection albums);

  void addAlbum(Album album);

  boolean hasAlbum(Album album);

  void deleteAlbum(Album album);

  void loadPhotos(PhotoCollection photos);

  void addPhoto(Photo photo);

  boolean hasPhoto(Photo photo);

  void deletePhoto(Photo photo);

  void updateProperty(PropertyHolder propertyHolder, PropertyInstance<?> property);

}
