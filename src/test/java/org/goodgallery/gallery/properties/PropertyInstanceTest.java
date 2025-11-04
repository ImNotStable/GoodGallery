package org.goodgallery.gallery.properties;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PropertyInstanceTest {

  @Test
  void testConstructor_createsInstance() {
    PropertyKey<String> key = new PropertyKey<>("test", String::getBytes, String::new);
    PropertyInstance<String> instance = new PropertyInstance<>(key, "value");
    
    assertNotNull(instance);
    assertEquals(key, instance.key());
    assertEquals("value", instance.value());
  }

  @Test
  void testConstructor_withNullValue() {
    PropertyKey<String> key = new PropertyKey<>("test", String::getBytes, String::new);
    PropertyInstance<String> instance = new PropertyInstance<>(key, null);
    
    assertNotNull(instance);
    assertNull(instance.value());
  }

  @Test
  void testKey_returnsCorrectKey() {
    PropertyKey<Integer> key = new PropertyKey<>("number", num -> new byte[]{num.byteValue()}, data -> (int) data[0]);
    PropertyInstance<Integer> instance = new PropertyInstance<>(key, 42);
    
    assertSame(key, instance.key());
  }

  @Test
  void testValue_returnsCorrectValue() {
    PropertyKey<String> key = new PropertyKey<>("test", String::getBytes, String::new);
    PropertyInstance<String> instance = new PropertyInstance<>(key, "hello");
    
    assertEquals("hello", instance.value());
  }

  @Test
  void testValue_setter_updatesValue() {
    PropertyKey<String> key = new PropertyKey<>("test", String::getBytes, String::new);
    PropertyInstance<String> instance = new PropertyInstance<>(key, "initial");
    
    PropertyInstance<String> result = instance.value("updated");
    
    assertSame(instance, result, "value setter should return this");
    assertEquals("updated", instance.value());
  }

  @Test
  void testValue_setter_canSetNull() {
    PropertyKey<String> key = new PropertyKey<>("test", String::getBytes, String::new);
    PropertyInstance<String> instance = new PropertyInstance<>(key, "initial");
    
    instance.value(null);
    assertNull(instance.value());
  }

  @Test
  void testSerialize_callsKeySerializer() {
    PropertyKey<String> key = new PropertyKey<>("test", String::getBytes, String::new);
    PropertyInstance<String> instance = new PropertyInstance<>(key, "data");
    
    byte[] serialized = instance.serialize();
    assertNotNull(serialized);
    assertEquals("data", new String(serialized));
  }

  @Test
  void testSerialize_withNullValue_returnsEmptyArray() {
    PropertyKey<String> key = new PropertyKey<>("test", String::getBytes, String::new);
    PropertyInstance<String> instance = new PropertyInstance<>(key, null);
    
    byte[] serialized = instance.serialize();
    assertNotNull(serialized);
    assertEquals(0, serialized.length);
  }

  @Test
  void testChaining_multipleValueCalls() {
    PropertyKey<Integer> key = new PropertyKey<>("num", num -> new byte[]{num.byteValue()}, data -> (int) data[0]);
    PropertyInstance<Integer> instance = new PropertyInstance<>(key, 1);
    
    instance.value(2).value(3).value(4);
    assertEquals(4, instance.value());
  }

  @Test
  void testMultipleInstances_sameKey_independentValues() {
    PropertyKey<String> key = new PropertyKey<>("test", String::getBytes, String::new);
    PropertyInstance<String> instance1 = new PropertyInstance<>(key, "value1");
    PropertyInstance<String> instance2 = new PropertyInstance<>(key, "value2");
    
    assertEquals("value1", instance1.value());
    assertEquals("value2", instance2.value());
    
    instance1.value("changed");
    assertEquals("changed", instance1.value());
    assertEquals("value2", instance2.value(), "instance2 should not be affected");
  }
}