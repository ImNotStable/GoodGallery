package org.goodgallery.gallery.properties;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class PropertyKeyTest {

  @Test
  void testConstructor_createsPropertyKey() {
    PropertyKey<String> key = new PropertyKey<>(
        "test",
        String::getBytes,
        String::new
    );
    
    assertNotNull(key);
    assertEquals("test", key.toString());
  }

  @Test
  void testSerialize_withValue() {
    PropertyKey<String> key = new PropertyKey<>(
        "test",
        String::getBytes,
        String::new
    );
    
    byte[] result = key.serialize("hello");
    assertNotNull(result);
    assertEquals("hello", new String(result));
  }

  @Test
  void testSerialize_withNull_returnsEmptyArray() {
    PropertyKey<String> key = new PropertyKey<>(
        "test",
        String::getBytes,
        String::new
    );
    
    byte[] result = key.serialize(null);
    assertNotNull(result);
    assertEquals(0, result.length);
  }

  @Test
  void testDeserialize_withValidData() {
    PropertyKey<String> key = new PropertyKey<>(
        "test",
        String::getBytes,
        String::new
    );
    
    byte[] data = "hello".getBytes();
    String result = key.deserialize(data);
    assertEquals("hello", result);
  }

  @Test
  void testDeserialize_withInvalidData_returnsNull() {
    PropertyKey<Integer> key = new PropertyKey<>(
        "test",
        num -> ByteBuffer.allocate(4).putInt(num).array(),
        data -> {
          if (data.length != 4) throw new IllegalArgumentException();
          return ByteBuffer.wrap(data).getInt();
        }
    );
    
    byte[] invalidData = new byte[]{1, 2}; // Wrong size
    Integer result = key.deserialize(invalidData);
    assertNull(result, "Should return null for invalid data");
  }

  @Test
  void testDeserialize_withExceptionInDeserializer_returnsNull() {
    PropertyKey<String> key = new PropertyKey<>(
        "test",
        String::getBytes,
        data -> { throw new RuntimeException("Test exception"); }
    );
    
    byte[] data = "test".getBytes();
    String result = key.deserialize(data);
    assertNull(result, "Should return null when deserializer throws exception");
  }

  @Test
  void testDefaultProvider_withoutSetting_returnsNull() {
    PropertyKey<String> key = new PropertyKey<>(
        "test",
        String::getBytes,
        String::new
    );
    
    PropertiesImpl properties = PropertiesImpl.create(null);
    String defaultValue = key.getDefaultValue(properties);
    assertNull(defaultValue);
  }

  @Test
  void testDefaultProvider_withCustomProvider() {
    PropertyKey<String> key = new PropertyKey<>(
        "test",
        String::getBytes,
        String::new
    ).defaultProvider(props -> "default_value");
    
    PropertiesImpl properties = PropertiesImpl.create(null);
    String defaultValue = key.getDefaultValue(properties);
    assertEquals("default_value", defaultValue);
  }

  @Test
  void testDefaultProvider_returnsThis() {
    PropertyKey<String> key = new PropertyKey<>(
        "test",
        String::getBytes,
        String::new
    );
    
    PropertyKey<String> result = key.defaultProvider(props -> "value");
    assertSame(key, result, "defaultProvider should return this for chaining");
  }

  @Test
  void testToString_returnsId() {
    PropertyKey<String> key = new PropertyKey<>(
        "my-property",
        String::getBytes,
        String::new
    );
    
    assertEquals("my-property", key.toString());
  }

  @Test
  void testSerializeDeserialize_roundTrip_integer() {
    PropertyKey<Integer> key = new PropertyKey<>(
        "int-key",
        num -> ByteBuffer.allocate(4).putInt(num).array(),
        data -> ByteBuffer.wrap(data).getInt()
    );
    
    Integer original = 42;
    byte[] serialized = key.serialize(original);
    Integer deserialized = key.deserialize(serialized);
    
    assertEquals(original, deserialized);
  }

  @Test
  void testSerializeDeserialize_roundTrip_long() {
    PropertyKey<Long> key = new PropertyKey<>(
        "long-key",
        num -> ByteBuffer.allocate(8).putLong(num).array(),
        data -> ByteBuffer.wrap(data).getLong()
    );
    
    Long original = 123456789L;
    byte[] serialized = key.serialize(original);
    Long deserialized = key.deserialize(serialized);
    
    assertEquals(original, deserialized);
  }

  @Test
  void testMultipleKeys_withSameId_areDifferentObjects() {
    PropertyKey<String> key1 = new PropertyKey<>("id", String::getBytes, String::new);
    PropertyKey<String> key2 = new PropertyKey<>("id", String::getBytes, String::new);
    
    assertNotSame(key1, key2);
    assertEquals(key1.toString(), key2.toString());
  }
}