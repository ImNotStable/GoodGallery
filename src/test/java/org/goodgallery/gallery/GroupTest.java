package org.goodgallery.gallery;

import org.goodgallery.gallery.properties.SerializedProperties;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GroupTest {

  @Test
  void testConstructor_generatesUniqueId() {
    Group group1 = new Group();
    Group group2 = new Group();
    
    assertNotNull(group1.getUniqueId());
    assertNotNull(group2.getUniqueId());
    assertNotEquals(group1.getUniqueId(), group2.getUniqueId());
  }

  @Test
  void testConstructor_withSerializedProperties() {
    UUID uuid = UUID.randomUUID();
    HashMap<String, byte[]> data = new HashMap<>();
    data.put("name", "Family Group".getBytes());
    SerializedProperties serialized = new SerializedProperties(data);
    
    Group group = Group.create(uuid, serialized);
    
    assertNotNull(group);
    assertEquals(uuid, group.getUniqueId());
  }

  @Test
  void testGetProperties_returnsProperties() {
    Group group = new Group();
    
    assertNotNull(group.getProperties());
  }

  @Test
  void testGetName_returnsName() {
    Group group = new Group();
    
    assertNotNull(group.getName());
  }

  @Test
  void testGetAlbums_returnsEmptyCollectionByDefault() {
    Group group = new Group();
    
    Collection<Album> albums = group.getAlbums();
    assertNotNull(albums);
    assertTrue(albums.isEmpty());
  }

  @Test
  void testGetAlbums_returnsUnmodifiableCollection() {
    Group group = new Group();
    
    Collection<Album> albums = group.getAlbums();
    assertThrows(UnsupportedOperationException.class, () -> {
      albums.add(new Album());
    });
  }

  @Test
  void testToString_returnsUniqueIdString() {
    Group group = new Group();
    
    assertEquals(group.getUniqueId().toString(), group.toString());
  }

  @Test
  void testCreate_factoryMethod() {
    UUID uuid = UUID.randomUUID();
    HashMap<String, byte[]> data = new HashMap<>();
    data.put("name", "Test Group".getBytes());
    SerializedProperties serialized = new SerializedProperties(data);
    
    Group group = Group.create(uuid, serialized);
    
    assertNotNull(group);
    assertEquals(uuid, group.getUniqueId());
    assertEquals("Test Group", group.getName());
  }

  @Test
  void testMultipleGroups_haveUniqueIds() {
    Group[] groups = new Group[10];
    for (int i = 0; i < 10; i++) {
      groups[i] = new Group();
    }
    
    for (int i = 0; i < 10; i++) {
      for (int j = i + 1; j < 10; j++) {
        assertNotEquals(groups[i].getUniqueId(), groups[j].getUniqueId());
      }
    }
  }
}