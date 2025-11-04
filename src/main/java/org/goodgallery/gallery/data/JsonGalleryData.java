package org.goodgallery.gallery.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.goodgallery.gallery.Album;
import org.goodgallery.gallery.Group;
import org.goodgallery.gallery.Photo;
import org.goodgallery.gallery.collections.AlbumCollection;
import org.goodgallery.gallery.collections.GroupCollection;
import org.goodgallery.gallery.collections.PhotoCollection;
import org.goodgallery.gallery.properties.PropertyHolder;
import org.goodgallery.gallery.properties.PropertyInstance;
import org.goodgallery.gallery.properties.SerializedProperties;
import org.goodgallery.gallery.util.JsonByteArrayAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

public final class JsonGalleryData extends AbstractGalleryData {

  public static GalleryData create(Path path) throws IOException {
    return new JsonGalleryData(path);
  }

  private final Gson GSON = new GsonBuilder()
    .registerTypeAdapter(byte[].class, new JsonByteArrayAdapter())
    .setPrettyPrinting()
    .create();

  private final Path path;
  private final JsonObject json;

  JsonGalleryData(Path path) throws IOException {
    this.path = path.resolve("gallery.json");

    if (Files.notExists(this.path)) {
      Files.createFile(this.path);
      this.json = new JsonObject();
      json.add("groups", new JsonObject());
      json.add("albums", new JsonObject());
      json.add("photos", new JsonObject());
    } else {
      this.json = GSON.fromJson(Files.newBufferedReader(this.path), JsonObject.class);
    }

    Runtime.getRuntime().addShutdownHook(new Thread(this::save));
  }

  @Override
  public void loadGroups(GroupCollection groups) {
    JsonObject groupsJson = json.getAsJsonObject("groups");

    for (String key : groupsJson.keySet()) {
      JsonObject groupJson = groupsJson.getAsJsonObject(key);
      SerializedProperties serializedProperties = SerializedProperties.create(GSON, groupJson);
      groups.createGroup(UUID.fromString(key), serializedProperties);
    }
  }

  @Override
  public void loadAlbums(AlbumCollection albums) {
    JsonObject albumsJson = json.getAsJsonObject("albums");
    for (String key : albumsJson.keySet()) {
      JsonObject albumJson = albumsJson.getAsJsonObject(key);
      SerializedProperties serializedProperties = SerializedProperties.create(GSON, albumJson);
      albums.createAlbum(UUID.fromString(key), serializedProperties);
    }
  }

  @Override
  public void loadPhotos(PhotoCollection photos) {
    JsonObject photosJson = json.getAsJsonObject("photos");

    for (String key : photosJson.keySet()) {
      SerializedProperties serializedProperties = SerializedProperties.create(GSON, photosJson.getAsJsonObject(key));
      photos.createPhoto(UUID.fromString(key), serializedProperties);
    }
  }

  @Override
  public void add(PropertyHolder propertyHolder) {
    getParent(propertyHolder).add(propertyHolder.toString(), new JsonObject());
  }

  @Override
  public void delete(PropertyHolder propertyHolder) {
    getParent(propertyHolder).remove(propertyHolder.toString());
  }

  @Override
  public void updateProperty(PropertyHolder propertyHolder, PropertyInstance<?> property) {
    findProperties(propertyHolder).add(property.key().toString(), GSON.toJsonTree(property.serialize(), byte[].class));
  }

  @Override
  protected void save() {
    try {
      Files.write(path, GSON.toJson(json).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) {
      throw new RuntimeException("Failed to save GalleryData to \"%s\"".formatted(path), e);
    }
  }

  private JsonObject getParent(PropertyHolder propertyHolder) {
    return json.getAsJsonObject(
      switch (propertyHolder) {
        case Photo ignored -> "photos";
        case Album ignored -> "albums";
        case Group ignored -> "groups";
        default -> throw new IllegalStateException();
      }
    );
  }

  private JsonObject findProperties(@NotNull PropertyHolder propertyHolder) {
    return getParent(propertyHolder).getAsJsonObject(propertyHolder.toString());
  }

}
