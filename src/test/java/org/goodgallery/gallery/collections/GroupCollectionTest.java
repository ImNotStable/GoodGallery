package org.goodgallery.gallery.collections;

import org.goodgallery.gallery.Group;
import org.goodgallery.gallery.properties.SerializedProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GroupCollectionTest {

  private GroupCollection collection;

  @BeforeEach
  void setUp() {
    collection = new GroupCollection();
  }

  @Test
  void testConstructor_createsEmptyCollection() {
    assertNotNull(collection);
    assertTrue(collection.getGroups().isEmpty());
  }

  @Test
  void testAdd_addsGroupToCollection() {
    Group group = new Group();
    collection.add(group);
    
    assertTrue(collection.has(group));
    assertTrue(collection.has(group.getUniqueId()));
  }

  @Test
  void testCreateGroup_createsAndAddsGroup() {
    UUID uuid = UUID.randomUUID();
    HashMap<String, byte[]> data = new HashMap<>();
    data.put("name", "Test Group".getBytes());
    SerializedProperties serialized = new SerializedProperties(data);
    
    collection.createGroup(uuid, serialized);
    
    assertTrue(collection.has(uuid));
  }

  @Test
  void testHas_withGroup_returnsTrueIfExists() {
    Group group = new Group();
    collection.add(group);
    
    assertTrue(collection.has(group));
  }

  @Test
  void testHas_withGroup_returnsFalseIfNotExists() {
    Group group = new Group();
    
    assertFalse(collection.has(group));
  }

  @Test
  void testHas_withUUID_returnsTrueIfExists() {
    Group group = new Group();
    collection.add(group);
    
    assertTrue(collection.has(group.getUniqueId()));
  }

  @Test
  void testHas_withUUID_returnsFalseIfNotExists() {
    UUID uuid = UUID.randomUUID();
    
    assertFalse(collection.has(uuid));
  }

  @Test
  void testHas_withName_returnsTrueIfExists() {
    Group group = new Group();
    collection.add(group);
    
    assertTrue(collection.has(group.getName()));
  }

  @Test
  void testHas_withName_returnsFalseIfNotExists() {
    assertFalse(collection.has("NonExistent Group"));
  }

  @Test
  void testGetGroups_returnsAllGroups() {
    Group group1 = new Group();
    Group group2 = new Group();
    
    collection.add(group1);
    collection.add(group2);
    
    Collection<Group> groups = collection.getGroups();
    assertEquals(2, groups.size());
    assertTrue(groups.contains(group1));
    assertTrue(groups.contains(group2));
  }

  @Test
  void testGetGroup_byUUID_returnsGroup() {
    Group group = new Group();
    collection.add(group);
    
    Group retrieved = collection.getGroup(group.getUniqueId());
    assertSame(group, retrieved);
  }

  @Test
  void testGetGroup_byUUID_returnsNullIfNotExists() {
    UUID uuid = UUID.randomUUID();
    
    Group retrieved = collection.getGroup(uuid);
    assertNull(retrieved);
  }

  @Test
  void testGetGroup_byName_returnsGroup() {
    Group group = new Group();
    collection.add(group);
    
    Group retrieved = collection.getGroup(group.getName());
    assertSame(group, retrieved);
  }

  @Test
  void testGetGroup_byName_returnsNullIfNotExists() {
    Group retrieved = collection.getGroup("NonExistent");
    assertNull(retrieved);
  }

  @Test
  void testRemove_removesGroupFromCollection() {
    Group group = new Group();
    collection.add(group);
    
    collection.remove(group);
    
    assertFalse(collection.has(group));
    assertFalse(collection.has(group.getUniqueId()));
    assertFalse(collection.has(group.getName()));
  }

  @Test
  void testRemove_nonExistentGroup_doesNotThrow() {
    Group group = new Group();
    
    assertDoesNotThrow(() -> collection.remove(group));
  }

  @Test
  void testMultipleGroups_maintainIndependence() {
    Group group1 = new Group();
    Group group2 = new Group();
    
    collection.add(group1);
    collection.add(group2);
    
    assertTrue(collection.has(group1.getUniqueId()));
    assertTrue(collection.has(group2.getUniqueId()));
    
    collection.remove(group1);
    
    assertFalse(collection.has(group1.getUniqueId()));
    assertTrue(collection.has(group2.getUniqueId()));
  }

  @Test
  void testConcurrentAccess_threadsafe() throws InterruptedException {
    Thread thread1 = new Thread(() -> {
      for (int i = 0; i < 100; i++) {
        Group group = new Group();
        collection.add(group);
      }
    });
    
    Thread thread2 = new Thread(() -> {
      for (int i = 0; i < 100; i++) {
        Group group = new Group();
        collection.add(group);
      }
    });
    
    thread1.start();
    thread2.start();
    thread1.join();
    thread2.join();
    
    assertEquals(200, collection.getGroups().size());
  }
}