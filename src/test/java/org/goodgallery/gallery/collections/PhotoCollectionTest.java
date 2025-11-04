package org.goodgallery.gallery.collections;

import org.goodgallery.gallery.Photo;
import org.goodgallery.gallery.properties.PropertiesImpl;
import org.goodgallery.gallery.properties.SerializedProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PhotoCollectionTest {

  private PhotoCollection collection;

  @BeforeEach
  void setUp() {
    collection = new PhotoCollection();
  }

  @Test
  void testConstructor_createsEmptyCollection() {
    assertNotNull(collection);
    assertTrue(collection.getPhotos().isEmpty());
  }

  @Test
  void testAdd_addsPhotoToCollection() {
    Photo photo = new Photo();
    collection.add(photo);
    
    assertTrue(collection.has(photo));
    assertTrue(collection.has(photo.getUniqueId()));
  }

  @Test
  void testCreatePhoto_createsAndAddsPhoto() {
    UUID uuid = UUID.randomUUID();
    HashMap<String, byte[]> data = new HashMap<>();
    data.put("path", "/test/photo.jpg".getBytes());
    data.put("name", "photo.jpg".getBytes());
    SerializedProperties serialized = new SerializedProperties(data);
    
    collection.createPhoto(uuid, serialized);
    
    assertTrue(collection.has(uuid));
  }

  @Test
  void testHas_withPhoto_returnsTrueIfExists() {
    Photo photo = new Photo();
    collection.add(photo);
    
    assertTrue(collection.has(photo));
  }

  @Test
  void testHas_withPhoto_returnsFalseIfNotExists() {
    Photo photo = new Photo();
    
    assertFalse(collection.has(photo));
  }

  @Test
  void testHas_withUUID_returnsTrueIfExists() {
    Photo photo = new Photo();
    collection.add(photo);
    
    assertTrue(collection.has(photo.getUniqueId()));
  }

  @Test
  void testHas_withUUID_returnsFalseIfNotExists() {
    UUID uuid = UUID.randomUUID();
    
    assertFalse(collection.has(uuid));
  }

  @Test
  void testHas_withName_returnsTrueIfExists() {
    Photo photo = new Photo();
    collection.add(photo);
    
    assertTrue(collection.has(photo.getName()));
  }

  @Test
  void testHas_withName_returnsFalseIfNotExists() {
    assertFalse(collection.has("nonexistent.jpg"));
  }

  @Test
  void testGetPhotos_returnsAllPhotos() {
    Photo photo1 = new Photo();
    Photo photo2 = new Photo();
    
    collection.add(photo1);
    collection.add(photo2);
    
    Collection<Photo> photos = collection.getPhotos();
    assertEquals(2, photos.size());
    assertTrue(photos.contains(photo1));
    assertTrue(photos.contains(photo2));
  }

  @Test
  void testGetPhoto_byUUID_returnsPhoto() {
    Photo photo = new Photo();
    collection.add(photo);
    
    Photo retrieved = collection.getPhoto(photo.getUniqueId());
    assertSame(photo, retrieved);
  }

  @Test
  void testGetPhoto_byUUID_returnsNullIfNotExists() {
    UUID uuid = UUID.randomUUID();
    
    Photo retrieved = collection.getPhoto(uuid);
    assertNull(retrieved);
  }

  @Test
  void testGetPhoto_byName_returnsPhoto() {
    Photo photo = new Photo();
    collection.add(photo);
    
    Photo retrieved = collection.getPhoto(photo.getName());
    assertSame(photo, retrieved);
  }

  @Test
  void testGetPhoto_byName_returnsNullIfNotExists() {
    Photo retrieved = collection.getPhoto("nonexistent.jpg");
    assertNull(retrieved);
  }

  @Test
  void testRemove_removesPhotoFromCollection() {
    Photo photo = new Photo();
    collection.add(photo);
    
    collection.remove(photo);
    
    assertFalse(collection.has(photo));
    assertFalse(collection.has(photo.getUniqueId()));
    assertFalse(collection.has(photo.getName()));
  }

  @Test
  void testRemove_nonExistentPhoto_doesNotThrow() {
    Photo photo = new Photo();
    
    assertDoesNotThrow(() -> collection.remove(photo));
  }

  @Test
  void testMultiplePhotos_maintainIndependence() {
    Photo photo1 = new Photo();
    Photo photo2 = new Photo();
    
    collection.add(photo1);
    collection.add(photo2);
    
    assertTrue(collection.has(photo1.getUniqueId()));
    assertTrue(collection.has(photo2.getUniqueId()));
    
    collection.remove(photo1);
    
    assertFalse(collection.has(photo1.getUniqueId()));
    assertTrue(collection.has(photo2.getUniqueId()));
  }

  @Test
  void testConcurrentAccess_threadsafe() throws InterruptedException {
    // PhotoCollection uses ConcurrentHashMap, so it should be thread-safe
    Thread thread1 = new Thread(() -> {
      for (int i = 0; i < 100; i++) {
        Photo photo = new Photo();
        collection.add(photo);
      }
    });
    
    Thread thread2 = new Thread(() -> {
      for (int i = 0; i < 100; i++) {
        Photo photo = new Photo();
        collection.add(photo);
      }
    });
    
    thread1.start();
    thread2.start();
    thread1.join();
    thread2.join();
    
    assertEquals(200, collection.getPhotos().size());
  }
}