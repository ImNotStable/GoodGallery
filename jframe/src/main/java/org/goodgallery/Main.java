package org.goodgallery;

import org.goodgallery.gallery.Album;
import org.goodgallery.gallery.Gallery;
import org.goodgallery.gallery.GalleryInstance;
import org.goodgallery.gallery.Group;
import org.goodgallery.gallery.Photo;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main extends JFrame {
  private static final Path GALLERY_PATH = Paths.get("./gallery");
  private Gallery gallery;

  // GUI Components
  private JTabbedPane tabbedPane;
  private DefaultListModel<Photo> photoListModel;
  private JList<Photo> photoList;
  private DefaultListModel<Album> albumListModel;
  private JList<Album> albumList;
  private DefaultListModel<Group> groupListModel;
  private JList<Group> groupList;
  private JLabel photoPreview;
  private JLabel statusLabel;

  public Main() {
    try {
      GalleryInstance.init(GALLERY_PATH);
      gallery = GalleryInstance.get();
    } catch (IOException e) {
      JOptionPane.showMessageDialog(this, "Failed to initialize gallery: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      System.exit(1);
    }

    initializeGUI();
    refreshAllLists();
  }

  private void initializeGUI() {
    setTitle("Good Gallery");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());

    // Create tabbed pane
    tabbedPane = new JTabbedPane();

    // Photos tab
    tabbedPane.addTab("Photos", createPhotosPanel());

    // Albums tab
    tabbedPane.addTab("Albums", createAlbumsPanel());

    // Groups tab
    tabbedPane.addTab("Groups", createGroupsPanel());

    add(tabbedPane, BorderLayout.CENTER);

    // Status bar
    statusLabel = new JLabel("Ready");
    statusLabel.setBorder(BorderFactory.createLoweredBevelBorder());
    add(statusLabel, BorderLayout.SOUTH);

    setSize(1000, 700);
    setLocationRelativeTo(null);
  }

  private JPanel createPhotosPanel() {
    JPanel panel = new JPanel(new BorderLayout());

    // Left side - photo list and controls
    JPanel leftPanel = new JPanel(new BorderLayout());

    photoListModel = new DefaultListModel<>();
    photoList = new JList<>(photoListModel);
    photoList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    photoList.addListSelectionListener(e -> {
      if (!e.getValueIsAdjusting()) {
        displayPhotoPreview();
      }
    });

    JScrollPane photoScrollPane = new JScrollPane(photoList);
    photoScrollPane.setPreferredSize(new Dimension(300, 0));
    leftPanel.add(photoScrollPane, BorderLayout.CENTER);

    // Photo controls
    JPanel photoControls = new JPanel(new GridLayout(0, 1, 5, 5));
    photoControls.setBorder(BorderFactory.createTitledBorder("Photo Actions"));

    JButton copyPhotoBtn = new JButton("Copy Photo");
    copyPhotoBtn.addActionListener(this::copyPhoto);
    photoControls.add(copyPhotoBtn);

    JButton cutPhotoBtn = new JButton("Cut Photo");
    cutPhotoBtn.addActionListener(this::cutPhoto);
    photoControls.add(cutPhotoBtn);

    JButton renamePhotoBtn = new JButton("Rename Photo");
    renamePhotoBtn.addActionListener(this::renamePhoto);
    photoControls.add(renamePhotoBtn);

    JButton deletePhotoBtn = new JButton("Delete Photo");
    deletePhotoBtn.addActionListener(this::deletePhoto);
    photoControls.add(deletePhotoBtn);

    JButton viewPhotoBtn = new JButton("View Full Size");
    viewPhotoBtn.addActionListener(this::viewPhoto);
    photoControls.add(viewPhotoBtn);

    leftPanel.add(photoControls, BorderLayout.SOUTH);

    // Right side - photo preview
    photoPreview = new JLabel("Select a photo to preview", SwingConstants.CENTER);
    photoPreview.setBorder(BorderFactory.createTitledBorder("Preview"));
    photoPreview.setPreferredSize(new Dimension(400, 0));

    panel.add(leftPanel, BorderLayout.WEST);
    panel.add(photoPreview, BorderLayout.CENTER);

    return panel;
  }

  private JPanel createAlbumsPanel() {
    JPanel panel = new JPanel(new BorderLayout());

    // Left side - album list and controls
    JPanel leftPanel = new JPanel(new BorderLayout());

    albumListModel = new DefaultListModel<>();
    albumList = new JList<>(albumListModel);
    albumList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    JScrollPane albumScrollPane = new JScrollPane(albumList);
    albumScrollPane.setPreferredSize(new Dimension(300, 0));
    leftPanel.add(albumScrollPane, BorderLayout.CENTER);

    // Album controls
    JPanel albumControls = new JPanel(new GridLayout(0, 1, 5, 5));
    albumControls.setBorder(BorderFactory.createTitledBorder("Album Actions"));

    JButton createAlbumBtn = new JButton("Create Album");
    createAlbumBtn.addActionListener(this::createAlbum);
    albumControls.add(createAlbumBtn);

    JButton renameAlbumBtn = new JButton("Rename Album");
    renameAlbumBtn.addActionListener(this::renameAlbum);
    albumControls.add(renameAlbumBtn);

    JButton deleteAlbumBtn = new JButton("Delete Album");
    deleteAlbumBtn.addActionListener(this::deleteAlbum);
    albumControls.add(deleteAlbumBtn);

    JButton addPhotoToAlbumBtn = new JButton("Add Photo to Album");
    addPhotoToAlbumBtn.addActionListener(this::addPhotoToAlbum);
    albumControls.add(addPhotoToAlbumBtn);

    JButton removePhotoFromAlbumBtn = new JButton("Remove Photo from Album");
    removePhotoFromAlbumBtn.addActionListener(this::removePhotoFromAlbum);
    albumControls.add(removePhotoFromAlbumBtn);

    leftPanel.add(albumControls, BorderLayout.SOUTH);

    // Right side - album contents
    JPanel rightPanel = new JPanel(new BorderLayout());
    rightPanel.setBorder(BorderFactory.createTitledBorder("Album Contents"));

    DefaultListModel<Photo> albumPhotosModel = new DefaultListModel<>();
    JList<Photo> albumPhotosList = new JList<>(albumPhotosModel);

    albumList.addListSelectionListener(e -> {
      if (!e.getValueIsAdjusting()) {
        Album selectedAlbum = albumList.getSelectedValue();
        albumPhotosModel.clear();
        if (selectedAlbum != null) {
          for (Photo photo : selectedAlbum.getPhotos()) {
            albumPhotosModel.addElement(photo);
          }
        }
      }
    });

    rightPanel.add(new JScrollPane(albumPhotosList), BorderLayout.CENTER);

    panel.add(leftPanel, BorderLayout.WEST);
    panel.add(rightPanel, BorderLayout.CENTER);

    return panel;
  }

  private JPanel createGroupsPanel() {
    JPanel panel = new JPanel(new BorderLayout());

    // Left side - group list and controls
    JPanel leftPanel = new JPanel(new BorderLayout());

    groupListModel = new DefaultListModel<>();
    groupList = new JList<>(groupListModel);
    groupList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    JScrollPane groupScrollPane = new JScrollPane(groupList);
    groupScrollPane.setPreferredSize(new Dimension(300, 0));
    leftPanel.add(groupScrollPane, BorderLayout.CENTER);

    // Group controls
    JPanel groupControls = new JPanel(new GridLayout(0, 1, 5, 5));
    groupControls.setBorder(BorderFactory.createTitledBorder("Group Actions"));

    JButton createGroupBtn = new JButton("Create Group");
    createGroupBtn.addActionListener(this::createGroup);
    groupControls.add(createGroupBtn);

    JButton deleteGroupBtn = new JButton("Delete Group");
    deleteGroupBtn.addActionListener(this::deleteGroup);
    groupControls.add(deleteGroupBtn);

    JButton moveAlbumToGroupBtn = new JButton("Move Album to Group");
    moveAlbumToGroupBtn.addActionListener(this::moveAlbumToGroup);
    groupControls.add(moveAlbumToGroupBtn);

    leftPanel.add(groupControls, BorderLayout.SOUTH);

    // Right side - group contents
    JPanel rightPanel = new JPanel(new BorderLayout());
    rightPanel.setBorder(BorderFactory.createTitledBorder("Group Albums"));

    DefaultListModel<Album> groupAlbumsModel = new DefaultListModel<>();
    JList<Album> groupAlbumsList = new JList<>(groupAlbumsModel);

    groupList.addListSelectionListener(e -> {
      if (!e.getValueIsAdjusting()) {
        Group selectedGroup = groupList.getSelectedValue();
        groupAlbumsModel.clear();
        if (selectedGroup != null) {
          for (Album album : selectedGroup.getAlbums()) {
            groupAlbumsModel.addElement(album);
          }
        }
      }
    });

    rightPanel.add(new JScrollPane(groupAlbumsList), BorderLayout.CENTER);

    panel.add(leftPanel, BorderLayout.WEST);
    panel.add(rightPanel, BorderLayout.CENTER);

    return panel;
  }

  // Photo operations
  private void copyPhoto(ActionEvent e) {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif", "bmp"));

    if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      try {
        Path sourcePath = fileChooser.getSelectedFile().toPath();
        gallery.copyPhoto(sourcePath);
        refreshAllLists();
        setStatus("Photo copied successfully");
      } catch (IOException ex) {
        showError("Failed to copy photo", ex);
      }
    }
  }

  private void cutPhoto(ActionEvent e) {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif", "bmp"));

    if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      try {
        Path sourcePath = fileChooser.getSelectedFile().toPath();
        gallery.cutPhoto(sourcePath);
        refreshAllLists();
        setStatus("Photo moved successfully");
      } catch (IOException ex) {
        showError("Failed to move photo", ex);
      }
    }
  }

  private void renamePhoto(ActionEvent e) {
    Photo selectedPhoto = photoList.getSelectedValue();
    if (selectedPhoto == null) {
      JOptionPane.showMessageDialog(this, "Please select a photo to rename", "No Selection", JOptionPane.WARNING_MESSAGE);
      return;
    }

    String newName = JOptionPane.showInputDialog(this, "Enter new name:", selectedPhoto.getFileName());
    if (newName != null && !newName.trim().isEmpty()) {
      gallery.renamePhoto(selectedPhoto, newName.trim());
      refreshAllLists();
      setStatus("Photo renamed successfully");
    }
  }

  private void deletePhoto(ActionEvent e) {
    Photo selectedPhoto = photoList.getSelectedValue();
    if (selectedPhoto == null) {
      JOptionPane.showMessageDialog(this, "Please select a photo to delete", "No Selection", JOptionPane.WARNING_MESSAGE);
      return;
    }

    int result = JOptionPane.showConfirmDialog(this,
      "Are you sure you want to delete '" + selectedPhoto.getFileName() + "'?",
      "Confirm Delete", JOptionPane.YES_NO_OPTION);

    if (result == JOptionPane.YES_OPTION) {
      try {
        gallery.deletePhoto(selectedPhoto);
        refreshAllLists();
        setStatus("Photo deleted successfully");
      } catch (IOException ex) {
        showError("Failed to delete photo", ex);
      }
    }
  }

  private void viewPhoto(ActionEvent e) {
    Photo selectedPhoto = photoList.getSelectedValue();
    if (selectedPhoto == null) {
      JOptionPane.showMessageDialog(this, "Please select a photo to view", "No Selection", JOptionPane.WARNING_MESSAGE);
      return;
    }

    try {
      BufferedImage originalImage = ImageIO.read(selectedPhoto.getPath().toFile());

      JFrame photoFrame = new JFrame("Photo Viewer - " + selectedPhoto.getFileName());
      photoFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      photoFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      double scale = Math.min(
        (double) screenSize.width / originalImage.getWidth(),
        (double) screenSize.height / originalImage.getHeight()
      );

      Image scaledImage = originalImage.getScaledInstance(
        (int) (originalImage.getWidth() * scale),
        (int) (originalImage.getHeight() * scale),
        Image.SCALE_SMOOTH
      );

      JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
      imageLabel.setHorizontalAlignment(JLabel.CENTER);
      imageLabel.setVerticalAlignment(JLabel.CENTER);

      photoFrame.add(new JScrollPane(imageLabel), BorderLayout.CENTER);
      photoFrame.setVisible(true);

    } catch (IOException ex) {
      showError("Failed to view photo", ex);
    }
  }

  // Album operations
  private void createAlbum(ActionEvent e) {
    String albumName = JOptionPane.showInputDialog(this, "Enter album name:");
    if (albumName != null && !albumName.trim().isEmpty()) {
      gallery.createAlbum(albumName.trim());
      refreshAllLists();
      setStatus("Album created successfully");
    }
  }

  private void renameAlbum(ActionEvent e) {
    Album selectedAlbum = albumList.getSelectedValue();
    if (selectedAlbum == null) {
      JOptionPane.showMessageDialog(this, "Please select an album to rename", "No Selection", JOptionPane.WARNING_MESSAGE);
      return;
    }

    String newName = JOptionPane.showInputDialog(this, "Enter new name:", selectedAlbum.getName());
    if (newName != null && !newName.trim().isEmpty()) {
      gallery.renameAlbum(selectedAlbum, newName.trim());
      refreshAllLists();
      setStatus("Album renamed successfully");
    }
  }

  private void deleteAlbum(ActionEvent e) {
    Album selectedAlbum = albumList.getSelectedValue();
    if (selectedAlbum == null) {
      JOptionPane.showMessageDialog(this, "Please select an album to delete", "No Selection", JOptionPane.WARNING_MESSAGE);
      return;
    }

    int result = JOptionPane.showConfirmDialog(this,
      "Are you sure you want to delete album '" + selectedAlbum.getName() + "'?",
      "Confirm Delete", JOptionPane.YES_NO_OPTION);

    if (result == JOptionPane.YES_OPTION) {
      gallery.deleteAlbum(selectedAlbum);
      refreshAllLists();
      setStatus("Album deleted successfully");
    }
  }

  private void addPhotoToAlbum(ActionEvent e) {
    Album selectedAlbum = albumList.getSelectedValue();
    if (selectedAlbum == null) {
      JOptionPane.showMessageDialog(this, "Please select an album first", "No Selection", JOptionPane.WARNING_MESSAGE);
      return;
    }

    // Show photo selection dialog
    List<Photo> availablePhotos = new ArrayList<>(gallery.getPhotos());
    Photo[] photoArray = availablePhotos.toArray(new Photo[0]);

    Photo selectedPhoto = (Photo) JOptionPane.showInputDialog(this,
      "Select a photo to add to album:", "Add Photo",
      JOptionPane.QUESTION_MESSAGE, null, photoArray, null);

    if (selectedPhoto != null) {
      gallery.addPhotoToAlbum(selectedPhoto, selectedAlbum);
      refreshAllLists();
      setStatus("Photo added to album successfully");
    }
  }

  private void removePhotoFromAlbum(ActionEvent e) {
    Album selectedAlbum = albumList.getSelectedValue();
    if (selectedAlbum == null) {
      JOptionPane.showMessageDialog(this, "Please select an album first", "No Selection", JOptionPane.WARNING_MESSAGE);
      return;
    }

    // Show photo selection dialog from album photos
    List<Photo> albumPhotos = new ArrayList<>(selectedAlbum.getPhotos());
    if (albumPhotos.isEmpty()) {
      JOptionPane.showMessageDialog(this, "Album has no photos to remove", "No Photos", JOptionPane.INFORMATION_MESSAGE);
      return;
    }

    Photo[] photoArray = albumPhotos.toArray(new Photo[0]);
    Photo selectedPhoto = (Photo) JOptionPane.showInputDialog(this,
      "Select a photo to remove from album:", "Remove Photo",
      JOptionPane.QUESTION_MESSAGE, null, photoArray, null);

    if (selectedPhoto != null) {
      gallery.removePhotoFromAlbum(selectedPhoto, selectedAlbum);
      refreshAllLists();
      setStatus("Photo removed from album successfully");
    }
  }

  // Group operations
  private void createGroup(ActionEvent e) {
    String groupName = JOptionPane.showInputDialog(this, "Enter group name:");
    if (groupName != null && !groupName.trim().isEmpty()) {
      gallery.createGroup(groupName.trim());
      refreshAllLists();
      setStatus("Group created successfully");
    }
  }

  private void deleteGroup(ActionEvent e) {
    Group selectedGroup = groupList.getSelectedValue();
    if (selectedGroup == null) {
      JOptionPane.showMessageDialog(this, "Please select a group to delete", "No Selection", JOptionPane.WARNING_MESSAGE);
      return;
    }

    int result = JOptionPane.showConfirmDialog(this,
      "Are you sure you want to delete group '" + selectedGroup.getName() + "'?",
      "Confirm Delete", JOptionPane.YES_NO_OPTION);

    if (result == JOptionPane.YES_OPTION) {
      gallery.deleteGroup(selectedGroup);
      refreshAllLists();
      setStatus("Group deleted successfully");
    }
  }

  private void moveAlbumToGroup(ActionEvent e) {
    // Select album
    List<Album> availableAlbums = new ArrayList<>(gallery.getAlbums());
    Album[] albumArray = availableAlbums.toArray(new Album[0]);

    Album selectedAlbum = (Album) JOptionPane.showInputDialog(this,
      "Select an album to move:", "Move Album",
      JOptionPane.QUESTION_MESSAGE, null, albumArray, null);

    if (selectedAlbum == null) return;

    // Select target group (or null for no group)
    List<Group> availableGroups = new ArrayList<>(gallery.getGroups());
    availableGroups.addFirst(null); // Add null option for "no group"
    Group[] groupArray = availableGroups.toArray(new Group[0]);

    Group selectedGroup = (Group) JOptionPane.showInputDialog(this,
      "Select target group (or 'null' for no group):", "Move Album",
      JOptionPane.QUESTION_MESSAGE, null, groupArray, null);

    gallery.moveAlbum(selectedAlbum, selectedGroup);
    refreshAllLists();
    setStatus("Album moved successfully");
  }

  // Utility methods
  private void displayPhotoPreview() {
    Photo selectedPhoto = photoList.getSelectedValue();
    if (selectedPhoto == null) {
      photoPreview.setIcon(null);
      photoPreview.setText("Select a photo to preview");
      return;
    }

    try {
      BufferedImage originalImage = ImageIO.read(selectedPhoto.getPath().toFile());

      // Scale image to fit preview area (max 400x400)
      int maxSize = 400;
      double scale = Math.min(
        (double) maxSize / originalImage.getWidth(),
        (double) maxSize / originalImage.getHeight()
      );

      if (scale < 1.0) {
        Image scaledImage = originalImage.getScaledInstance(
          (int) (originalImage.getWidth() * scale),
          (int) (originalImage.getHeight() * scale),
          Image.SCALE_SMOOTH
        );
        photoPreview.setIcon(new ImageIcon(scaledImage));
      } else {
        photoPreview.setIcon(new ImageIcon(originalImage));
      }

      photoPreview.setText(null);

    } catch (IOException ex) {
      photoPreview.setIcon(null);
      photoPreview.setText("Failed to load image");
    }
  }

  private void refreshAllLists() {
    // Refresh photos
    photoListModel.clear();
    for (Photo photo : gallery.getPhotos()) {
      photoListModel.addElement(photo);
    }

    // Refresh albums
    albumListModel.clear();
    for (Album album : gallery.getAlbums()) {
      albumListModel.addElement(album);
    }

    // Refresh groups
    groupListModel.clear();
    for (Group group : gallery.getGroups()) {
      groupListModel.addElement(group);
    }
  }

  private void setStatus(String message) {
    statusLabel.setText(message);
  }

  private void showError(String message, Exception ex) {
    JOptionPane.showMessageDialog(this, message + ": " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    ex.printStackTrace();
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (Exception e) {
        e.printStackTrace();
      }
      new Main().setVisible(true);
    });
  }
}