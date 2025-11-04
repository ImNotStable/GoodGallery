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

  /**
   * Converts a JSON element into a byte array.
   *
   * <p>If the input is null, represents JSON null, or is an empty string, an empty byte array is returned.
   * Otherwise the JSON element is interpreted as a string and its bytes (platform default charset) are returned.</p>
   *
   * @param json the JSON element to convert; expected to contain a string value
   * @param typeOfT unused type token for deserialization context
   * @param context unused deserialization context
   * @return a byte array containing the bytes of the JSON string, or an empty byte array if the JSON is null, JSON null, or an empty string
   */
  @Override
  public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    if (json == null || json.isJsonNull() || json.getAsString().isEmpty())
      return new byte[0];
    return json.getAsString().getBytes();
  }

  /**
   * Converts a byte array into a JSON string element.
   *
   * @param src the byte array to convert; encoded to a Java String using the platform default charset
   * @return a JsonPrimitive containing the resulting String
   */
  @Override
  public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
    return new JsonPrimitive(new String(src));
  }

}