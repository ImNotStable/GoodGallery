package org.goodgallery;

import org.goodgallery.arguments.Argument;
import org.goodgallery.command.Command;
import org.goodgallery.command.CommandDispatcher;
import org.goodgallery.gallery.*;
import org.goodgallery.gallery.properties.Properties;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Collectors;

public class Main {

  private static final GallerySettings SETTINGS = new GallerySettings()
    .storage(GallerySettings.StorageType.SQLITE)
    .galleryPath(Paths.get("gallery"));
  private static final Gallery GALLERY = GalleryInstance.init(SETTINGS);
  private static final CommandDispatcher DISPATCHER = new CommandDispatcher();

  private static Image icon;

  @SuppressWarnings("resource")
  static void main() {
    try {
      URL iconURL = Main.class.getResource("/icon.png");
      if (iconURL == null)
        throw new RuntimeException("Icon resource not found");
      icon = ImageIO.read(iconURL);
    } catch (IOException exception) {
      throw new RuntimeException(exception);
    }

    Command.builder("photos")
      .then(Argument.literal("list")
        .executes(context -> {
          context.out().printf("Photos (%d):%n", GALLERY.getPhotos().size());
          for (Photo photo : GALLERY.getPhotos()) {
            Optional<String> name = photo.getName();
            Optional<Path> path = photo.getPath();

            if (name.isEmpty() || path.isEmpty()) continue;

            context.out().printf(" - %s (%s)%n", name.get(), path.get());
          }
        })
      )
      .then(Argument.literal("copy")
        .then(Argument.path("path")
          .executes(context -> {
            Path path = context.get("path", Path.class);
            try {
              GALLERY.copyPhoto(path);
              context.out().println("Successfully copied photo");
            } catch (Exception e) {
              context.out().printf("Failed to copy photo due to \"%s\"%n", e.getMessage());
              e.printStackTrace(context.out());
            }
          })
        )
      )
      .then(Argument.literal("cut")
        .then(Argument.path("path")
          .executes(context -> {
            Path path = context.get("path", Path.class);
            try {
              GALLERY.cutPhoto(path);
              context.out().println("Successfully cut photo");
            } catch (Exception e) {
              context.out().printf("Failed to cut photo due to \"%s\"%n", e.getMessage());
              e.printStackTrace(context.out());
            }
          })
        )
      )
      .then(Argument.literal("delete")
        .then(Argument.photo("photo")
          .executes(context -> {
              Photo photo = context.get("photo", Photo.class);
              try {
                GALLERY.deletePhoto(photo);
                context.out().println("Photo deleted successfully");
              } catch (IOException e) {
                context.out().printf("Failed to delete photo due to \"%s\"%n", e.getMessage());
                e.printStackTrace(context.out());
              }
            }
          )
        )
      )
      .then(Argument.literal("rename")
        .then(Argument.photo("photo")
          .then(Argument.string("name")
            .executes(context -> {
              Photo photo = context.get("photo", Photo.class);
              String newName = context.get("name", String.class);
              GALLERY.updateProperty(photo, Properties.NAME_KEY, newName);
              context.out().println("Renamed photo successfully");
            })
          )
        )
      )
      .then(Argument.literal("view")
        .then(Argument.photo("photo")
          .executes(context -> {
            Photo photo = context.get("photo", Photo.class);

            JFrame frame = new JFrame();
            frame.setIconImage(icon);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

            Optional<File> photoFile = photo.getPath().map(Path::toFile);
            if (photoFile.isEmpty()) {
              context.out().println("Photo has no associated file path");
              return;
            }

            BufferedImage originalImage;
            try {
              originalImage = ImageIO.read(photoFile.get());
            } catch (IOException e) {
              context.out().printf("Failed to read photo due to \"%s\"%n", e.getMessage());
              return;
            }

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

            JLabel label = new JLabel(new ImageIcon(scaledImage));
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setVerticalAlignment(JLabel.CENTER);

            frame.add(label, BorderLayout.CENTER);
            frame.setVisible(true);
          })
        )
      )
      .register(DISPATCHER);

    Command.builder("albums")
      .then(Argument.literal("list")
        .executes(context -> {
          context.out().printf("Albums (%d):%n", GALLERY.getAlbums().size());

          for (Album album : GALLERY.getAlbums()) {
            Optional<String> name = album.getName();

            if (name.isEmpty()) continue;

            String photos = album.getPhotos().stream().map(photo -> {
              Optional<String> photoName = photo.getName();
              Optional<Path> photoPath = photo.getPath();

              if (photoName.isEmpty() || photoPath.isEmpty()) return null;

              return "  * %s (%s)".formatted(photoName.get(), photoPath.get());
            }).collect(Collectors.joining("%n"));

            context.out().printf(" - %s%n%s", name.get(), photos);
          }
        })
      )
      .then(Argument.literal("create")
        .then(Argument.string("album")
          .executes(context -> {
            String name = context.get("album", String.class);
            GALLERY.createAlbum(name);
            context.out().println("Successfully created album");
          })
        )
      )
      .then(Argument.literal("rename")
        .then(Argument.album("album")
          .then(Argument.string("name")
            .executes(context -> {
              Album album = context.get("album", Album.class);
              String newName = context.get("name", String.class);
              GALLERY.updateProperty(album, Properties.NAME_KEY, newName);
              context.out().println("Renamed photo successfully");
            })
          )
        )
      )
      .then(Argument.literal("delete")
        .then(Argument.album("album")
          .executes(context -> {
            Album album = context.get("album", Album.class);
            GALLERY.deleteAlbum(album);
            context.out().println("Successfully deleted album");
          })
        )
      )
      .then(Argument.literal("addPhoto")
        .then(Argument.album("album")
          .then(Argument.photo("photo")
            .executes(context -> {
                Album album = context.get("album", Album.class);
                Photo photo = context.get("photo", Photo.class);
                GALLERY.addPhotoToAlbum(photo, album);
                context.out().println("Successfully added photo to album");
              }
            )
          )
        )
      )
      .then(Argument.literal("removePhoto")
        .then(Argument.album("album")
          .then(Argument.photo("photo")
            .executes(context -> {
              Album album = context.get("album", Album.class);
              Photo photo = context.get("photo", Photo.class);
              GALLERY.removePhotoFromAlbum(photo, album);
              context.out().println("Successfully removed photo from album");
            })
          )
        )
      )
      .register(DISPATCHER);

    Command.builder("exit")
      .executes(_ -> System.exit(0))
      .register(DISPATCHER);
  }

}