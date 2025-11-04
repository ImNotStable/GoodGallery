package org.goodgallery.gallery.properties;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class PropertiesImplTest {

  @Test
  void testCreate_withNullSerializedProperties() {
    PropertyKey<String> key = new PropertyKey<>("test", String::getBytes, String::new);
    PropertiesImpl properties = PropertiesImpl.create(null, key);
    
    assertNotNull(properties);
  }

  @Test
  void testCreate_withSerializedProperties() {
    PropertyKey<String> key = new PropertyKey<>("test", String::getBytes, String::new);
    SerializedProperties serialized = new SerializedProperties(new HashMap<>());
    
    PropertiesImpl properties = PropertiesImpl.create(serialized, key);
    assertNotNull(properties);
  }

  @Test
  void testCreate_withMultipleKeys() {
    PropertyKey<String> key1 = new PropertyKey<>("key1", String::getBytes, String::new);
    PropertyKey<String> key2 = new PropertyKey<>("key2", String::getBytes, String::new);
    
    PropertiesImpl properties = PropertiesImpl.create(null, key1, key2);
    assertNotNull(properties);
  }

  @Test
  void testGet_returnsPropertyInstance() {
    PropertyKey<String> key = new PropertyKey<>("test", String::getBytes, String::new);
    PropertiesImpl properties = PropertiesImpl.create(null, key);
    
    PropertyInstance<String> instance = properties.get(key);
    assertNotNull(instance);
    assertEquals(key, instance.key());
  }

  @Test
  void testSet_updatesValue() {
    PropertyKey<String> key = new PropertyKey<>("test", String::getBytes, String::new);
    PropertiesImpl properties = PropertiesImpl.create(null, key);
    
    PropertyInstance<String> result = properties.set(key, "new_value");
    assertNotNull(result);
    assertEquals("new_value", result.value());
  }

  @Test
  void testSet_returnsUpdatedInstance() {
    PropertyKey<String> key = new PropertyKey<>("test", String::getBytes, String::new);
    PropertiesImpl properties = PropertiesImpl.create(null, key);
    
    PropertyInstance<String> instance = properties.set(key, "value");
    assertSame(instance, properties.get(key), "set should return the same instance");
  }

  @Test
  void testGetValue_returnsValue() {
    PropertyKey<String> key = new PropertyKey<>("test", String::getBytes, String::new)
        .defaultProvider(props -> "default");
    PropertiesImpl properties = PropertiesImpl.create(null, key);
    
    String value = properties.getValue(key);
    assertEquals("default", value);
  }

  @Test
  void testGetValue_afterSet_returnsNewValue() {
    PropertyKey<String> key = new PropertyKey<>("test", String::getBytes, String::new);
    PropertiesImpl properties = PropertiesImpl.create(null, key);
    
    properties.set(key, "updated");
    assertEquals("updated", properties.getValue(key));
  }

  @Test
  void testGetValueOrDefault_withNoValue_returnsDefault() {
    PropertyKey<String> key = new PropertyKey<>("test", String::getBytes, String::new);
    PropertiesImpl properties = PropertiesImpl.create(null, key);
    
    String value = properties.getValueOrDefault(key, "fallback");
    assertEquals("fallback", value);
  }

  @Test
  void testGetValueOrDefault_withValue_returnsValue() {
    PropertyKey<String> key = new PropertyKey<>("test", String::getBytes, String::new);
    PropertiesImpl properties = PropertiesImpl.create(null, key);
    properties.set(key, "actual");
    
    String value = properties.getValueOrDefault(key, "fallback");
    assertEquals("actual", value);
  }

  @Test
  void testMultipleProperties_independentValues() {
    PropertyKey<String> key1 = new PropertyKey<>("key1", String::getBytes, String::new);
    PropertyKey<Integer> key2 = new PropertyKey<>("key2", 
        num -> new byte[]{num.byteValue()}, 
        data -> (int) data[0]);
    
    PropertiesImpl properties = PropertiesImpl.create(null, key1, key2);
    
    properties.set(key1, "string_value");
    properties.set(key2, 42);
    
    assertEquals("string_value", properties.getValue(key1));
    assertEquals(42, properties.getValue(key2));
  }

  @Test
  void testCreate_loadsFromSerializedProperties() {
    PropertyKey<String> key = new PropertyKey<>("test", String::getBytes, String::new);
    
    HashMap<String, byte[]> data = new HashMap<>();
    data.put("test", "initial_value".getBytes());
    SerializedProperties serialized = new SerializedProperties(data);
    
    PropertiesImpl properties = PropertiesImpl.create(serialized, key);
    assertEquals("initial_value", properties.getValue(key));
  }

  @Test
  void testCreate_usesDefaultProvider_whenNoSerializedValue() {
    PropertyKey<String> key = new PropertyKey<>("test", String::getBytes, String::new)
        .defaultProvider(props -> "from_provider");
    
    SerializedProperties serialized = new SerializedProperties(new HashMap<>());
    PropertiesImpl properties = PropertiesImpl.create(serialized, key);
    
    assertEquals("from_provider", properties.getValue(key));
  }
}