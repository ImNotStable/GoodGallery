package org.goodgallery.gallery;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Setter(AccessLevel.PACKAGE)
@Getter
public class Album implements PhotoCollection {

  private String name;
  private final Set<Photo> photos;

  Album(String name) {
    this.name = name;
    this.photos = new HashSet<>();
  }

  Album(String name, Photo... photos) {
    this(name);
    this.photos.addAll(Arrays.asList(photos));
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
    return name;
  }

}
