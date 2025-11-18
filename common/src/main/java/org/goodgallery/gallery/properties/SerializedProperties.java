package org.goodgallery.gallery.properties;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record SerializedProperties(Map<String, byte[]> serializedData) implements Properties {

  public SerializedProperties () {
    this(new HashMap<>());
  }

  public SerializedProperties(Map<String, byte[]> serializedData) {
    this.serializedData = Collections.unmodifiableMap(serializedData != null ? serializedData : new HashMap<>());
  }

  @Override
  public <T> Optional<T> getValue(PropertyKey<T> key) {
    byte[] data = serializedData.get(key.toString());
    if (data == null)
      return Optional.empty();
    return Optional.of(key.deserialize(data));
  }

}