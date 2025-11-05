package org.goodgallery.gallery.util;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

public class JsonByteArrayAdapter implements JsonSerializer<byte[]>, JsonDeserializer<byte[]> {

  @Override
  public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    if (json == null || json.isJsonNull() || json.getAsString().isEmpty())
      return new byte[0];
    return json.getAsString().getBytes(StandardCharsets.UTF_8);
  }

  @Override
  public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
    return new JsonPrimitive(new String(src, StandardCharsets.UTF_8));
  }

}