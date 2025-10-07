package org.goodgallery.gallery;

import lombok.Getter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Group implements AlbumCollection {

  @Getter
  private final String name;
  private final Set<Album> albums;

  Group(String name) {
    this.name = name;
    this.albums = new HashSet<>();
  }

  Group(String name, Album... albums) {
    this(name);
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

  @Override
  public String toString() {
    return "%s".formatted(name);
  }

}
