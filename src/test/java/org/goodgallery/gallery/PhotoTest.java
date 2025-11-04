package org.goodgallery.gallery;

import org.goodgallery.gallery.properties.Properties;
import org.goodgallery.gallery.properties.SerializedProperties;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PhotoTest {

  @Test
  void testConstructor_generatesUniqueId() {
    Photo photo1 = new Photo();
    Photo photo2 = new Photo();
    
    assertNotNull(photo1.getUniqueId());
    assertNotNull(photo2.getUniqueId());
    assertNotEquals(photo1.getUniqueId(), photo2.getUniqueId());
  }

  @Test
  void testConstructor_withSerializedProperties() {
    UUID uuid = UUID.randomUUID();
    HashMap<String, byte[]> data = new HashMap<>();
    data.put("path", "/photos/test.jpg".getBytes());
    data.put("name", "test.jpg".getBytes());
    SerializedProperties serialized = new SerializedProperties(data);
    
    Photo photo = Photo.create(uuid, serialized);
    
    assertNotNull(photo);
    assertEquals(uuid, photo.getUniqueId());
  }

  @Test
  void testGetProperties_returnsProperties() {
    Photo photo = new Photo();
    
    assertNotNull(photo.getProperties());
  }

  @Test
  void testGetName_returnsName() {
    Photo photo = new Photo();
    
    assertNotNull(photo.getName());
  }

  @Test
  void testGetPath_returnsPath() {
    Photo photo = new Photo();
    
    assertNull(photo.getPath()); // Path is null by default for new photos
  }

  @Test
  void testGetFileName_withPath() {
    HashMap<String, byte[]> data = new HashMap<>();
    data.put("path", "/photos/vacation/beach.jpg".getBytes());
    SerializedProperties serialized = new SerializedProperties(data);
    
    Photo photo = Photo.create(UUID.randomUUID(), serialized);
    
    String fileName = photo.getFileName();
    assertEquals("beach.jpg", fileName);
  }

  @Test
  void testToString_returnsUniqueIdString() {
    Photo photo = new Photo();
    
    assertEquals(photo.getUniqueId().toString(), photo.toString());
  }

  @Test
  void testGetPropertyValue_withPathKey() {
    HashMap<String, byte[]> data = new HashMap<>();
    data.put("path", "/test/image.png".getBytes());
    SerializedProperties serialized = new SerializedProperties(data);
    
    Photo photo = Photo.create(UUID.randomUUID(), serialized);
    Path path = photo.getPropertyValue(Properties.PATH_KEY);
    
    assertNotNull(path);
    assertEquals(Paths.get("/test/image.png"), path);
  }

  @Test
  void testCreate_factoryMethod() {
    UUID uuid = UUID.randomUUID();
    HashMap<String, byte[]> data = new HashMap<>();
    SerializedProperties serialized = new SerializedProperties(data);
    
    Photo photo = Photo.create(uuid, serialized);
    
    assertNotNull(photo);
    assertEquals(uuid, photo.getUniqueId());
  }

  @Test
  void testMultiplePhotos_haveUniqueIds() {
    Photo[] photos = new Photo[10];
    for (int i = 0; i < 10; i++) {
      photos[i] = new Photo();
    }
    
    for (int i = 0; i < 10; i++) {
      for (int j = i + 1; j < 10; j++) {
        assertNotEquals(photos[i].getUniqueId(), photos[j].getUniqueId());
      }
    }
  }
}