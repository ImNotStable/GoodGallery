package org.goodgallery.gallery;

import com.google.common.base.Preconditions;
import lombok.Getter;
import org.goodgallery.gallery.collections.AlbumCollection;
import org.goodgallery.gallery.collections.GroupCollection;
import org.goodgallery.gallery.collections.PhotoCollection;
import org.goodgallery.gallery.data.GalleryData;
import org.goodgallery.gallery.data.JsonGalleryData;
import org.goodgallery.gallery.properties.Properties;
import org.goodgallery.gallery.properties.PropertiesImpl;
import org.goodgallery.gallery.properties.PropertyHolder;
import org.goodgallery.gallery.properties.PropertyInstance;
import org.goodgallery.gallery.properties.PropertyKey;

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

  private final PhotoCollection photos;
  private final AlbumCollection albums;
  private final GroupCollection groups;

  Gallery(Path path) throws IOException {
    path = path.toAbsolutePath().normalize();
    if (!Files.isDirectory(path))
      Files.createDirectory(path);
    this.galleryData = JsonGalleryData.create(path);
    this.path = path;

    this.photos = new PhotoCollection();
    galleryData.loadPhotos(photos);
    this.albums = new AlbumCollection();
    galleryData.loadAlbums(albums);
    this.groups = new GroupCollection();
    galleryData.loadGroups(groups);
  }

  public Collection<Group> getGroups() {
    return Collections.unmodifiableCollection(groups.getGroups());
  }

  public Group getGroup(UUID uniqueId) {
    return groups.getGroup(uniqueId);
  }

  public Group getGroup(String name) {
    return groups.getGroup(name);
  }

  public boolean hasGroup(String name) {
    return groups.has(name);
  }

  public Group createGroup(String name) {
    Group group = new Group();
    galleryData.add(group);
    updateProperty(group, PropertiesImpl.NAME_KEY, name);
    groups.add(group);
    return group;
  }

  public void deleteGroup(Group group) {
    galleryData.delete(group);
    groups.remove(group);
  }

  public void addAlbumToGroup(Album album, Group group) {
    Preconditions.checkState(albums.has(album), "Album \"%s\" does not exist", album.getName());
    Preconditions.checkState(groups.has(group), "Group \"%s\" does not exist", group.getName());
    mutateProperty(group, Properties.ALBUMS_KEY, albums -> albums.add(album));
  }

  public void removeAlbumFromGroup(Album album, Group group) {
    Preconditions.checkState(albums.has(album), "Album \"%s\" does not exist", album.getName());
    Preconditions.checkState(groups.has(group), "Group \"%s\" does not exist", group.getName());
    mutateProperty(group, Properties.ALBUMS_KEY, albums -> albums.remove(album));
  }

  public Collection<Album> getAlbums() {
    return Collections.unmodifiableCollection(albums.getAlbums());
  }

  public Album getAlbum(UUID uniqueId) {
    return albums.getAlbum(uniqueId);
  }

  public Album getAlbum(String name) {
    return albums.getAlbum(name);
  }

  public boolean hasAlbum(String name) {
    return albums.has(name);
  }

  public Album createAlbum(String name) {
    Album album = new Album();
    galleryData.add(album);
    updateProperty(album, PropertiesImpl.NAME_KEY, name);
    albums.add(album);
    return album;
  }

  public void deleteAlbum(Album album) {
    galleryData.delete(album);
    albums.remove(album);
  }

  public void addPhotoToAlbum(Photo photo, Album album) {
    Preconditions.checkState(photos.has(photo), "Photo \"%s\" does not exist", photo.getName());
    Preconditions.checkState(albums.has(album), "Album \"%s\" does not exist", album.getName());
    mutateProperty(album, Properties.PHOTOS_KEY, photos -> photos.add(photo));
  }

  public void removePhotoFromAlbum(Photo photo, Album album) {
    Preconditions.checkState(photos.has(photo), "Photo \"%s\" does not exist", photo.getName());
    Preconditions.checkState(albums.has(album), "Album \"%s\" does not exist", album.getName());
    mutateProperty(album, Properties.PHOTOS_KEY, photos -> photos.remove(photo));
  }

  public Collection<Photo> getPhotos() {
    return Collections.unmodifiableCollection(photos.getPhotos());
  }

  public Photo getPhoto(UUID uniqueId) {
    return photos.getPhoto(uniqueId);
  }

  public Photo getPhoto(Path path) {
    return photos.getPhoto(path);
  }

  public Photo getPhoto(String name) {
    return photos.getPhoto(name);
  }

  public boolean hasPhoto(String name) {
    return photos.has(name);
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
    photos.add(photo);

    return photo;
  }

  public Photo cutPhoto(Path originalPath) throws IOException {
    Photo photo = copyPhoto(originalPath);
    Files.deleteIfExists(originalPath);
    return photo;
  }

  public void deletePhoto(Photo photo) throws IOException {
    galleryData.delete(photo);
    photos.remove(photo);
    Files.deleteIfExists(photo.getPropertyValue(PropertiesImpl.PATH_KEY));
  }

  public <T> void updateProperty(PropertyHolder propertyHolder, PropertyKey<T> key, T value) {
    PropertyInstance<T> property = ((PropertiesImpl) propertyHolder.getProperties()).set(key, value);
    galleryData.updateProperty(propertyHolder, property);
  }

  public <T> void mutateProperty(PropertyHolder propertyHolder, PropertyKey<T> key, Consumer<T> mutator) {
    PropertyInstance<T> property = ((PropertiesImpl) propertyHolder.getProperties()).get(key);
    mutator.accept(property.value());
    galleryData.updateProperty(propertyHolder, property);
  }

}
