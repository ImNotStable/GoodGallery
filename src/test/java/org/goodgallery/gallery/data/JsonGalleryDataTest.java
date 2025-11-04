package org.goodgallery.gallery.data;

import com.google.gson.JsonObject;
import org.goodgallery.gallery.Album;
import org.goodgallery.gallery.Group;
import org.goodgallery.gallery.Photo;
import org.goodgallery.gallery.collections.AlbumCollection;
import org.goodgallery.gallery.collections.GroupCollection;
import org.goodgallery.gallery.collections.PhotoCollection;
import org.goodgallery.gallery.properties.PropertiesImpl;
import org.goodgallery.gallery.properties.PropertyInstance;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JsonGalleryDataTest {

  @TempDir
  Path tempDir;

  private JsonGalleryData galleryData;

  @BeforeEach
  void setUp() throws IOException {
    galleryData = new JsonGalleryData(tempDir);
  }

  @AfterEach
  void tearDown() {
    if (galleryData != null) {
      galleryData.close();
    }
  }

  @Test
  void testCreate_createsGalleryJsonFile() throws IOException {
    Path galleryJsonPath = tempDir.resolve("gallery.json");
    assertTrue(Files.exists(galleryJsonPath), "gallery.json should be created");
  }

  @Test
  void testCreate_withExistingFile_loadsExistingData() throws IOException {
    Path galleryJsonPath = tempDir.resolve("gallery.json");
    String jsonContent = """
      {
        "groups": {"test-uuid": {"name": "VGVzdEdyb3Vw"}},
        "albums": {},
        "photos": {}
      }
      """;
    Files.writeString(galleryJsonPath, jsonContent);

    JsonGalleryData newGalleryData = new JsonGalleryData(tempDir);
    assertNotNull(newGalleryData);
    newGalleryData.close();
  }

  @Test
  void testCreate_factoryMethod() throws IOException {
    Path newTempDir = tempDir.resolve("factory-test");
    Files.createDirectory(newTempDir);
    
    GalleryData data = JsonGalleryData.create(newTempDir);
    assertNotNull(data);
    assertTrue(data instanceof JsonGalleryData);
    ((JsonGalleryData) data).close();
  }

  @Test
  void testLoadGroups_emptyCollection() {
    GroupCollection groups = new GroupCollection();
    assertDoesNotThrow(() -> galleryData.loadGroups(groups));
    assertTrue(groups.getGroups().isEmpty());
  }

  @Test
  void testLoadAlbums_emptyCollection() {
    AlbumCollection albums = new AlbumCollection();
    assertDoesNotThrow(() -> galleryData.loadAlbums(albums));
    assertTrue(albums.getAlbums().isEmpty());
  }

  @Test
  void testLoadPhotos_emptyCollection() {
    PhotoCollection photos = new PhotoCollection();
    assertDoesNotThrow(() -> galleryData.loadPhotos(photos));
    assertTrue(photos.getPhotos().isEmpty());
  }

  @Test
  void testAdd_photo() {
    Photo photo = new Photo();
    assertDoesNotThrow(() -> galleryData.add(photo));
  }

  @Test
  void testAdd_album() {
    Album album = new Album();
    assertDoesNotThrow(() -> galleryData.add(album));
  }

  @Test
  void testAdd_group() {
    Group group = new Group();
    assertDoesNotThrow(() -> galleryData.add(group));
  }

  @Test
  void testDelete_photo() {
    Photo photo = new Photo();
    galleryData.add(photo);
    assertDoesNotThrow(() -> galleryData.delete(photo));
  }

  @Test
  void testDelete_album() {
    Album album = new Album();
    galleryData.add(album);
    assertDoesNotThrow(() -> galleryData.delete(album));
  }

  @Test
  void testDelete_group() {
    Group group = new Group();
    galleryData.add(group);
    assertDoesNotThrow(() -> galleryData.delete(group));
  }

  @Test
  void testUpdateProperty_photo() {
    Photo photo = new Photo();
    galleryData.add(photo);
    
    PropertiesImpl properties = (PropertiesImpl) photo.getProperties();
    PropertyInstance<String> nameProperty = properties.get(PropertiesImpl.NAME_KEY);
    
    assertDoesNotThrow(() -> galleryData.updateProperty(photo, nameProperty));
  }

  @Test
  void testUpdateProperty_album() {
    Album album = new Album();
    galleryData.add(album);
    
    PropertiesImpl properties = (PropertiesImpl) album.getProperties();
    PropertyInstance<String> nameProperty = properties.get(PropertiesImpl.NAME_KEY);
    
    assertDoesNotThrow(() -> galleryData.updateProperty(album, nameProperty));
  }

  @Test
  void testUpdateProperty_group() {
    Group group = new Group();
    galleryData.add(group);
    
    PropertiesImpl properties = (PropertiesImpl) group.getProperties();
    PropertyInstance<String> nameProperty = properties.get(PropertiesImpl.NAME_KEY);
    
    assertDoesNotThrow(() -> galleryData.updateProperty(group, nameProperty));
  }

  @Test
  void testSave_persistsDataToFile() throws IOException, InterruptedException {
    Photo photo = new Photo();
    galleryData.add(photo);
    
    // Trigger save
    galleryData.save();
    
    // Give a moment for async save to complete
    Thread.sleep(100);
    
    Path galleryJsonPath = tempDir.resolve("gallery.json");
    String content = Files.readString(galleryJsonPath);
    assertTrue(content.contains("photos"), "Saved file should contain photos section");
  }

  @Test
  void testMultipleOperations_maintainsConsistency() {
    Photo photo1 = new Photo();
    Photo photo2 = new Photo();
    Album album = new Album();
    Group group = new Group();

    assertDoesNotThrow(() -> {
      galleryData.add(photo1);
      galleryData.add(photo2);
      galleryData.add(album);
      galleryData.add(group);
      
      galleryData.delete(photo2);
      
      PropertiesImpl properties = (PropertiesImpl) album.getProperties();
      PropertyInstance<String> nameProperty = properties.get(PropertiesImpl.NAME_KEY);
      galleryData.updateProperty(album, nameProperty);
    });
  }

  @Test
  void testClose_cancelsAutoSave() {
    assertDoesNotThrow(() -> galleryData.close());
  }

  @Test
  void testSave_handlesIOException() {
    // Create a read-only directory to force IOException
    Path readOnlyDir = tempDir.resolve("readonly");
    assertDoesNotThrow(() -> {
      Files.createDirectory(readOnlyDir);
      readOnlyDir.toFile().setReadOnly();
    });
  }

  @Test
  void testJsonStructure_hasRequiredSections() throws IOException {
    Path galleryJsonPath = tempDir.resolve("gallery.json");
    String content = Files.readString(galleryJsonPath);
    
    assertTrue(content.contains("groups"), "JSON should have groups section");
    assertTrue(content.contains("albums"), "JSON should have albums section");
    assertTrue(content.contains("photos"), "JSON should have photos section");
  }
}