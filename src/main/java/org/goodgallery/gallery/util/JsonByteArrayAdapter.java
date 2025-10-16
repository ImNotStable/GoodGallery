package org.goodgallery.gallery.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class JsonByteArrayAdapter implements JsonSerializer<byte[]>, JsonDeserializer<byte[]> {

  @Override
  public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    if (json == null || json.isJsonNull() || json.getAsString().isEmpty())
      return new byte[0];
    return json.getAsString().getBytes();
  }

  @Override
  public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
    return new JsonPrimitive(new String(src));
  }

}
