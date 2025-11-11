package org.goodgallery.gallery.data;

import com.google.gson.*;
import org.goodgallery.gallery.Album;
import org.goodgallery.gallery.GalleryItem;
import org.goodgallery.gallery.Group;
import org.goodgallery.gallery.Photo;
import org.goodgallery.gallery.properties.PropertyInstance;
import org.goodgallery.gallery.properties.SerializedProperties;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.UUID;
import java.util.function.BiConsumer;

public final class JsonGalleryData extends AbstractGalleryData {

  private final Gson GSON = new GsonBuilder()
    .registerTypeAdapter(byte[].class, new JsonByteArrayAdapter())
    .setPrettyPrinting()
    .create();

  private final Path path;
  private final JsonObject json;

  public JsonGalleryData(Path path) throws IOException {
    this.path = path.resolve("gallery.json");

    if (Files.exists(this.path)) {
      this.json = GSON.fromJson(Files.newBufferedReader(this.path), JsonObject.class);
    } else {
      Files.createFile(this.path);
      this.json = new JsonObject();
      json.add("groups", new JsonObject());
      json.add("albums", new JsonObject());
      json.add("photos", new JsonObject());
      save();
    }
  }

  private void loadSection(String section, BiConsumer<UUID, SerializedProperties> generator) {
    JsonObject sectionJson = json.getAsJsonObject(section);
    for (String key : sectionJson.keySet())
      generator.accept(UUID.fromString(key), new SerializedProperties(GSON, sectionJson.getAsJsonObject(key)));
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

  @Override
  protected void load() {
    loadSection("photos,", this::createPhoto);
    loadSection("albums", this::createAlbum);
    loadSection("groups", this::createGroup);
  }

  @Override
  protected void save() {
    try {
      Files.write(path, GSON.toJson(json).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) {
      throw new RuntimeException("Failed to save GalleryData to \"%s\"".formatted(path), e);
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

  private static class JsonByteArrayAdapter implements JsonSerializer<byte[]>, JsonDeserializer<byte[]> {

    @Override
    public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      if (json == null || json.isJsonNull() || json.getAsString().isEmpty())
        return new byte[0];
      return Base64.getDecoder().decode(json.getAsString());
    }

    @Override
    public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
      return new JsonPrimitive(Base64.getEncoder().encodeToString(src));
    }

  }

}