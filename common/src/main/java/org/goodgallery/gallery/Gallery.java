package org.goodgallery.gallery;

import lombok.Getter;
import org.goodgallery.gallery.data.GalleryData;
import org.goodgallery.gallery.properties.Properties;
import org.goodgallery.gallery.properties.PropertiesImpl;
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
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public final class Gallery {

  @Getter
  private final Path path;
  private final GalleryData galleryData;

  Gallery(GallerySettings properties) throws Exception {
    path = properties.galleryPath().toAbsolutePath().normalize();
    if (!Files.isDirectory(path))
      Files.createDirectories(path);
    this.galleryData = properties.storage(path);
  }

  public Collection<Group> getGroups() {
    return Collections.unmodifiableCollection(galleryData.getGroups());
  }

  public Optional<Group> getGroup(UUID uniqueId) {
    return galleryData.getGroup(uniqueId);
  }

  public Optional<Group> getGroup(String name) {
    return galleryData.getGroup(name);
  }

  public boolean hasGroup(UUID uniqueId) {
    return galleryData.hasGroup(uniqueId);
  }

  public boolean hasGroup(String name) {
    return galleryData.hasGroup(name);
  }

  public Group createGroup(String name) {
    Group group = new Group();
    ((PropertiesImpl) group.getProperties()).get(Properties.NAME_KEY).value(name);
    galleryData.add(group);
    return group;
  }

  public void deleteGroup(Group group) {
    galleryData.remove(group);
  }

  public void addAlbumToGroup(Album album, Group group) {
    if (!galleryData.hasAlbum(album))
      throw new IllegalStateException("Album \"%s\" does not exist".formatted(album.getName().orElse(album.toString())));
    if (!galleryData.hasGroup(group))
      throw new IllegalStateException("Group \"%s\" does not exist".formatted(group.getName().orElse(group.toString())));
    mutateProperty(group, Properties.ALBUMS_KEY, albums -> albums.add(album));
  }

  public void removeAlbumFromGroup(Album album, Group group) {
    if (!galleryData.hasAlbum(album))
      throw new IllegalStateException("Album \"%s\" does not exist".formatted(album.getName().orElse(album.toString())));
    if (!galleryData.hasGroup(group))
      throw new IllegalStateException("Group \"%s\" does not exist".formatted(group.getName().orElse(group.toString())));
    mutateProperty(group, Properties.ALBUMS_KEY, albums -> albums.remove(album));
  }

  public Collection<Album> getAlbums() {
    return Collections.unmodifiableCollection(galleryData.getAlbums());
  }

  public Optional<Album> getAlbum(UUID uniqueId) {
    return galleryData.getAlbum(uniqueId);
  }

  public Optional<Album> getAlbum(String name) {
    return galleryData.getAlbum(name);
  }

  public boolean hasAlbum(UUID uniqueId) {
    return galleryData.hasAlbum(uniqueId);
  }

  public boolean hasAlbum(String name) {
    return galleryData.hasAlbum(name);
  }

  public Album createAlbum(String name) {
    Album album = new Album();
    ((PropertiesImpl) album.getProperties()).get(Properties.NAME_KEY).value(name);
    galleryData.add(album);
    return album;
  }

  public void deleteAlbum(Album album) {
    galleryData.remove(album);
  }

  public void addPhotoToAlbum(Photo photo, Album album) {
    if (!galleryData.hasPhoto(photo))
      throw new IllegalStateException("Photo \"%s\" does not exist".formatted(photo.getName().orElse(photo.toString())));
    if (!galleryData.hasAlbum(album))
      throw new IllegalStateException("Album \"%s\" does not exist".formatted(album.getName().orElse(album.toString())));
    mutateProperty(album, Properties.PHOTOS_KEY, photos -> photos.add(photo));
  }

  public void removePhotoFromAlbum(Photo photo, Album album) {
    if (!galleryData.hasPhoto(photo))
      throw new IllegalStateException("Photo \"%s\" does not exist".formatted(photo.getName().orElse(photo.toString())));
    if (!galleryData.hasAlbum(album))
      throw new IllegalStateException("Album \"%s\" does not exist".formatted(album.getName().orElse(album.toString())));
    mutateProperty(album, Properties.PHOTOS_KEY, photos -> photos.remove(photo));
  }

  public Collection<Photo> getPhotos() {
    return Collections.unmodifiableCollection(galleryData.getPhotos());
  }

  public Optional<Photo> getPhoto(UUID uniqueId) {
    return galleryData.getPhoto(uniqueId);
  }

  public Optional<Photo> getPhoto(Path path) {
    return galleryData.getPhoto(path);
  }

  public Optional<Photo> getPhoto(String name) {
    return galleryData.getPhoto(name);
  }

  public boolean hasPhoto(UUID uniqueId) {
    return galleryData.hasPhoto(uniqueId);
  }

  public boolean hasPhoto(Path path) {
    return galleryData.hasPhoto(path);
  }

  public boolean hasPhoto(String name) {
    return galleryData.hasPhoto(name);
  }

  public Photo copyPhoto(Path originalPath) throws Exception {
    if (!Files.exists(originalPath))
      throw new FileNotFoundException("File at \"%s\" does not exist".formatted(originalPath));

    String mimeType = Files.probeContentType(originalPath);
    if (mimeType == null || !mimeType.startsWith("image/"))
      throw new RuntimeException("File at \"%s\" is not a valid image".formatted(originalPath));

    Path newPath = path.resolve(originalPath.getFileName());

    if (Files.exists(newPath))
      throw new FileAlreadyExistsException("Photo at \"%s\" already exists".formatted(newPath));

    Files.copy(originalPath, newPath, StandardCopyOption.COPY_ATTRIBUTES);

    Photo photo = new Photo();
    ((PropertiesImpl) photo.getProperties()).get(Properties.PATH_KEY).value(newPath);
    galleryData.add(photo);

    return photo;
  }

  public Photo cutPhoto(Path originalPath) throws Exception {
    Photo photo = copyPhoto(originalPath);
    Files.deleteIfExists(originalPath);
    return photo;
  }

  public void deletePhoto(Photo photo) throws IOException {
    galleryData.remove(photo);

    Optional<Path> path = photo.getPropertyValue(Properties.PATH_KEY);
    if (path.isPresent())
      Files.deleteIfExists(path.get());
  }

  public <T> void updateProperty(GalleryItem galleryItem, PropertyKey<T> key, T value) {
    PropertyInstance<T> property = ((PropertiesImpl) galleryItem.getProperties()).get(key).value(value);
    galleryData.updateProperty(galleryItem, property);
  }

  public <T> void mutateProperty(GalleryItem galleryItem, PropertyKey<T> key, Consumer<T> mutator) {
    PropertyInstance<T> property = ((PropertiesImpl) galleryItem.getProperties()).get(key);
    property.value().ifPresent(mutator);
    galleryData.updateProperty(galleryItem, property);
  }

}