package org.goodgallery.gallery.data;

import org.goodgallery.gallery.Album;
import org.goodgallery.gallery.Group;
import org.goodgallery.gallery.Photo;
import org.goodgallery.gallery.collections.AlbumCollection;
import org.goodgallery.gallery.collections.GroupCollection;
import org.goodgallery.gallery.collections.PhotoCollection;
import org.goodgallery.gallery.properties.PropertyHolder;
import org.goodgallery.gallery.properties.PropertyInstance;
import org.jetbrains.annotations.Nullable;

public class SQLiteGalleryData extends AbstractGalleryData {

  @Override
  protected void save() {

  }

  @Override
  public void loadGroups(GroupCollection groups, PhotoCollection photos) {

  }

  @Override
  public void addGroup(Group group) {

  }

  @Override
  public boolean hasGroup(Group group) {
    return false;
  }

  @Override
  public void deleteGroup(Group group) {

  }

  @Override
  public void loadAlbums(AlbumCollection albums, PhotoCollection photos) {

  }

  @Override
  public void addAlbum(Album album, Photo... photos) {

  }

  @Override
  public void moveAlbum(Album album, @Nullable Group group) {

  }

  @Override
  public void addPhotoToAlbum(Photo photo, Album album) {

  }

  @Override
  public void removePhotoFromAlbum(Photo photo, Album album) {

  }

  @Override
  public boolean hasAlbum(Album album) {
    return false;
  }

  @Override
  public void deleteAlbum(Album album) {

  }

  @Override
  public void loadPhotos(PhotoCollection photos) {

  }

  @Override
  public void addPhoto(Photo photo) {

  }

  @Override
  public boolean hasPhoto(Photo photo) {
    return false;
  }

  @Override
  public void deletePhoto(Photo photo) {

  }

  @Override
  public void updateProperty(PropertyHolder propertyHolder, PropertyInstance<?> property) {

  }
}
