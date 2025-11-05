package org.goodgallery.gallery.properties;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class SerializedProperties implements Properties {

  private final Map<String, byte[]> serializedData;

  public SerializedProperties(Gson gson, JsonObject json) {
    Map<String, byte[]> serializedData = new HashMap<>();
    for (String key : json.keySet())
      serializedData.put(key, gson.fromJson(json.get(key), byte[].class));
    this.serializedData = Collections.unmodifiableMap(serializedData);
  }

  SerializedProperties(Map<String, byte[]> serializedData) {
    this.serializedData = Collections.unmodifiableMap(serializedData);
  }

  @Override
  public <T> T getValue(PropertyKey<T> key) {
    return key.deserialize(serializedData.get(key.toString()));
  }

}