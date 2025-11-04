package org.goodgallery.gallery.collections;

import org.goodgallery.gallery.Album;
import org.goodgallery.gallery.properties.SerializedProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AlbumCollectionTest {

  private AlbumCollection collection;

  @BeforeEach
  void setUp() {
    collection = new AlbumCollection();
  }

  @Test
  void testConstructor_createsEmptyCollection() {
    assertNotNull(collection);
    assertTrue(collection.getAlbums().isEmpty());
  }

  @Test
  void testAdd_addsAlbumToCollection() {
    Album album = new Album();
    collection.add(album);
    
    assertTrue(collection.has(album));
    assertTrue(collection.has(album.getUniqueId()));
  }

  @Test
  void testCreateAlbum_createsAndAddsAlbum() {
    UUID uuid = UUID.randomUUID();
    HashMap<String, byte[]> data = new HashMap<>();
    data.put("name", "Test Album".getBytes());
    SerializedProperties serialized = new SerializedProperties(data);
    
    collection.createAlbum(uuid, serialized);
    
    assertTrue(collection.has(uuid));
  }

  @Test
  void testHas_withAlbum_returnsTrueIfExists() {
    Album album = new Album();
    collection.add(album);
    
    assertTrue(collection.has(album));
  }

  @Test
  void testHas_withAlbum_returnsFalseIfNotExists() {
    Album album = new Album();
    
    assertFalse(collection.has(album));
  }

  @Test
  void testHas_withUUID_returnsTrueIfExists() {
    Album album = new Album();
    collection.add(album);
    
    assertTrue(collection.has(album.getUniqueId()));
  }

  @Test
  void testHas_withUUID_returnsFalseIfNotExists() {
    UUID uuid = UUID.randomUUID();
    
    assertFalse(collection.has(uuid));
  }

  @Test
  void testHas_withName_returnsTrueIfExists() {
    Album album = new Album();
    collection.add(album);
    
    assertTrue(collection.has(album.getName()));
  }

  @Test
  void testHas_withName_returnsFalseIfNotExists() {
    assertFalse(collection.has("NonExistent Album"));
  }

  @Test
  void testGetAlbums_returnsAllAlbums() {
    Album album1 = new Album();
    Album album2 = new Album();
    
    collection.add(album1);
    collection.add(album2);
    
    Collection<Album> albums = collection.getAlbums();
    assertEquals(2, albums.size());
    assertTrue(albums.contains(album1));
    assertTrue(albums.contains(album2));
  }

  @Test
  void testGetAlbum_byUUID_returnsAlbum() {
    Album album = new Album();
    collection.add(album);
    
    Album retrieved = collection.getAlbum(album.getUniqueId());
    assertSame(album, retrieved);
  }

  @Test
  void testGetAlbum_byUUID_returnsNullIfNotExists() {
    UUID uuid = UUID.randomUUID();
    
    Album retrieved = collection.getAlbum(uuid);
    assertNull(retrieved);
  }

  @Test
  void testGetAlbum_byName_returnsAlbum() {
    Album album = new Album();
    collection.add(album);
    
    Album retrieved = collection.getAlbum(album.getName());
    assertSame(album, retrieved);
  }

  @Test
  void testGetAlbum_byName_returnsNullIfNotExists() {
    Album retrieved = collection.getAlbum("NonExistent");
    assertNull(retrieved);
  }

  @Test
  void testRemove_removesAlbumFromCollection() {
    Album album = new Album();
    collection.add(album);
    
    collection.remove(album);
    
    assertFalse(collection.has(album));
    assertFalse(collection.has(album.getUniqueId()));
    assertFalse(collection.has(album.getName()));
  }

  @Test
  void testRemove_nonExistentAlbum_doesNotThrow() {
    Album album = new Album();
    
    assertDoesNotThrow(() -> collection.remove(album));
  }

  @Test
  void testMultipleAlbums_maintainIndependence() {
    Album album1 = new Album();
    Album album2 = new Album();
    
    collection.add(album1);
    collection.add(album2);
    
    assertTrue(collection.has(album1.getUniqueId()));
    assertTrue(collection.has(album2.getUniqueId()));
    
    collection.remove(album1);
    
    assertFalse(collection.has(album1.getUniqueId()));
    assertTrue(collection.has(album2.getUniqueId()));
  }

  @Test
  void testConcurrentAccess_threadsafe() throws InterruptedException {
    Thread thread1 = new Thread(() -> {
      for (int i = 0; i < 100; i++) {
        Album album = new Album();
        collection.add(album);
      }
    });
    
    Thread thread2 = new Thread(() -> {
      for (int i = 0; i < 100; i++) {
        Album album = new Album();
        collection.add(album);
      }
    });
    
    thread1.start();
    thread2.start();
    thread1.join();
    thread2.join();
    
    assertEquals(200, collection.getAlbums().size());
  }
}