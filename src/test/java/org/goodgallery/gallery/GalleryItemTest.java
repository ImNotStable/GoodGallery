package org.goodgallery.gallery;

import org.goodgallery.gallery.properties.Properties;
import org.goodgallery.gallery.properties.PropertyKey;
import org.goodgallery.gallery.properties.SerializedProperties;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GalleryItemTest {

  private static class TestGalleryItem extends GalleryItem {
    private static final PropertyKey<?>[] TEST_KEYS = {
      Properties.NAME_KEY
    };

    TestGalleryItem() {
      super(TEST_KEYS);
    }

    TestGalleryItem(UUID uuid, SerializedProperties props) {
      super(uuid, props, TEST_KEYS);
    }
  }

  @Test
  void testConstructor_withNoArgs_generatesUniqueId() {
    TestGalleryItem item = new TestGalleryItem();
    
    assertNotNull(item.getUniqueId());
  }

  @Test
  void testConstructor_withUuidAndProperties() {
    UUID uuid = UUID.randomUUID();
    HashMap<String, byte[]> data = new HashMap<>();
    data.put("name", "Test Item".getBytes());
    SerializedProperties serialized = new SerializedProperties(data);
    
    TestGalleryItem item = new TestGalleryItem(uuid, serialized);
    
    assertEquals(uuid, item.getUniqueId());
    assertEquals("Test Item", item.getName());
  }

  @Test
  void testGetUniqueId_returnsCorrectId() {
    UUID uuid = UUID.randomUUID();
    TestGalleryItem item = new TestGalleryItem(uuid, null);
    
    assertEquals(uuid, item.getUniqueId());
  }

  @Test
  void testGetProperties_returnsNonNull() {
    TestGalleryItem item = new TestGalleryItem();
    
    assertNotNull(item.getProperties());
  }

  @Test
  void testGetName_returnsPropertyValue() {
    TestGalleryItem item = new TestGalleryItem();
    
    String name = item.getName();
    assertNotNull(name);
  }

  @Test
  void testGetPropertyValue_withValidKey() {
    HashMap<String, byte[]> data = new HashMap<>();
    data.put("name", "Custom Name".getBytes());
    SerializedProperties serialized = new SerializedProperties(data);
    
    TestGalleryItem item = new TestGalleryItem(UUID.randomUUID(), serialized);
    
    String name = item.getPropertyValue(Properties.NAME_KEY);
    assertEquals("Custom Name", name);
  }

  @Test
  void testGetPropertyValue_withMissingKey_returnsNull() {
    TestGalleryItem item = new TestGalleryItem();
    
    // Try to get a property that doesn't exist in TEST_KEYS
    PropertyKey<String> missingKey = new PropertyKey<>("missing", String::getBytes, String::new);
    String value = item.getPropertyValue(missingKey);
    
    assertNull(value);
  }

  @Test
  void testToString_returnsUuidAsString() {
    UUID uuid = UUID.randomUUID();
    TestGalleryItem item = new TestGalleryItem(uuid, null);
    
    assertEquals(uuid.toString(), item.toString());
  }

  @Test
  void testMultipleItems_haveDistinctUuids() {
    TestGalleryItem item1 = new TestGalleryItem();
    TestGalleryItem item2 = new TestGalleryItem();
    
    assertNotEquals(item1.getUniqueId(), item2.getUniqueId());
  }
}