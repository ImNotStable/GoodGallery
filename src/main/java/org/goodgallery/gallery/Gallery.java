package org.goodgallery.gallery;

import com.google.common.base.Preconditions;
import lombok.Getter;
import org.goodgallery.gallery.data.GalleryData;
import org.goodgallery.gallery.data.JsonGalleryData;
import org.goodgallery.gallery.properties.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.function.Consumer;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public final class Gallery {

  @Getter
  private final Path path;
  private final GalleryData galleryData;

  Gallery(Path path) throws IOException {
    path = path.toAbsolutePath().normalize();
    if (!Files.isDirectory(path))
      Files.createDirectory(path);
    this.path = path;
    this.galleryData = new JsonGalleryData(path);
  }

  public Collection<Group> getGroups() {
    return Collections.unmodifiableCollection(galleryData.getGroups());
  }

  public Group getGroup(UUID uniqueId) {
    return galleryData.getGroup(uniqueId);
  }

  public Group getGroup(String name) {
    return galleryData.getGroup(name);
  }

  public boolean hasGroup(String name) {
    return galleryData.hasGroup(name);
  }

  public Group createGroup(String name) {
    Group group = new Group();
    galleryData.add(group);
    updateProperty(group, PropertiesImpl.NAME_KEY, name);
    return group;
  }

  public void deleteGroup(Group group) {
    galleryData.remove(group);
  }

  public void addAlbumToGroup(Album album, Group group) {
    Preconditions.checkState(galleryData.hasAlbum(album), "Album \"%s\" does not exist", album.getName());
    Preconditions.checkState(galleryData.hasGroup(group), "Group \"%s\" does not exist", group.getName());
    mutateProperty(group, Properties.ALBUMS_KEY, albums -> albums.add(album));
  }

  public void removeAlbumFromGroup(Album album, Group group) {
    Preconditions.checkState(galleryData.hasAlbum(album), "Album \"%s\" does not exist", album.getName());
    Preconditions.checkState(galleryData.hasGroup(group), "Group \"%s\" does not exist", group.getName());
    mutateProperty(group, Properties.ALBUMS_KEY, albums -> albums.remove(album));
  }

  public Collection<Album> getAlbums() {
    return Collections.unmodifiableCollection(galleryData.getAlbums());
  }

  public Album getAlbum(UUID uniqueId) {
    return galleryData.getAlbum(uniqueId);
  }

  public Album getAlbum(String name) {
    return galleryData.getAlbum(name);
  }

  public boolean hasAlbum(String name) {
    return galleryData.hasAlbum(name);
  }

  public Album createAlbum(String name) {
    Album album = new Album();
    galleryData.add(album);
    updateProperty(album, PropertiesImpl.NAME_KEY, name);
    return album;
  }

  public void deleteAlbum(Album album) {
    galleryData.remove(album);
  }

  public void addPhotoToAlbum(Photo photo, Album album) {
    Preconditions.checkState(galleryData.hasPhoto(photo), "Photo \"%s\" does not exist", photo.getName());
    Preconditions.checkState(galleryData.hasAlbum(album), "Album \"%s\" does not exist", album.getName());
    mutateProperty(album, Properties.PHOTOS_KEY, photos -> photos.add(photo));
  }

  public void removePhotoFromAlbum(Photo photo, Album album) {
    Preconditions.checkState(galleryData.hasPhoto(photo), "Photo \"%s\" does not exist", photo.getName());
    Preconditions.checkState(galleryData.hasAlbum(album), "Album \"%s\" does not exist", album.getName());
    mutateProperty(album, Properties.PHOTOS_KEY, photos -> photos.remove(photo));
  }

  public Collection<Photo> getPhotos() {
    return Collections.unmodifiableCollection(galleryData.getPhotos());
  }

  public Photo getPhoto(UUID uniqueId) {
    return galleryData.getPhoto(uniqueId);
  }

  public Photo getPhoto(Path path) {
    return galleryData.getPhoto(path);
  }

  public Photo getPhoto(String name) {
    return galleryData.getPhoto(name);
  }

  public boolean hasPhoto(String name) {
    return galleryData.hasPhoto(name);
  }

  public Photo copyPhoto(Path originalPath) throws IOException {
    if (!Files.exists(originalPath))
      throw new FileNotFoundException("Photo at \"%s\" does not exist".formatted(originalPath));

    Path newPath = path.resolve(originalPath.getFileName());

    if (Files.exists(newPath))
      throw new FileAlreadyExistsException("Photo at \"%s\" already exists".formatted(newPath));

    Files.copy(originalPath, newPath, StandardCopyOption.COPY_ATTRIBUTES);

    Photo photo = new Photo();
    galleryData.add(photo);
    updateProperty(photo, PropertiesImpl.PATH_KEY, newPath);

    return photo;
  }

  public Photo cutPhoto(Path originalPath) throws IOException {
    Photo photo = copyPhoto(originalPath);
    Files.deleteIfExists(originalPath);
    return photo;
  }

  public void deletePhoto(Photo photo) throws IOException {
    galleryData.remove(photo);
    Files.deleteIfExists(photo.getPropertyValue(PropertiesImpl.PATH_KEY));
  }

  public <T> void updateProperty(PropertyHolder propertyHolder, PropertyKey<T> key, T value) {
    PropertyInstance<T> property = ((PropertiesImpl) propertyHolder.getProperties()).get(key).value(value);
    galleryData.updateProperty(propertyHolder, property);
  }

  public <T> void mutateProperty(PropertyHolder propertyHolder, PropertyKey<T> key, Consumer<T> mutator) {
    PropertyInstance<T> property = ((PropertiesImpl) propertyHolder.getProperties()).get(key);
    mutator.accept(property.value());
    galleryData.updateProperty(propertyHolder, property);
  }

  public <T> T getPropertyValue(PropertyHolder propertyHolder, PropertyKey<T> key) {
    return propertyHolder.getPropertyValue(key);
  }

}