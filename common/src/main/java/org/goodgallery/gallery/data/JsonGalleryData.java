package org.goodgallery.gallery.data;

import com.google.gson.*;
import org.goodgallery.gallery.Album;
import org.goodgallery.gallery.GalleryItem;
import org.goodgallery.gallery.Group;
import org.goodgallery.gallery.Photo;
import org.goodgallery.gallery.properties.PropertiesImpl;
import org.goodgallery.gallery.properties.PropertyInstance;
import org.goodgallery.gallery.properties.SerializedProperties;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

public final class JsonGalleryData extends AbstractGalleryData {

  private final Gson GSON = new GsonBuilder()
    .registerTypeAdapter(byte[].class, new JsonByteArrayAdapter())
    .setPrettyPrinting()
    .create();

  private final JsonObject json;

  public JsonGalleryData(Path path) throws IOException {
    super(path.resolve("gallery.json"));

    if (Files.exists(path)) {
      json = GSON.fromJson(Files.newBufferedReader(super.path), JsonObject.class);
    } else {
      Files.createFile(super.path);
      json = new JsonObject();
      json.add("groups", new JsonObject());
      json.add("albums", new JsonObject());
      json.add("photos", new JsonObject());
      save();
    }
  }

  private <T extends GalleryItem> void loadSection(String section, BiFunction<UUID, SerializedProperties, T> generator, Map<UUID, T> map) {
    JsonObject sectionJson = json.getAsJsonObject(section);
    for (String key : sectionJson.keySet()) {
      UUID uniqueId = UUID.fromString(key);
      T galleryItem = generator.apply(uniqueId, new SerializedProperties(GSON, sectionJson.getAsJsonObject(key)));
      map.put(uniqueId, galleryItem);
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

  private void save() {
    try {
      Files.write(path, GSON.toJson(json).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) {
      throw new RuntimeException("Failed to save GalleryData to \"%s\"".formatted(path), e);
    }
  }

  @Override
  protected synchronized void load() {
    loadSection("photos", Photo::new, photosByUUID);
    loadSection("albums", Album::new, albumsByUUID);
    loadSection("groups", Group::new, groupsByUUID);
  }

  @Override
  protected synchronized void insert(GalleryItem galleryItem) {
    getParent(galleryItem).add(galleryItem.toString(), new JsonObject());
    ((PropertiesImpl) galleryItem.getProperties()).all().forEach(
      property -> findProperties(galleryItem).add(property.key().toString(), GSON.toJsonTree(property.serialize(), byte[].class))
    );
    save();
  }

  @Override
  protected synchronized void delete(GalleryItem galleryItem) {
    getParent(galleryItem).remove(galleryItem.toString());
    save();
  }

  @Override
  public synchronized void updateProperty(GalleryItem galleryItem, PropertyInstance<?> property) {
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