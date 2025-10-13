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
public class Album implements PropertyHolder {

  private static final PropertyKey<?>[] DEFAULT_KEYS = {
    Properties.NAME_KEY, Properties.CREATION_TIMESTAMP_KEY
  };

  public static Album create(UUID uniqueId, JsonObject json, Photo... photos) {
    return  new Album(uniqueId, json, photos);
  }

  private final UUID uniqueId;
  private final Properties properties;
  private final Set<Photo> photos;

  Album(UUID uniqueId, JsonObject json, Photo... photos) {
    this.uniqueId = uniqueId;
    this.properties = Properties.create(json, DEFAULT_KEYS);
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

}
