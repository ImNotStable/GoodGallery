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

  /**
   * Creates a GalleryData implementation backed by a gallery.json file located under the given path.
   *
   * @param path the directory that contains (or will contain) the gallery.json data file
   * @return a GalleryData instance persisting metadata to {@code path}/gallery.json
   * @throws IOException if the gallery.json file cannot be created or read during initialization
   */
  public static GalleryData create(Path path) throws IOException {
    return new JsonGalleryData(path);
  }

  private final Gson GSON = new GsonBuilder()
    .registerTypeAdapter(byte[].class, new JsonByteArrayAdapter())
    .setPrettyPrinting()
    .create();

  private final Path path;
  private final JsonObject json;

  /**
   * Initializes a JsonGalleryData instance backed by a gallery.json file in the given directory.
   *
   * <p>If the file does not exist it is created and an empty JSON store with "groups", "albums",
   * and "photos" sections is initialized; if the file exists it is loaded into memory. A JVM
   * shutdown hook is registered to persist the in-memory JSON by calling save().
   *
   * @param path the directory in which to resolve and manage the gallery.json file
   * @throws IOException if the gallery.json file cannot be created or read
   */
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

  /**
   * Populates the given GroupCollection with Group instances reconstructed from the in-memory JSON store.
   *
   * @param groups the collection to populate with groups loaded from this data store
   */
  @Override
  public void loadGroups(GroupCollection groups) {
    JsonObject groupsJson = json.getAsJsonObject("groups");

    for (String key : groupsJson.keySet()) {
      JsonObject groupJson = groupsJson.getAsJsonObject(key);
      SerializedProperties serializedProperties = SerializedProperties.create(GSON, groupJson);
      groups.createGroup(UUID.fromString(key), serializedProperties);
    }
  }

  /**
   * Populates the provided AlbumCollection with albums deserialized from the in-memory JSON store.
   *
   * For each entry in the "albums" JSON object, an Album is created using the entry's UUID key
   * and the properties deserialized from the corresponding JSON object.
   *
   * @param albums the collection to populate with albums reconstructed from the JSON data
   */
  @Override
  public void loadAlbums(AlbumCollection albums) {
    JsonObject albumsJson = json.getAsJsonObject("albums");
    for (String key : albumsJson.keySet()) {
      JsonObject albumJson = albumsJson.getAsJsonObject(key);
      SerializedProperties serializedProperties = SerializedProperties.create(GSON, albumJson);
      albums.createAlbum(UUID.fromString(key), serializedProperties);
    }
  }

  /**
   * Populates the provided collection with Photo instances reconstructed from the in-memory JSON store.
   *
   * Iterates the stored photo entries, parses each entry's UUID key, converts its JSON object into
   * SerializedProperties, and creates a corresponding photo in the given collection.
   *
   * @param photos the collection to populate with photos loaded from the JSON data store
   */
  @Override
  public void loadPhotos(PhotoCollection photos) {
    JsonObject photosJson = json.getAsJsonObject("photos");

    for (String key : photosJson.keySet()) {
      SerializedProperties serializedProperties = SerializedProperties.create(GSON, photosJson.getAsJsonObject(key));
      photos.createPhoto(UUID.fromString(key), serializedProperties);
    }
  }

  /**
   * Add a new empty JSON entry for the given PropertyHolder in the appropriate parent section.
   *
   * @param propertyHolder the holder (Group, Album, or Photo) whose string key will be used to create an empty JsonObject entry in the corresponding parent ("groups", "albums", or "photos")
   */
  @Override
  public void add(PropertyHolder propertyHolder) {
    getParent(propertyHolder).add(propertyHolder.toString(), new JsonObject());
  }

  /**
   * Removes the JSON entry for the given property holder from the gallery's in-memory data.
   *
   * @param propertyHolder the holder whose persisted entry should be removed
   * @throws IllegalStateException if the holder's type is not recognized (not group, album, or photo)
   */
  @Override
  public void delete(PropertyHolder propertyHolder) {
    getParent(propertyHolder).remove(propertyHolder.toString());
  }

  /**
   * Store or update the given property's serialized value in this gallery's JSON store for the specified holder.
   *
   * Serializes the property's value and places it under the property's key in the holder's properties object.
   *
   * @param propertyHolder the target holder (group, album, or photo) whose properties will be updated
   * @param property       the property instance whose serialized value will be stored under its key
   */
  @Override
  public void updateProperty(PropertyHolder propertyHolder, PropertyInstance<?> property) {
    findProperties(propertyHolder).add(property.key().toString(), GSON.toJsonTree(property.serialize(), byte[].class));
  }

  /**
   * Persist the in-memory JSON representation to the configured gallery.json file.
   *
   * @throws RuntimeException if an I/O error prevents writing the file
   */
  @Override
  protected void save() {
    try {
      Files.write(path, GSON.toJson(json).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) {
      throw new RuntimeException("Failed to save GalleryData to \"%s\"".formatted(path), e);
    }
  }

  /**
   * Selects the top-level JSON object section corresponding to the given PropertyHolder type.
   *
   * @param propertyHolder the holder whose JSON section to retrieve (Photo → "photos", Album → "albums", Group → "groups")
   * @return the JsonObject for the holder's top-level section
   * @throws IllegalStateException if the PropertyHolder type is not Photo, Album, or Group
   */
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

  /**
   * Locate the JSON object that stores properties for the given PropertyHolder.
   *
   * @param propertyHolder the property holder whose property map should be retrieved
   * @return the JsonObject associated with the holder's string key inside its parent section
   */
  private JsonObject findProperties(@NotNull PropertyHolder propertyHolder) {
    return getParent(propertyHolder).getAsJsonObject(propertyHolder.toString());
  }

}