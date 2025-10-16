package org.goodgallery.gallery;

import lombok.Getter;
import org.goodgallery.gallery.properties.PropertiesImpl;
import org.goodgallery.gallery.properties.PropertyKey;
import org.goodgallery.gallery.properties.SerializedProperties;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
public final class Group extends GalleryItem {

  private static final PropertyKey<?>[] DEFAULT_KEYS = {
    PropertiesImpl.NAME_KEY, PropertiesImpl.CREATION_TIMESTAMP_KEY
  };

  public static Group create(UUID uniqueId, SerializedProperties serializedProperties, Album... albums) {
    return new Group(uniqueId, serializedProperties, albums);
  }

  private final Set<Album> albums;

  Group(UUID uniqueId, SerializedProperties serializedProperties, Album... albums) {
    super(uniqueId, serializedProperties, DEFAULT_KEYS);
    this.albums = new HashSet<>();
    this.albums.addAll(Arrays.asList(albums));
  }

  Group(Album... albums) {
    super(DEFAULT_KEYS);
    this.albums = new HashSet<>();
    this.albums.addAll(Arrays.asList(albums));
  }

  public Collection<Album> getAlbums() {
    return Collections.unmodifiableCollection(albums);
  }

  void addAlbum(Album album) {
    albums.add(album);
  }

  void removeAlbum(Album album) {
    albums.remove(album);
  }

}
