package org.goodgallery.gallery.properties;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class SerializedProperties implements Properties {

  private final Map<String, byte[]> serializedData;

  public SerializedProperties(Gson gson, JsonObject json) {
    Map<String, byte[]> serializedData = new HashMap<>();
    for (String key : json.keySet())
      serializedData.put(key, gson.fromJson(json.get(key), byte[].class));
    this.serializedData = Collections.unmodifiableMap(serializedData);
  }

  public SerializedProperties(Map<String, byte[]> serializedData) {
    this.serializedData = Collections.unmodifiableMap(serializedData);
  }

  @Override
  public <T> Optional<T> getValue(PropertyKey<T> key) {
    byte[] data = serializedData.get(key.toString());
    if (data == null)
      return Optional.empty();
    return Optional.ofNullable(key.deserialize(data));
  }

}