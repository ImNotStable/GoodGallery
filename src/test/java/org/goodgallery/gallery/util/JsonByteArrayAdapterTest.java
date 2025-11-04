package org.goodgallery.gallery.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonByteArrayAdapterTest {

  private JsonByteArrayAdapter adapter;

  @BeforeEach
  void setUp() {
    adapter = new JsonByteArrayAdapter();
  }

  @Test
  void testSerialize_withValidByteArray() {
    byte[] data = "Hello World".getBytes();
    
    JsonElement result = adapter.serialize(data, byte[].class, null);
    
    assertNotNull(result);
    assertTrue(result.isJsonPrimitive());
    assertEquals("Hello World", result.getAsString());
  }

  @Test
  void testSerialize_withEmptyByteArray() {
    byte[] data = new byte[0];
    
    JsonElement result = adapter.serialize(data, byte[].class, null);
    
    assertNotNull(result);
    assertTrue(result.isJsonPrimitive());
    assertEquals("", result.getAsString());
  }

  @Test
  void testSerialize_withSpecialCharacters() {
    byte[] data = "Special: !@#$%^&*()".getBytes();
    
    JsonElement result = adapter.serialize(data, byte[].class, null);
    
    assertNotNull(result);
    assertEquals("Special: !@#$%^&*()", result.getAsString());
  }

  @Test
  void testDeserialize_withValidJsonString() {
    JsonElement json = new JsonPrimitive("Test Data");
    
    byte[] result = adapter.deserialize(json, byte[].class, null);
    
    assertNotNull(result);
    assertEquals("Test Data", new String(result));
  }

  @Test
  void testDeserialize_withEmptyString() {
    JsonElement json = new JsonPrimitive("");
    
    byte[] result = adapter.deserialize(json, byte[].class, null);
    
    assertNotNull(result);
    assertEquals(0, result.length);
  }

  @Test
  void testDeserialize_withNull_returnsEmptyArray() {
    byte[] result = adapter.deserialize(null, byte[].class, null);
    
    assertNotNull(result);
    assertEquals(0, result.length);
  }

  @Test
  void testDeserialize_withJsonNull_returnsEmptyArray() {
    JsonElement json = JsonNull.INSTANCE;
    
    byte[] result = adapter.deserialize(json, byte[].class, null);
    
    assertNotNull(result);
    assertEquals(0, result.length);
  }

  @Test
  void testSerializeDeserialize_roundTrip() {
    byte[] original = "Round trip test".getBytes();
    
    JsonElement serialized = adapter.serialize(original, byte[].class, null);
    byte[] deserialized = adapter.deserialize(serialized, byte[].class, null);
    
    assertArrayEquals(original, deserialized);
  }

  @Test
  void testSerializeDeserialize_withUnicodeCharacters() {
    byte[] original = "Unicode: 日本語 🎉".getBytes();
    
    JsonElement serialized = adapter.serialize(original, byte[].class, null);
    byte[] deserialized = adapter.deserialize(serialized, byte[].class, null);
    
    assertArrayEquals(original, deserialized);
  }

  @Test
  void testSerializeDeserialize_withNumericData() {
    byte[] original = "12345678".getBytes();
    
    JsonElement serialized = adapter.serialize(original, byte[].class, null);
    byte[] deserialized = adapter.deserialize(serialized, byte[].class, null);
    
    assertArrayEquals(original, deserialized);
  }

  @Test
  void testDeserialize_multipleEmptyConditions() {
    // Test null
    assertNotNull(adapter.deserialize(null, byte[].class, null));
    assertEquals(0, adapter.deserialize(null, byte[].class, null).length);
    
    // Test JsonNull
    assertNotNull(adapter.deserialize(JsonNull.INSTANCE, byte[].class, null));
    assertEquals(0, adapter.deserialize(JsonNull.INSTANCE, byte[].class, null).length);
    
    // Test empty string
    assertNotNull(adapter.deserialize(new JsonPrimitive(""), byte[].class, null));
    assertEquals(0, adapter.deserialize(new JsonPrimitive(""), byte[].class, null).length);
  }

  @Test
  void testSerialize_withBinaryData() {
    byte[] binary = new byte[]{0x00, 0x01, 0x02, (byte) 0xFF};
    
    JsonElement result = adapter.serialize(binary, byte[].class, null);
    
    assertNotNull(result);
    assertTrue(result.isJsonPrimitive());
  }
}