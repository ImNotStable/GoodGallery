package org.goodgallery.gallery.data;

import org.goodgallery.gallery.collections.AlbumCollection;
import org.goodgallery.gallery.collections.GroupCollection;
import org.goodgallery.gallery.collections.PhotoCollection;
import org.goodgallery.gallery.properties.PropertyHolder;
import org.goodgallery.gallery.properties.PropertyInstance;

public interface GalleryData {

  /**
 * Loads the given collection of groups into the gallery data model.
 *
 * @param groups the collection of groups to load into the data model
 */
void loadGroups(GroupCollection groups);

  /**
 * Loads the given album collection into the gallery data model.
 *
 * @param albums the collection of albums to load into the data model
 */
void loadAlbums(AlbumCollection albums);

  /**
 * Loads a collection of photos into the gallery data model.
 *
 * @param photos the collection of photos to load
 */
void loadPhotos(PhotoCollection photos);

  /**
 * Adds the given PropertyHolder to the gallery data model.
 *
 * @param propertyHolder the property holder to add to the data model
 */
void add(PropertyHolder propertyHolder);

  /**
 * Removes the given property holder from the gallery data model.
 *
 * @param propertyHolder the property holder to remove
 */
void delete(PropertyHolder propertyHolder);

  /**
 * Apply the given PropertyInstance's value to the specified PropertyHolder.
 *
 * @param propertyHolder the holder (for example, a group, album, or photo) whose property will be updated
 * @param property the PropertyInstance containing the new value to set on the holder
 */
void updateProperty(PropertyHolder propertyHolder, PropertyInstance<?> property);

}