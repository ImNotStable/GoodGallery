package org.goodgallery.gallery.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.goodgallery.gallery.Album;
import org.goodgallery.gallery.Group;
import org.goodgallery.gallery.Photo;
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

  private final Gson GSON = new GsonBuilder()
    .registerTypeAdapter(byte[].class, new JsonByteArrayAdapter())
    .setPrettyPrinting()
    .create();

  private final Path path;
  private final JsonObject json;

  public JsonGalleryData(Path path) throws IOException {
    this.path = path.resolve("gallery.json");

    if (Files.notExists(this.path)) {
      Files.createFile(this.path);
      this.json = new JsonObject();
      json.add("groups", new JsonObject());
      json.add("albums", new JsonObject());
      json.add("photos", new JsonObject());
      save();
    } else {
      this.json = GSON.fromJson(Files.newBufferedReader(this.path), JsonObject.class);
    }
  }

  @Override
  protected void insert(PropertyHolder propertyHolder) {
    getParent(propertyHolder).add(propertyHolder.toString(), new JsonObject());
    save();
  }

  @Override
  protected void delete(PropertyHolder propertyHolder) {
    getParent(propertyHolder).remove(propertyHolder.toString());
    save();
  }

  @Override
  public void updateProperty(PropertyHolder propertyHolder, PropertyInstance<?> property) {
    findProperties(propertyHolder).add(property.key().toString(), GSON.toJsonTree(property.serialize(), byte[].class));
    save();
  }

  @Override
  protected void load() {
    JsonObject photosJson = json.getAsJsonObject("photos");

    for (String key : photosJson.keySet()) {
      SerializedProperties serializedProperties = new SerializedProperties(GSON, photosJson.getAsJsonObject(key));
      createPhoto(UUID.fromString(key), serializedProperties);
    }


    JsonObject albumsJson = json.getAsJsonObject("albums");
    for (String key : albumsJson.keySet()) {
      JsonObject albumJson = albumsJson.getAsJsonObject(key);
      SerializedProperties serializedProperties = new SerializedProperties(GSON, albumJson);
      createAlbum(UUID.fromString(key), serializedProperties);
    }

    JsonObject groupsJson = json.getAsJsonObject("groups");
    for (String key : groupsJson.keySet()) {
      JsonObject groupJson = groupsJson.getAsJsonObject(key);
      SerializedProperties serializedProperties = new SerializedProperties(GSON, groupJson);
      createGroup(UUID.fromString(key), serializedProperties);
    }
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