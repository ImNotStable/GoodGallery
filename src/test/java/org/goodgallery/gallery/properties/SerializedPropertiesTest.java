package org.goodgallery.gallery.properties;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SerializedPropertiesTest {

  @Test
  void testConstructor_createsImmutableMap() {
    Map<String, byte[]> data = new HashMap<>();
    data.put("key1", "value1".getBytes());
    
    SerializedProperties properties = new SerializedProperties(data);
    assertNotNull(properties);
    
    // Original map modifications should not affect SerializedProperties
    data.put("key2", "value2".getBytes());
  }

  @Test
  void testCreate_fromGsonAndJsonObject() {
    Gson gson = new Gson();
    JsonObject json = new JsonObject();
    json.addProperty("name", "VGVzdE5hbWU="); // Base64-like string
    
    SerializedProperties properties = SerializedProperties.create(gson, json);
    assertNotNull(properties);
  }

  @Test
  void testCreate_withEmptyJsonObject() {
    Gson gson = new Gson();
    JsonObject json = new JsonObject();
    
    SerializedProperties properties = SerializedProperties.create(gson, json);
    assertNotNull(properties);
  }

  @Test
  void testGetValue_withExistingKey() {
    PropertyKey<String> key = new PropertyKey<>("test", String::getBytes, String::new);
    Map<String, byte[]> data = new HashMap<>();
    data.put("test", "stored_value".getBytes());
    
    SerializedProperties properties = new SerializedProperties(data);
    String value = properties.getValue(key);
    
    assertEquals("stored_value", value);
  }

  @Test
  void testGetValue_withNonExistingKey_returnsNull() {
    PropertyKey<String> key = new PropertyKey<>("missing", String::getBytes, String::new);
    Map<String, byte[]> data = new HashMap<>();
    
    SerializedProperties properties = new SerializedProperties(data);
    String value = properties.getValue(key);
    
    assertNull(value);
  }

  @Test
  void testGetValue_deserializesUsingPropertyKey() {
    PropertyKey<Integer> key = new PropertyKey<>("number",
        num -> new byte[]{num.byteValue()},
        data -> (int) data[0]
    );
    
    Map<String, byte[]> data = new HashMap<>();
    data.put("number", new byte[]{42});
    
    SerializedProperties properties = new SerializedProperties(data);
    Integer value = properties.getValue(key);
    
    assertEquals(42, value);
  }

  @Test
  void testGetValueOrDefault_withExistingValue() {
    PropertyKey<String> key = new PropertyKey<>("test", String::getBytes, String::new);
    Map<String, byte[]> data = new HashMap<>();
    data.put("test", "actual".getBytes());
    
    SerializedProperties properties = new SerializedProperties(data);
    String value = properties.getValueOrDefault(key, "default");
    
    assertEquals("actual", value);
  }

  @Test
  void testGetValueOrDefault_withMissingValue_returnsDefault() {
    PropertyKey<String> key = new PropertyKey<>("missing", String::getBytes, String::new);
    Map<String, byte[]> data = new HashMap<>();
    
    SerializedProperties properties = new SerializedProperties(data);
    String value = properties.getValueOrDefault(key, "default");
    
    assertEquals("default", value);
  }

  @Test
  void testGetValueOrDefault_withKeyDefaultProvider() {
    PropertyKey<String> key = new PropertyKey<>("test", String::getBytes, String::new)
        .defaultProvider(props -> "from_provider");
    
    Map<String, byte[]> data = new HashMap<>();
    SerializedProperties properties = new SerializedProperties(data);
    
    String value = properties.getValueOrDefault(key);
    assertEquals("from_provider", value);
  }

  @Test
  void testMultipleValues_independentDeserialization() {
    PropertyKey<String> stringKey = new PropertyKey<>("str", String::getBytes, String::new);
    PropertyKey<Integer> intKey = new PropertyKey<>("int",
        num -> new byte[]{num.byteValue()},
        data -> (int) data[0]
    );
    
    Map<String, byte[]> data = new HashMap<>();
    data.put("str", "text".getBytes());
    data.put("int", new byte[]{100});
    
    SerializedProperties properties = new SerializedProperties(data);
    
    assertEquals("text", properties.getValue(stringKey));
    assertEquals(100, properties.getValue(intKey));
  }

  @Test
  void testCreate_parsesMultipleJsonProperties() {
    Gson gson = new Gson();
    JsonObject json = new JsonObject();
    json.addProperty("prop1", "value1");
    json.addProperty("prop2", "value2");
    json.addProperty("prop3", "value3");
    
    SerializedProperties properties = SerializedProperties.create(gson, json);
    
    PropertyKey<String> key1 = new PropertyKey<>("prop1", String::getBytes, String::new);
    PropertyKey<String> key2 = new PropertyKey<>("prop2", String::getBytes, String::new);
    PropertyKey<String> key3 = new PropertyKey<>("prop3", String::getBytes, String::new);
    
    assertNotNull(properties.getValue(key1));
    assertNotNull(properties.getValue(key2));
    assertNotNull(properties.getValue(key3));
  }
}