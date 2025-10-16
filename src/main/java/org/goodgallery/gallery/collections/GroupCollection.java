package org.goodgallery.gallery.collections;

import com.google.gson.JsonObject;
import org.goodgallery.gallery.Album;
import org.goodgallery.gallery.Group;
import org.goodgallery.gallery.properties.SerializedProperties;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class GroupCollection {

  private final Map<UUID, Group> groupsByUUID;
  private final Map<String, Group> groupsByName;

  public GroupCollection() {
    groupsByUUID = new ConcurrentHashMap<>();
    groupsByName = new ConcurrentHashMap<>();
  }

  public void createGroup(UUID uniqueId, SerializedProperties serializedProperties, Album... albums) {
    add(Group.create(uniqueId, serializedProperties, albums));
  }

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

  public void remove(Group group) {
    groupsByUUID.remove(group.getUniqueId());
    groupsByName.remove(group.getName());
  }

}
