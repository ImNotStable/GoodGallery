package org.goodgallery.gallery;

import lombok.Getter;
import org.goodgallery.gallery.collections.AlbumCollection;
import org.goodgallery.gallery.collections.GroupCollection;
import org.goodgallery.gallery.collections.PhotoCollection;
import org.goodgallery.gallery.data.GalleryData;
import org.goodgallery.gallery.data.JsonGalleryData;
import org.goodgallery.gallery.properties.PropertiesImpl;
import org.goodgallery.gallery.properties.PropertyHolder;
import org.goodgallery.gallery.properties.PropertyInstance;
import org.goodgallery.gallery.properties.PropertyKey;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

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
    galleryData.loadAlbums(albums, photos);
    this.groups = new GroupCollection();
    galleryData.loadGroups(groups, photos);
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
    galleryData.addGroup(group);
    updateProperty(group, PropertiesImpl.NAME_KEY, name);
    groups.add(group);
    return group;
  }

  public void deleteGroup(Group group) {
    galleryData.deleteGroup(group);
    groups.remove(group);
  }

  public void moveAlbum(Album album, @Nullable Group group) {
    Group originalGroup = findGroupWithAlbum(album);
    if (originalGroup == group)
      return;

    galleryData.moveAlbum(album, group);
    if (group == null) {
      originalGroup.removeAlbum(album);
      albums.add(album);
      return;
    }

    if (originalGroup == null)
      albums.remove(album);
    else
      originalGroup.removeAlbum(album);
    group.addAlbum(album);
  }

  private @Nullable Group findGroupWithAlbum(Album album) {
    if (albums.has(album))
      return null;
    for (Group group : groups.getGroups())
      if (group.getAlbums().contains(album))
        return group;
    throw new IllegalArgumentException("Album " + album.getName() + " not found");
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

  public Album createAlbum(String name, Photo... photos) {
    Album album = new Album(photos);
    galleryData.addAlbum(album, photos);
    updateProperty(album, PropertiesImpl.NAME_KEY, name);
    albums.add(album);
    return album;
  }

  public void deleteAlbum(Album album) {
    galleryData.deleteAlbum(album);
    albums.remove(album);
  }

  public void addPhotoToAlbum(Photo photo, Album album) {
    if (!photos.has(photo))
      throw new IllegalStateException("Photo at \"%s\" does not exist".formatted(photo.getFileName()));
    if (!albums.has(album))
      throw new IllegalStateException("Album \"%s\" does not exist".formatted(album.getName()));

    galleryData.addPhotoToAlbum(photo, album);
    album.addPhoto(photo);
  }

  public void removePhotoFromAlbum(Photo photo, Album album) {
    if (!photos.has(photo))
      throw new IllegalStateException("Photo at \"%s\" does not exist".formatted(photo.getFileName()));
    if (!albums.has(album))
      throw new IllegalStateException("Album \"%s\" does not exist".formatted(album.getName()));

    galleryData.removePhotoFromAlbum(photo, album);
    album.removePhoto(photo);
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

  public Photo copyPhoto(Path originalPath, Album... albums) throws IOException {
    if (!Files.exists(originalPath))
      throw new FileNotFoundException("Photo at \"%s\" does not exist".formatted(originalPath));

    Path newPath = path.resolve(originalPath.getFileName());

    if (Files.exists(newPath))
      throw new IllegalStateException("Photo at \"%s\" already exists".formatted(newPath));

    Files.copy(originalPath, newPath, StandardCopyOption.COPY_ATTRIBUTES);

    Photo photo = new Photo();
    galleryData.addPhoto(photo);
    updateProperty(photo, PropertiesImpl.PATH_KEY, newPath);
    photos.add(photo);

    for (Album album : albums)
      addPhotoToAlbum(photo, album);

    return photo;
  }

  public Photo cutPhoto(Path originalPath, Album... albums) throws IOException {
    Photo photo = copyPhoto(originalPath, albums);
    Files.deleteIfExists(originalPath);
    return photo;
  }

  public void deletePhoto(Photo photo) throws IOException {
    galleryData.deletePhoto(photo);
    photos.remove(photo);
    Files.deleteIfExists(photo.getPropertyValue(PropertiesImpl.PATH_KEY));
  }

  public <T> void updateProperty(PropertyHolder propertyHolder, PropertyKey<T> key, T value) {
    PropertyInstance<T> property = ((PropertiesImpl) propertyHolder.getProperties()).set(key, value);
    galleryData.updateProperty(propertyHolder, property);
  }

}
