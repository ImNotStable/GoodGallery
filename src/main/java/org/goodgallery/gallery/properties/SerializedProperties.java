package org.goodgallery.gallery.properties;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class SerializedProperties implements Properties {

  /**
   * Create a SerializedProperties by deserializing every entry of the provided JsonObject into a byte[] using the given Gson.
   *
   * @param gson the Gson instance used to deserialize each JSON value into a byte[]
   * @param json the JsonObject whose properties will be converted into the serialized-data map
   * @return a SerializedProperties containing an unmodifiable mapping from each JSON property name to its deserialized byte[] value
   */
  public static SerializedProperties create(Gson gson, JsonObject json) {
    Map<String, byte[]> serializedData = new HashMap<>();

    for (String key : json.keySet())
      serializedData.put(key, gson.fromJson(json.get(key), byte[].class));

    return new SerializedProperties(serializedData);
  }

  private final Map<String, byte[]> serializedData;

  /**
   * Constructs a SerializedProperties instance that holds an unmodifiable view of the provided serialized data map.
   *
   * @param serializedData a map from property key strings to their serialized byte[] values; the map will be wrapped
   *                       with {@link Collections#unmodifiableMap(Map)} and not mutated by this instance
   */
  SerializedProperties(Map<String, byte[]> serializedData) {
    this.serializedData = Collections.unmodifiableMap(serializedData);
  }

  /**
   * Retrieve the value for a property key by deserializing its stored byte representation.
   *
   * @param <T> the expected property value type
   * @param key the property key whose stored bytes will be deserialized
   * @return the deserialized property value of type `T`, or `null` if no serialized bytes exist for the key
   */
  @Override
  public <T> T getValue(PropertyKey<T> key) {
    return key.deserialize(serializedData.get(key.toString()));
  }

}