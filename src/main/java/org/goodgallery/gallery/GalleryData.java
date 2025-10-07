package org.goodgallery.gallery;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.goodgallery.gallery.properties.PropertyInstance;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

public class GalleryData {

  private static final Gson GSON = new GsonBuilder()
    .setPrettyPrinting()
    .create();

  private final Path path;
  private final JsonObject json;

  GalleryData(Path path) throws IOException {
    this.path = path.resolve("gallery.json");

    if (Files.notExists(this.path)) {
      Files.createFile(this.path);
      this.json = new JsonObject();
      json.add("groups", new JsonObject());
      json.add("albums", new JsonObject());
      json.add("photos", new JsonObject());
      save();
    } else
      this.json = GSON.fromJson(Files.newBufferedReader(this.path), JsonObject.class);
  }

  public Map<String, Group> getGroups(Map<String, Photo> photos) {
    Map<String, Group> groups = new HashMap<>();

    JsonObject groupsJson = json.getAsJsonObject("groups");

    for (String key : groupsJson.keySet()) {
      JsonObject albumsJson = groupsJson.getAsJsonObject(key).getAsJsonObject("albums");

      Album[] albums = new Album[albumsJson.size()];
      int i = 0;
      for (String albumKey : albumsJson.keySet()) {
        JsonArray rawAlbumPhotos = albumsJson.getAsJsonObject(albumKey).getAsJsonArray("photos");

        Photo[] albumPhotos = new Photo[rawAlbumPhotos.size()];
        for (int j = 0; j < rawAlbumPhotos.size(); j++)
          albumPhotos[j] = photos.get(rawAlbumPhotos.get(j).getAsString());

        albums[i++] = new Album(albumKey, albumPhotos);
      }

      groups.put(key, new Group(key, albums));
    }

    return groups;
  }

  public void addGroup(Group group) {
    JsonObject groups = json.getAsJsonObject("groups");

    JsonObject groupJson = new JsonObject();
    JsonArray albumArray = new JsonArray();
    for (Album album : group.getAlbums())
      albumArray.add(album.getName());
    groupJson.add("albums", new JsonObject());

    groups.add(group.getName(), groupJson);
    save();
  }

  public boolean hasGroup(Group group) {
    return json.getAsJsonObject("groups").has(group.getName());
  }

  public void deleteGroup(Group group) {
    json.getAsJsonObject("groups").remove(group.getName());
    save();
  }

  public void moveAlbum(Album album, @Nullable Group group) {
    JsonObject originalAlbumParent = findAlbumParent(album);
    JsonObject albumJson = originalAlbumParent.getAsJsonObject(album.getName());
    JsonObject newAlbumParent = group == null ?
      json.getAsJsonObject("albums") :
      json.getAsJsonObject("groups").getAsJsonObject(group.getName()).getAsJsonObject("albums");

    originalAlbumParent.remove(album.getName());
    newAlbumParent.add(album.getName(), albumJson);

    save();
  }

  public Map<String, Album> getAlbums(Map<String, Photo> photos) {
    Map<String, Album> albums = new HashMap<>();

    JsonObject albumsJson = json.getAsJsonObject("albums");
    for (String key : albumsJson.keySet()) {
      JsonArray rawAlbumPhotos = albumsJson.getAsJsonObject(key).getAsJsonArray("photos");

      Photo[] albumPhotos = new Photo[rawAlbumPhotos.size()];
      for (int i = 0; i < rawAlbumPhotos.size(); i++)
        albumPhotos[i] = photos.get(rawAlbumPhotos.get(i).getAsString());

      albums.put(key, new Album(key, albumPhotos));
    }

    return albums;
  }

  public void addAlbum(Album album, Photo... photos) {
    JsonObject albums = json.getAsJsonObject("albums");

    JsonObject albumJson = new JsonObject();
    JsonArray photosArray = new JsonArray();
    for (Photo photo : photos)
      photosArray.add(photo.getFileName());
    albumJson.add("photos", photosArray);

    albums.add(album.getName(), albumJson);
    save();
  }

  public boolean hasAlbum(Album album) {
    return json.getAsJsonObject("albums").has(album.getName());
  }

  public void renameAlbum(Album album, String newName) {
    JsonObject albumLocation = findAlbumParent(album);
    JsonObject albumJson = albumLocation.getAsJsonObject(album.getName());

    JsonObject albums = json.getAsJsonObject("albums");

    albums.add(newName, albumJson);
    albumLocation.remove(album.getName());

    save();
  }

  public void deleteAlbum(Album album) {
    json.getAsJsonObject("albums").remove(album.getName());
    save();
  }

  public Map<String, Photo> getPhotos() {
    Map<String, Photo> photos = new HashMap<>();
    JsonObject photosJson = json.getAsJsonObject("photos");

    for (String key : photosJson.keySet()) {
      Path path = this.path.getParent().resolve(key);
      JsonObject photoJson = photosJson.getAsJsonObject(key);
      photos.put(key, new Photo(path, photoJson));
    }

    return photos;
  }

  public void addPhoto(Photo photo) {
    JsonObject photoJson = new JsonObject();
    photo.getProperties().getProperties().forEach(property -> property.appendJson(photoJson));
    json.getAsJsonObject("photos").add(photo.getFileName(), photoJson);
    save();
  }

  public void updatePhotoProperty(Photo photo, PropertyInstance<?> property) {
    property.appendJson(json.getAsJsonObject("photos").getAsJsonObject(photo.getFileName()));
  }

  public boolean hasPhoto(Photo photo) {
    return json.getAsJsonObject("photos").has(photo.getFileName());
  }

  public void deletePhoto(Photo photo) {
    json.getAsJsonObject("photos").remove(photo.getFileName());
    save();
  }

  public void addPhotoToAlbum(Photo photo, Album album) {
    if (!hasPhoto(photo))
      throw new IllegalStateException("Photo at \"%s\" does not exist".formatted(photo.getFileName()));
    if (!hasAlbum(album))
      throw new IllegalStateException("Album \"%s\" does not exist".formatted(album.getName()));

    json.getAsJsonObject("albums")
      .getAsJsonObject(album.getName())
      .getAsJsonArray("photos")
      .add(photo.getFileName());

    save();
  }

  public void removePhotoFromAlbum(Photo photo, Album album) {
    if (!hasPhoto(photo))
      throw new IllegalStateException("Photo at \"%s\" does not exist".formatted(photo.getFileName()));
    if (!hasAlbum(album))
      throw new IllegalStateException("Album \"%s\" does not exist".formatted(album.getName()));

    JsonArray photos = json.getAsJsonObject("albums")
      .getAsJsonObject(album.getName())
      .getAsJsonArray("photos");

    for (int i = 0; i < photos.size(); i++)
      if (photos.get(i).getAsString().equals(photo.getFileName()))
        photos.remove(i);

    save();
  }

  private void save() {
    try {
      Files.write(path, GSON.toJson(json).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) {
      throw new RuntimeException("Failed to save GalleryData to \"%s\"".formatted(path), e);
    }
  }

  private JsonObject findAlbumParent(Album album) {
    JsonObject galleryAlbums = json.getAsJsonObject("albums");

    if (galleryAlbums.has(album.getName()))
      return galleryAlbums;

    JsonObject groups = json.getAsJsonObject("groups");

    for (String groupName : groups.keySet()) {
      JsonObject groupAlbums = groups.getAsJsonObject(groupName).getAsJsonObject("albums");
      if (groupAlbums.has(album.getName()))
        return groupAlbums;
    }

    throw new IllegalStateException("Album \"%s\" does not exist".formatted(album.getName()));
  }

}
