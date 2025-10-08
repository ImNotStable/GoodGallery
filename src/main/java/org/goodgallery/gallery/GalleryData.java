package org.goodgallery.gallery;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.goodgallery.gallery.collections.AlbumCollection;
import org.goodgallery.gallery.collections.GroupCollection;
import org.goodgallery.gallery.collections.PhotoCollection;
import org.goodgallery.gallery.properties.PropertyHolder;
import org.goodgallery.gallery.properties.PropertyInstance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

public class GalleryData {

  private final Gson GSON = new GsonBuilder()
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

    Runtime.getRuntime().addShutdownHook(new Thread(this::save));
  }

  public void loadGroups(GroupCollection groups, PhotoCollection photos) {
    JsonObject groupsJson = json.getAsJsonObject("groups");

    for (String key : groupsJson.keySet()) {
      JsonObject groupJson = groupsJson.getAsJsonObject(key);
      JsonObject albumsJson = groupJson.getAsJsonObject("albums");

      Album[] albums = new Album[albumsJson.size()];
      int i = 0;
      for (String albumKey : albumsJson.keySet()) {
        JsonObject albumJson = albumsJson.getAsJsonObject(albumKey);
        JsonArray rawAlbumPhotos = albumsJson.getAsJsonObject(albumKey).getAsJsonArray("photos");

        Photo[] albumPhotos = new Photo[rawAlbumPhotos.size()];
        for (int j = 0; j < rawAlbumPhotos.size(); j++)
          albumPhotos[j] = photos.getPhoto(rawAlbumPhotos.get(j).getAsString());

        albums[i++] = new Album(UUID.fromString(albumKey), albumJson, albumPhotos);
      }

      groups.add(new Group(UUID.fromString(key), groupsJson, albums));
    }
  }

  public void addGroup(Group group) {
    JsonObject groups = json.getAsJsonObject("groups");

    JsonObject groupJson = new JsonObject();
    JsonArray albumArray = new JsonArray();
    for (Album album : group.getAlbums())
      albumArray.add(album.toString());
    groupJson.add("albums", new JsonObject());

    groups.add(group.toString(), groupJson);
    save();
  }

  public boolean hasGroup(Group group) {
    return json.getAsJsonObject("groups").has(group.toString());
  }

  public void deleteGroup(Group group) {
    json.getAsJsonObject("groups").remove(group.toString());
    save();
  }

  public void loadAlbums(AlbumCollection albums, PhotoCollection photos) {
    JsonObject albumsJson = json.getAsJsonObject("albums");
    for (String key : albumsJson.keySet()) {
      JsonObject albumJson = albumsJson.getAsJsonObject(key);
      JsonArray rawAlbumPhotos = albumJson.getAsJsonArray("photos");

      Photo[] albumPhotos = new Photo[rawAlbumPhotos.size()];
      for (int i = 0; i < rawAlbumPhotos.size(); i++)
        albumPhotos[i] = photos.getPhoto(rawAlbumPhotos.get(i).getAsString());

      albums.add(new Album(UUID.fromString(key), albumJson, albumPhotos));
    }
  }

  public void addAlbum(Album album, Photo... photos) {
    JsonObject albums = json.getAsJsonObject("albums");

    JsonObject albumJson = new JsonObject();
    JsonArray photosArray = new JsonArray();
    for (Photo photo : photos)
      photosArray.add(photo.getFileName());
    albumJson.add("photos", photosArray);

    albums.add(album.toString(), albumJson);
    save();
  }

  public void moveAlbum(Album album, @Nullable Group group) {
    if (!hasAlbum(album))
      throw new IllegalStateException("Album \"%s\" does not exist".formatted(album.toString()));
    if (group != null && !hasGroup(group))
      throw new IllegalStateException("Group \"%s\" does not exist".formatted(group.toString()));

    JsonObject originalAlbumParent = findAlbumParent(album.toString());
    JsonObject albumJson = originalAlbumParent.getAsJsonObject(album.toString());
    JsonObject newAlbumParent = group == null ?
      json.getAsJsonObject("albums") :
      json.getAsJsonObject("groups").getAsJsonObject(group.toString()).getAsJsonObject("albums");

    originalAlbumParent.remove(album.toString());
    newAlbumParent.add(album.toString(), albumJson);

    save();
  }

  public void addPhotoToAlbum(Photo photo, Album album) {
    if (!hasPhoto(photo))
      throw new IllegalStateException("Photo \"%s\" does not exist".formatted(photo.toString()));
    if (!hasAlbum(album))
      throw new IllegalStateException("Album \"%s\" does not exist".formatted(album.toString()));

    json.getAsJsonObject("albums")
      .getAsJsonObject(album.toString())
      .getAsJsonArray("photos")
      .add(photo.getFileName());

    save();
  }

  public void removePhotoFromAlbum(Photo photo, Album album) {
    if (!hasPhoto(photo))
      throw new IllegalStateException("Photo at \"%s\" does not exist".formatted(photo.toString()));
    if (!hasAlbum(album))
      throw new IllegalStateException("Album \"%s\" does not exist".formatted(album.toString()));

    JsonArray photos = json.getAsJsonObject("albums")
      .getAsJsonObject(album.toString())
      .getAsJsonArray("photos");

    for (int i = 0; i < photos.size(); i++)
      if (photos.get(i).getAsString().equals(photo.getFileName()))
        photos.remove(i);

    save();
  }

  public boolean hasAlbum(Album album) {
    return findAlbum(album.toString()) != null;
  }

  public void deleteAlbum(Album album) {
    json.getAsJsonObject("albums").remove(album.toString());
    save();
  }

  public void loadPhotos(PhotoCollection photos) {
    JsonObject photosJson = json.getAsJsonObject("photos");

    for (String key : photosJson.keySet()) {
      JsonObject photoJson = photosJson.getAsJsonObject(key);

      photos.add(new Photo(UUID.fromString(key), photoJson));
    }
  }

  public void addPhoto(Photo photo) {
    JsonObject photoJson = new JsonObject();
    photo.getProperties().getProperties().forEach(property -> property.appendJson(photoJson));
    json.getAsJsonObject("photos").add(photo.toString(), photoJson);
    save();
  }

  public boolean hasPhoto(Photo photo) {
    return json.getAsJsonObject("photos").has(photo.toString());
  }

  public void deletePhoto(Photo photo) {
    json.getAsJsonObject("photos").remove(photo.toString());
    save();
  }

  public void updateProperty(PropertyHolder propertyHolder, PropertyInstance<?> property) {
    property.appendJson(findProperties(propertyHolder));
  }

  private void save() {
    try {
      Files.write(path, GSON.toJson(json).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) {
      throw new RuntimeException("Failed to save GalleryData to \"%s\"".formatted(path), e);
    }
  }

  private JsonObject findProperties(@NotNull PropertyHolder propertyHolder) {
    String uniqueId = propertyHolder.getUniqueId().toString();
    return switch (propertyHolder) {
      case Photo _ -> findPhoto(uniqueId);
      case Album _ -> findAlbum(uniqueId);
      case Group _ -> findGroup(uniqueId);
      default -> new JsonObject();
    };
  }

  private JsonObject findGroup(String uniqueId) {
    return json.getAsJsonObject("groups").getAsJsonObject(uniqueId);
  }

  private @Nullable JsonObject findAlbumParent(String uniqueId) {
    JsonObject galleryAlbums = json.getAsJsonObject("albums");

    if (galleryAlbums.has(uniqueId))
      return galleryAlbums;

    JsonObject groups = json.getAsJsonObject("groups");

    for (String groupName : groups.keySet()) {
      JsonObject groupAlbums = groups.getAsJsonObject(groupName).getAsJsonObject("albums");
      if (groupAlbums.has(uniqueId))
        return groupAlbums;
    }

    return null;
  }

  private JsonObject findAlbum(String uniqueId) {
    return findAlbumParent(uniqueId).getAsJsonObject(uniqueId);
  }

  private JsonObject findPhoto(String uniqueId) {
    return json.getAsJsonObject("photos").getAsJsonObject(uniqueId);
  }

}
