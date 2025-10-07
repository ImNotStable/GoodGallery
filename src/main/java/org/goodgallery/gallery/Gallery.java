package org.goodgallery.gallery;

import lombok.Getter;
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
import java.util.Map;

public class Gallery implements AlbumCollection, PhotoCollection {

  @Getter
  private final Path path;
  private final GalleryData galleryData;

  private final Map<String, Photo> photos;
  private final Map<String, Album> albums;
  private final Map<String, Group> groups;

  Gallery(Path path) throws IOException {
    path = path.toAbsolutePath().normalize();
    if (!Files.isDirectory(path))
      Files.createDirectory(path);
    this.galleryData = new GalleryData(path);
    this.path = path;
    this.photos = galleryData.getPhotos();
    this.albums = galleryData.getAlbums(photos);
    this.groups = galleryData.getGroups(photos);
  }

  public Collection<Group> getGroups() {
    return Collections.unmodifiableCollection(groups.values());
  }

  public Group getGroup(String name) {
    return groups.get(name);
  }

  public boolean hasGroup(String name) {
    return groups.containsKey(name);
  }

  public Group createGroup(String name) {
    Group group = new Group(name);
    galleryData.addGroup(group);
    groups.put(name, group);
    return group;
  }

  public void deleteGroup(Group group) {
    groups.remove(group.getName(), group);
  }

  public void moveAlbum(Album album, @Nullable Group group) {
    Group originalGroup = findGroupWithAlbum(album);
    if (originalGroup == group)
      return;

    galleryData.moveAlbum(album, group);
    if (group == null) {
      originalGroup.removeAlbum(album);
      albums.put(album.getName(), album);
      return;
    }

    if (originalGroup == null)
      albums.remove(album.getName(), album);
    else
      originalGroup.removeAlbum(album);
    group.addAlbum(album);
  }

  private @Nullable Group findGroupWithAlbum(Album album) {
    if (albums.containsKey(album.getName()))
      return null;
    for (Group group : groups.values())
      if (group.getAlbums().contains(album))
        return group;
    throw new IllegalArgumentException("Album " + album.getName() + " not found");
  }

  public Collection<Album> getAlbums() {
    return Collections.unmodifiableCollection(albums.values());
  }

  public Album getAlbum(String name) {
    return albums.get(name);
  }

  public boolean hasAlbum(String name) {
    return albums.containsKey(name);
  }

  public Album createAlbum(String name, Photo... photos) {
    Album album = new Album(name, photos);
    galleryData.addAlbum(album, photos);
    albums.put(name, album);
    return album;
  }

  public void renameAlbum(Album album, String newName) {
    galleryData.renameAlbum(album, newName);
    album.setName(newName);
  }

  public void deleteAlbum(Album album) {
    galleryData.deleteAlbum(album);
    albums.remove(album.getName(), album);
  }

  public void addPhotoToAlbum(Photo photo, Album album) {
    if (!photos.containsValue(photo))
      throw new IllegalStateException("Photo at \"%s\" does not exist".formatted(photo.getFileName()));
    if (!albums.containsValue(album))
      throw new IllegalStateException("Album \"%s\" does not exist".formatted(album.getName()));

    galleryData.addPhotoToAlbum(photo, album);
    album.addPhoto(photo);
  }

  public void removePhotoFromAlbum(Photo photo, Album album) {
    if (!photos.containsValue(photo))
      throw new IllegalStateException("Photo at \"%s\" does not exist".formatted(photo.getFileName()));
    if (!albums.containsValue(album))
      throw new IllegalStateException("Album \"%s\" does not exist".formatted(album.getName()));

    galleryData.removePhotoFromAlbum(photo, album);
    album.removePhoto(photo);
  }

  public Collection<Photo> getPhotos() {
    return Collections.unmodifiableCollection(photos.values());
  }

  public Photo getPhoto(String name) {
    return photos.get(name);
  }

  public boolean hasPhoto(String name) {
    return photos.containsKey(name);
  }

  public Photo copyPhoto(Path originalPath, Album... albums) throws IOException {
    if (!Files.exists(originalPath))
      throw new FileNotFoundException("Photo at \"%s\" does not exist".formatted(originalPath));

    Path newPath = path.resolve(originalPath.getFileName());

    if (Files.exists(newPath))
      throw new IllegalStateException("Photo at \"%s\" already exists".formatted(newPath));

    Files.copy(originalPath, newPath, StandardCopyOption.COPY_ATTRIBUTES);

    Photo photo = new Photo(newPath);
    galleryData.addPhoto(photo);
    photos.put(photo.getFileName(), photo);

    for (Album album : albums)
      addPhotoToAlbum(photo, album);

    return photo;
  }

  public Photo cutPhoto(Path originalPath, Album... albums) throws IOException {
    Photo photo = copyPhoto(originalPath, albums);
    Files.deleteIfExists(originalPath);
    return photo;
  }

  public <T> void updatePhotoProperty(Photo photo, PropertyKey<T> key, T value) {
    PropertyInstance<T> property = photo.getProperties().set(key, value);
    galleryData.updatePhotoProperty(photo, property);
  }

  public void deletePhoto(Photo photo) throws IOException {
    galleryData.deletePhoto(photo);
    photos.remove(photo.getFileName(), photo);
    Files.deleteIfExists(photo.getPath());
  }

}
