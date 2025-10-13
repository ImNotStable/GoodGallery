package org.goodgallery.gallery;

import com.google.gson.JsonObject;
import lombok.Getter;
import org.goodgallery.gallery.properties.PropertyHolder;
import org.goodgallery.gallery.properties.PropertyKey;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
public class Group implements PropertyHolder {

  private static final PropertyKey<?>[] DEFAULT_KEYS = {
    Properties.NAME_KEY, Properties.CREATION_TIMESTAMP_KEY
  };

  public static Group create(UUID uniqueId, JsonObject json, Album... albums) {
    return new Group(uniqueId, json, albums);
  }

  private final UUID uniqueId;
  private final Properties properties;
  private final Set<Album> albums;

  Group(UUID uniqueId, JsonObject json, Album... albums) {
    this.uniqueId = uniqueId;
    this.properties = new Properties(json, DEFAULT_KEYS);
    this.albums = new HashSet<>();
    this.albums.addAll(Arrays.asList(albums));
  }

  Group(UUID uniqueId, Album... albums) {
    this(uniqueId, null, albums);
  }

  Group(Album... albums) {
    this(UUID.randomUUID(), albums);
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

  @Override
  public String toString() {
    return uniqueId.toString();
  }

}
