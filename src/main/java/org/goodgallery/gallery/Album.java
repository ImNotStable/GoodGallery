package org.goodgallery.gallery;

import com.google.gson.JsonObject;
import lombok.Getter;
import org.goodgallery.gallery.properties.PropertyHolder;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
public class Album implements PropertyHolder {

  private final UUID uniqueId;
  private final Properties properties;
  private final Set<Photo> photos;

  Album(UUID uniqueId, JsonObject json, Photo... photos) {
    this.uniqueId = uniqueId;
    this.properties = new AlbumProperties(this, json);
    this.photos = new HashSet<>();
    this.photos.addAll(Arrays.asList(photos));
  }

  Album(UUID uniqueId, Photo... photos) {
    this(uniqueId, null, photos);
  }

  Album(Photo... photos) {
    this(UUID.randomUUID(), photos);
  }

  public String getName() {
    return properties.getValue(Properties.NAME_KEY);
  }

  public Collection<Photo> getPhotos() {
    return Collections.unmodifiableCollection(photos);
  }

  void addPhoto(Photo photo) {
    photos.add(photo);
  }

  void removePhoto(Photo photo) {
    photos.remove(photo);
  }

  @Override
  public String toString() {
    return uniqueId.toString();
  }

  static class AlbumProperties extends Properties {

    AlbumProperties(Album album, JsonObject json) {
      super(album, json);
      register(NAME_KEY);
      register(CREATION_TIMESTAMP_KEY);
    }

  }

}
