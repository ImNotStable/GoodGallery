package org.goodgallery.gallery.collections;

import org.goodgallery.gallery.Album;
import org.goodgallery.gallery.properties.SerializedProperties;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class AlbumCollection {

  private final Map<UUID, Album> albumsByUUID;
  private final Map<String, Album> albumsByName;

  /**
   * Constructs a new AlbumCollection with empty thread-safe maps for indexing albums by UUID and by name.
   */
  public AlbumCollection() {
    albumsByUUID = new ConcurrentHashMap<>();
    albumsByName = new ConcurrentHashMap<>();
  }

  /**
   * Create and add a new Album to the collection using the provided UUID and serialized properties.
   *
   * @param uniqueId            the UUID to assign to the new album
   * @param serializedProperties serialized data used to initialize the album's properties
   */
  public void createAlbum(UUID uniqueId, SerializedProperties serializedProperties) {
    add(Album.create(uniqueId, serializedProperties));
  }

  /**
   * Adds the album to the collection and indexes it by both unique ID and name.
   *
   * @param album the Album to add; if an album with the same UUID or name already exists it will be replaced
   */
  public void add(Album album) {
    albumsByUUID.put(album.getUniqueId(), album);
    albumsByName.put(album.getName(), album);
  }

  public boolean has(Album album) {
    return has(album.getUniqueId());
  }

  public boolean has(UUID uniqueId) {
    return albumsByUUID.containsKey(uniqueId);
  }

  public boolean has(String name) {
    return albumsByName.containsKey(name);
  }

  public Collection<Album> getAlbums() {
    return albumsByUUID.values();
  }

  public Album getAlbum(UUID uniqueId) {
    return albumsByUUID.get(uniqueId);
  }

  public Album getAlbum(String name) {
    return albumsByName.get(name);
  }

  public void remove(Album album) {
    albumsByUUID.remove(album.getUniqueId());
    albumsByName.remove(album.getName());
  }

}