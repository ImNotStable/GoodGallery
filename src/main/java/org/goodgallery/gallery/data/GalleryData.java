package org.goodgallery.gallery.data;

import org.goodgallery.gallery.collections.AlbumCollection;
import org.goodgallery.gallery.collections.GroupCollection;
import org.goodgallery.gallery.collections.PhotoCollection;
import org.goodgallery.gallery.properties.PropertyHolder;
import org.goodgallery.gallery.properties.PropertyInstance;

public interface GalleryData {

  void loadGroups(GroupCollection groups);

  void loadAlbums(AlbumCollection albums);

  void loadPhotos(PhotoCollection photos);

  void add(PropertyHolder propertyHolder);

  void delete(PropertyHolder propertyHolder);

  void updateProperty(PropertyHolder propertyHolder, PropertyInstance<?> property);

}
