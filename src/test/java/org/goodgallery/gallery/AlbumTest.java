package org.goodgallery.gallery;

import org.goodgallery.gallery.properties.SerializedProperties;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AlbumTest {

  @Test
  void testConstructor_generatesUniqueId() {
    Album album1 = new Album();
    Album album2 = new Album();
    
    assertNotNull(album1.getUniqueId());
    assertNotNull(album2.getUniqueId());
    assertNotEquals(album1.getUniqueId(), album2.getUniqueId());
  }

  @Test
  void testConstructor_withSerializedProperties() {
    UUID uuid = UUID.randomUUID();
    HashMap<String, byte[]> data = new HashMap<>();
    data.put("name", "Vacation Album".getBytes());
    SerializedProperties serialized = new SerializedProperties(data);
    
    Album album = Album.create(uuid, serialized);
    
    assertNotNull(album);
    assertEquals(uuid, album.getUniqueId());
  }

  @Test
  void testGetProperties_returnsProperties() {
    Album album = new Album();
    
    assertNotNull(album.getProperties());
  }

  @Test
  void testGetName_returnsName() {
    Album album = new Album();
    
    assertNotNull(album.getName());
  }

  @Test
  void testGetPhotos_returnsEmptyCollectionByDefault() {
    Album album = new Album();
    
    Collection<Photo> photos = album.getPhotos();
    assertNotNull(photos);
    assertTrue(photos.isEmpty());
  }

  @Test
  void testGetPhotos_returnsUnmodifiableCollection() {
    Album album = new Album();
    
    Collection<Photo> photos = album.getPhotos();
    assertThrows(UnsupportedOperationException.class, () -> {
      photos.add(new Photo());
    });
  }

  @Test
  void testToString_returnsUniqueIdString() {
    Album album = new Album();
    
    assertEquals(album.getUniqueId().toString(), album.toString());
  }

  @Test
  void testCreate_factoryMethod() {
    UUID uuid = UUID.randomUUID();
    HashMap<String, byte[]> data = new HashMap<>();
    data.put("name", "Test Album".getBytes());
    SerializedProperties serialized = new SerializedProperties(data);
    
    Album album = Album.create(uuid, serialized);
    
    assertNotNull(album);
    assertEquals(uuid, album.getUniqueId());
    assertEquals("Test Album", album.getName());
  }

  @Test
  void testMultipleAlbums_haveUniqueIds() {
    Album[] albums = new Album[10];
    for (int i = 0; i < 10; i++) {
      albums[i] = new Album();
    }
    
    for (int i = 0; i < 10; i++) {
      for (int j = i + 1; j < 10; j++) {
        assertNotEquals(albums[i].getUniqueId(), albums[j].getUniqueId());
      }
    }
  }
}