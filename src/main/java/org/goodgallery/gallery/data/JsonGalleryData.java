package org.goodgallery.gallery.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.goodgallery.gallery.Album;
import org.goodgallery.gallery.GalleryItem;
import org.goodgallery.gallery.Group;
import org.goodgallery.gallery.Photo;
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
  protected void insert(GalleryItem galleryItem) {
    getParent(galleryItem).add(galleryItem.toString(), new JsonObject());
    save();
  }

  @Override
  protected void delete(GalleryItem galleryItem) {
    getParent(galleryItem).remove(galleryItem.toString());
    save();
  }

  @Override
  public void updateProperty(GalleryItem galleryItem, PropertyInstance<?> property) {
    findProperties(galleryItem).add(property.key().toString(), GSON.toJsonTree(property.serialize(), byte[].class));
    save();
  }

  @Override
  protected void load() {
    JsonObject photosJson = json.getAsJsonObject("photos");
    for (String key : photosJson.keySet())
      createPhoto(UUID.fromString(key), new SerializedProperties(GSON, photosJson.getAsJsonObject(key)));

    JsonObject albumsJson = json.getAsJsonObject("albums");
    for (String key : albumsJson.keySet())
      createAlbum(UUID.fromString(key), new SerializedProperties(GSON, albumsJson.getAsJsonObject(key)));

    JsonObject groupsJson = json.getAsJsonObject("groups");
    for (String key : groupsJson.keySet())
      createGroup(UUID.fromString(key), new SerializedProperties(GSON, groupsJson.getAsJsonObject(key)));
  }

  @Override
  protected void save() {
    try {
      Files.write(path, GSON.toJson(json).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) {
      throw new RuntimeException("Failed to save GalleryData to \"%s\"".formatted(path), e);
    }
  }

  private JsonObject getParent(GalleryItem galleryItem) {
    return json.getAsJsonObject(
      switch (galleryItem) {
        case Photo _ -> "photos";
        case Album _ -> "albums";
        case Group _ -> "groups";
        default -> throw new IllegalStateException();
      }
    );
  }

  private JsonObject findProperties(@NotNull GalleryItem galleryItem) {
    return getParent(galleryItem).getAsJsonObject(galleryItem.toString());
  }

}