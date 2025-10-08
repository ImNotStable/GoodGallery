package org.goodgallery;

import org.goodgallery.arguments.Argument;
import org.goodgallery.command.Command;
import org.goodgallery.command.CommandDispatcher;
import org.goodgallery.gallery.Album;
import org.goodgallery.gallery.Gallery;
import org.goodgallery.gallery.GalleryInstance;
import org.goodgallery.gallery.Photo;
import org.goodgallery.gallery.Properties;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class Main {

  private static final Path GALLERY_PATH = Paths.get("./gallery");

  @SuppressWarnings("resource")
  public static void main(String[] args) {
    Gallery gallery = GalleryInstance.init(GALLERY_PATH);
    CommandDispatcher dispatcher = new CommandDispatcher();

    Command.builder("photos")
      .then(Argument.literal("list")
        .executes(context -> {
          context.out().printf("Photos (%d):%n", gallery.getPhotos().size());
          for (Photo photo : gallery.getPhotos())
            context.out().printf(" - %s (%s)%n", photo.getPropertyValue(Properties.NAME_KEY), photo.getFileName());
        })
      )
      .then(Argument.literal("copy")
        .then(Argument.path("path")
          .executes(context -> {
            Path path = context.get("path", Path.class);
            try {
              gallery.copyPhoto(path);
              context.out().println("Successfully copied photo");
            } catch (IOException e) {
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
              gallery.cutPhoto(path);
              context.out().println("Successfully cut photo");
            } catch (IOException e) {
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
                gallery.deletePhoto(photo);
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
              gallery.updateProperty(photo, Properties.NAME_KEY, newName);
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
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

            BufferedImage originalImage;
            try {
              originalImage = ImageIO.read(photo.getPath().toFile());
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
      .register(dispatcher);

    Command.builder("albums")
      .then(Argument.literal("list")
        .executes(context -> {
          context.out().printf("Albums (%d):%n", gallery.getAlbums().size());
          for (Album album : gallery.getAlbums())
            context.out().printf(" - %s%n%s",
              album.getName(),
              album.getPhotos().stream()
                .map(photo -> "  * %s (%s)".formatted(photo.getPropertyValue(Properties.NAME_KEY), photo.getFileName()))
                .collect(Collectors.joining("%n"))
              );
        })
      )
      .then(Argument.literal("create")
        .then(Argument.string("album")
          .executes(context -> {
            String name = context.get("album", String.class);
            gallery.createAlbum(name);
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
              gallery.updateProperty(album, Properties.NAME_KEY, newName);
              context.out().println("Renamed photo successfully");
            })
          )
        )
      )
      .then(Argument.literal("delete")
        .then(Argument.album("album")
          .executes(context -> {
            Album album = context.get("album", Album.class);
            gallery.deleteAlbum(album);
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
                gallery.addPhotoToAlbum(photo, album);
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
              gallery.removePhotoFromAlbum(photo, album);
              context.out().println("Successfully removed photo from album");
            })
          )
        )
      )
      .register(dispatcher);
  }

}
