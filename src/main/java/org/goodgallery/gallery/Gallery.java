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

  /**
   * Constructs a Gallery backed by the given filesystem path.
   * <p>
   * The provided path is normalized to an absolute path and, if missing, the directory is created.
   * Persistent storage is initialized and existing photos, albums, and groups are loaded into memory.
   *
   * @param path the directory to use for gallery storage; will be normalized to an absolute path
   * @throws IOException if the directory cannot be created or the underlying storage cannot be initialized
   */
  Gallery(Path path) throws IOException {
    path = path.toAbsolutePath().normalize();
    if (!Files.isDirectory(path))
      Files.createDirectory(path);
    this.path = path;
    this.galleryData = JsonGalleryData.create(path);
  }

  /**
   * Get an unmodifiable view of all groups in the gallery.
   *
   * @return an unmodifiable collection containing every Group managed by this gallery
   */
  public Collection<Group> getGroups() {
    return Collections.unmodifiableCollection(galleryData.getGroups());
  }

  public Group getGroup(UUID uniqueId) {
    return galleryData.getGroup(uniqueId);
  }

  public Group getGroup(String name) {
    return galleryData.getGroup(name);
  }

  /**
   * Check whether a group with the given name exists in the gallery.
   *
   * @param name the group name to look up
   * @return `true` if a group with the given name exists in the gallery, `false` otherwise
   */
  public boolean hasGroup(String name) {
    return galleryData.hasGroup(name);
  }

  /**
   * Create a new group with the given name and register it in the gallery.
   * <p>
   * The created group is persisted and added to the gallery's internal group collection.
   *
   * @param name the display name to assign to the new group
   * @return the created Group
   */
  public Group createGroup(String name) {
    Group group = new Group();
    galleryData.add(group);
    updateProperty(group, PropertiesImpl.NAME_KEY, name);
    return group;
  }

  /**
   * Removes the specified group from the gallery and deletes its persisted representation.
   *
   * @param group the group to remove
   */
  public void deleteGroup(Group group) {
    galleryData.remove(group);
  }

  /**
   * Adds an album to a group's album collection.
   *
   * @param album the album to add
   * @param group the group to receive the album
   * @throws IllegalStateException if the album is not present in the gallery's albums collection
   *                               or the group is not present in the gallery's groups collection
   */
  public void addAlbumToGroup(Album album, Group group) {
    Preconditions.checkState(galleryData.hasAlbum(album), "Album \"%s\" does not exist", album.getName());
    Preconditions.checkState(galleryData.hasGroup(group), "Group \"%s\" does not exist", group.getName());
    mutateProperty(group, Properties.ALBUMS_KEY, albums -> albums.add(album));
  }

  /**
   * Removes the specified album from the specified group's album collection and persists the change.
   *
   * @param album the album to remove from the group
   * @param group the group to be updated
   * @throws IllegalStateException if the album does not exist in the gallery or the group does not exist
   */
  public void removeAlbumFromGroup(Album album, Group group) {
    Preconditions.checkState(galleryData.hasAlbum(album), "Album \"%s\" does not exist", album.getName());
    Preconditions.checkState(galleryData.hasGroup(group), "Group \"%s\" does not exist", group.getName());
    mutateProperty(group, Properties.ALBUMS_KEY, albums -> albums.remove(album));
  }

  /**
   * Provides an unmodifiable view of all albums in the gallery.
   *
   * @return an unmodifiable collection of albums contained in the gallery
   */
  public Collection<Album> getAlbums() {
    return Collections.unmodifiableCollection(galleryData.getAlbums());
  }

  public Album getAlbum(UUID uniqueId) {
    return galleryData.getAlbum(uniqueId);
  }

  /**
   * Retrieve the album with the specified name.
   *
   * @param name the album name to look up
   * @return the Album with the given name, or {@code null} if no such album exists
   */
  public Album getAlbum(String name) {
    return galleryData.getAlbum(name);
  }

  /**
   * Determines whether an album with the specified name exists in the gallery.
   *
   * @return true if an album with the specified name exists, false otherwise
   */
  public boolean hasAlbum(String name) {
    return galleryData.hasAlbum(name);
  }

  /**
   * Creates and registers a new, empty Album with the given name in the gallery.
   *
   * @param name the display name to assign to the new album
   * @return the newly created Album
   */
  public Album createAlbum(String name) {
    Album album = new Album();
    galleryData.add(album);
    updateProperty(album, PropertiesImpl.NAME_KEY, name);
    return album;
  }

  /**
   * Removes the specified album from persistent storage and from the gallery's in-memory album collection.
   *
   * @param album the album to delete
   */
  public void deleteAlbum(Album album) {
    galleryData.remove(album);
  }

  /**
   * Adds an existing photo to an existing album.
   *
   * @param photo the photo to add to the album
   * @param album the album that will receive the photo
   * @throws IllegalStateException if the photo or the album does not exist in this gallery
   */
  public void addPhotoToAlbum(Photo photo, Album album) {
    Preconditions.checkState(galleryData.hasPhoto(photo), "Photo \"%s\" does not exist", photo.getName());
    Preconditions.checkState(galleryData.hasAlbum(album), "Album \"%s\" does not exist", album.getName());
    mutateProperty(album, Properties.PHOTOS_KEY, photos -> photos.add(photo));
  }

  /**
   * Removes the specified photo from the specified album.
   *
   * @param photo the photo to remove
   * @param album the album to remove the photo from
   * @throws IllegalStateException if the photo or album does not exist in this gallery
   */
  public void removePhotoFromAlbum(Photo photo, Album album) {
    Preconditions.checkState(galleryData.hasPhoto(photo), "Photo \"%s\" does not exist", photo.getName());
    Preconditions.checkState(galleryData.hasAlbum(album), "Album \"%s\" does not exist", album.getName());
    mutateProperty(album, Properties.PHOTOS_KEY, photos -> photos.remove(photo));
  }

  /**
   * Get an unmodifiable view of all photos in the gallery.
   *
   * @return an unmodifiable Collection of Photo containing every photo managed by the gallery
   */
  public Collection<Photo> getPhotos() {
    return Collections.unmodifiableCollection(galleryData.getPhotos());
  }

  public Photo getPhoto(UUID uniqueId) {
    return galleryData.getPhoto(uniqueId);
  }

  public Photo getPhoto(Path path) {
    return galleryData.getPhoto(path);
  }

  /**
   * Retrieve a photo by its name.
   *
   * @param name the photo's name
   * @return the Photo with the specified name, or null if no photo with that name exists
   */
  public Photo getPhoto(String name) {
    return galleryData.getPhoto(name);
  }

  /**
   * Check whether a photo with the given name exists in the gallery.
   *
   * @param name the photo name to look up
   * @return true if a photo with the specified name exists in the gallery, false otherwise
   */
  public boolean hasPhoto(String name) {
    return galleryData.hasPhoto(name);
  }

  /**
   * Creates a copy of the file at the given path inside the gallery, registers the copy as a new Photo, and returns it.
   *
   * @param originalPath the path to the existing source photo file to copy
   * @return the newly created Photo representing the copied file in the gallery
   * @throws FileNotFoundException if the source file at {@code originalPath} does not exist
   * @throws FileAlreadyExistsException if a file with the same name already exists in the gallery destination
   * @throws IOException if an I/O error occurs while copying file attributes or performing file operations
   */
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

  /**
   * Moves a file into the gallery by copying it into the gallery directory and deleting the original.
   *
   * @param originalPath the filesystem path of the source file to move into the gallery
   * @return the newly created Photo representing the copied file in the gallery
   * @throws IOException if copying fails (including when the source is missing or destination already exists)
   *                     or if deleting the original file fails
   */
  public Photo cutPhoto(Path originalPath) throws IOException {
    Photo photo = copyPhoto(originalPath);
    Files.deleteIfExists(originalPath);
    return photo;
  }

  /**
   * Deletes a photo from the gallery including its persisted metadata and underlying file, and removes it from the in-memory collection.
   *
   * @param photo the Photo to delete; must have a file path stored in the `PATH_KEY` property
   * @throws IOException if an I/O error occurs while deleting the photo's underlying file or during persistence operations
   */
  public void deletePhoto(Photo photo) throws IOException {
    galleryData.remove(photo);
    Files.deleteIfExists(photo.getPropertyValue(PropertiesImpl.PATH_KEY));
  }

  /**
   * Set the given property on a PropertyHolder to the provided value and persist the change.
   *
   * @param propertyHolder the holder whose property will be updated
   * @param key the property key to set
   * @param value the new value for the property
   */
  public <T> void updateProperty(PropertyHolder propertyHolder, PropertyKey<T> key, T value) {
    PropertyInstance<T> property = ((PropertiesImpl) propertyHolder.getProperties()).set(key, value);
    galleryData.updateProperty(propertyHolder, property);
  }

  /**
   * Mutates a property's value on the given PropertyHolder and persists the updated property.
   *
   * @param propertyHolder the holder containing the property to mutate
   * @param key the key identifying which property to mutate
   * @param mutator a consumer that modifies the property's current value in place
   */
  public <T> void mutateProperty(PropertyHolder propertyHolder, PropertyKey<T> key, Consumer<T> mutator) {
    PropertyInstance<T> property = ((PropertiesImpl) propertyHolder.getProperties()).get(key);
    mutator.accept(property.value());
    galleryData.updateProperty(propertyHolder, property);
  }

  public <T> T getPropertyValue(PropertyHolder propertyHolder, PropertyKey<T> key) {
    return propertyHolder.getPropertyValue(key);
  }

}