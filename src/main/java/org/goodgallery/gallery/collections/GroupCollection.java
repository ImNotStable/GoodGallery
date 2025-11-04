package org.goodgallery.gallery.collections;

import org.goodgallery.gallery.Group;
import org.goodgallery.gallery.properties.SerializedProperties;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class GroupCollection {

  private final Map<UUID, Group> groupsByUUID;
  private final Map<String, Group> groupsByName;

  /**
   * Constructs an empty GroupCollection with concurrent maps for lookup by UUID and by name.
   */
  public GroupCollection() {
    groupsByUUID = new ConcurrentHashMap<>();
    groupsByName = new ConcurrentHashMap<>();
  }

  /**
   * Create a Group from the provided serialized properties and add it to this collection.
   *
   * @param uniqueId the UUID to assign to the created Group
   * @param serializedProperties serialized data used to construct the Group; the resulting Group will be added to and indexed by this collection
   */
  public void createGroup(UUID uniqueId, SerializedProperties serializedProperties) {
    add(Group.create(uniqueId, serializedProperties));
  }

  /**
   * Adds the given Group to this collection and indexes it by its UUID and name.
   *
   * <p>Inserts or replaces entries in the internal maps so the group is retrievable
   * by both its unique identifier and its name.</p>
   *
   * @param group the Group to add and index by UUID and name
   */
  public void add(Group group) {
    groupsByUUID.put(group.getUniqueId(), group);
    groupsByName.put(group.getName(), group);
  }

  public boolean has(Group group) {
    return has(group.getUniqueId());
  }

  public boolean has(UUID uniqueId) {
    return groupsByUUID.containsKey(uniqueId);
  }

  public boolean has(String name) {
    return groupsByName.containsKey(name);
  }

  public Collection<Group> getGroups() {
    return groupsByUUID.values();
  }

  public Group getGroup(UUID uniqueId) {
    return groupsByUUID.get(uniqueId);
  }

  public Group getGroup(String name) {
    return groupsByName.get(name);
  }

  /**
   * Remove the specified Group from the collection.
   *
   * Removes entries for the group's UUID and name from the internal indexes.
   *
   * @param group the Group whose UUID and name will be removed from the collection
   */
  public void remove(Group group) {
    groupsByUUID.remove(group.getUniqueId());
    groupsByName.remove(group.getName());
  }

}