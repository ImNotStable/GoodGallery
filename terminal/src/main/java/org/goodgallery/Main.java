package org.goodgallery;

import org.goodgallery.arguments.Argument;
import org.goodgallery.command.Command;
import org.goodgallery.command.CommandDispatcher;
import org.goodgallery.gallery.*;
import org.goodgallery.gallery.properties.Properties;
import org.goodgallery.terminal.TerminalManager;
import org.goodgallery.terminal.messages.Error;
import org.goodgallery.terminal.messages.Output;
import org.jline.jansi.Ansi;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Collectors;

public class Main {

  private static final GallerySettings SETTINGS = new GallerySettings()
    .storage(GallerySettings.StorageType.SQLITE)
    .galleryPath(Path.of("P:\\Projects\\Java\\GoodGallery\\gallery"));
  private static final Gallery GALLERY = GalleryInstance.init(SETTINGS);
  private static final CommandDispatcher DISPATCHER = new CommandDispatcher();
  private static final TerminalManager TERMINAL;
  private static final Image icon;

  static {
    try {
      TERMINAL = new TerminalManager(DISPATCHER);
      TERMINAL.start();
    } catch (IOException exception) {
      throw new RuntimeException("Failed to start JLine Terminal", exception);
    }
    try {
      URL iconURL = Main.class.getResource("/icon.png");
      if (iconURL == null)
        throw new RuntimeException("Icon resource not found");
      icon = ImageIO.read(iconURL);
    } catch (IOException exception) {
      throw new RuntimeException("Failed to access icon resource", exception);
    }
  }

  static void main() {
    Output error = new Error(new Exception("Test Exception"));
    Command.builder("photos")
      .then(Argument.literal("list")
        .executes(context -> {
          StringBuilder sb = new StringBuilder();
          sb.append("Photos (%d)".formatted(GALLERY.getPhotos().size()));
          for (Photo photo : GALLERY.getPhotos()) {
            Optional<String> name = photo.getName();
            Optional<Path> path = photo.getPath();

            if (name.isEmpty() || path.isEmpty()) continue;

            sb.append(System.lineSeparator())
              .append(" - %s (%s)".formatted(name.get(), path.get()));
          }
          context.info(sb.toString());
        })
      )
      .then(Argument.literal("copy")
        .then(Argument.path("path")
          .executes(context -> {
            try {
              Path path = context.get("path", Path.class);
              GALLERY.copyPhoto(path);
              context.customOutput(Ansi.Color.GREEN, "Successfully copied photo");
            } catch (Exception exception) {
              context.error("Failed to copy photo");
              context.exception(exception);
            }
          })
        )
      )
      .then(Argument.literal("cut")
        .then(Argument.path("path")
          .executes(context -> {
            try {
              Path path = context.get("path", Path.class);
              GALLERY.cutPhoto(path);
              context.customOutput(Ansi.Color.GREEN, "Successfully cut photo");
            } catch (Exception exception) {
              context.error("Failed to cut photo");
              context.exception(exception);
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
                context.customOutput(Ansi.Color.GREEN, "Successfully deleted photo");
              } catch (IOException exception) {
                context.error("Failed to delete photo");
                context.exception(exception);
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
              context.customOutput(Ansi.Color.GREEN, "Renamed photo successfully");
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
              context.error("Photo has no associated file path");
              return;
            }

            BufferedImage originalImage;
            try {
              originalImage = ImageIO.read(photoFile.get());
            } catch (IOException exception) {
              context.error("Failed to read photo from file due to \"%s\"", exception.getMessage());
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
          StringBuilder sb = new StringBuilder();
          sb.append("Albums (%d)".formatted(GALLERY.getAlbums().size()));

          for (Album album : GALLERY.getAlbums()) {
            Optional<String> name = album.getName();

            if (name.isEmpty()) continue;

            String photos = album.getPhotos().stream().map(photo -> {
              Optional<String> photoName = photo.getName();
              Optional<Path> photoPath = photo.getPath();

              if (photoName.isEmpty() || photoPath.isEmpty()) return null;

              return "  * %s (%s)".formatted(photoName.get(), photoPath.get());
            }).collect(Collectors.joining(System.lineSeparator()));

            sb.append(System.lineSeparator())
                .append(" - %s%n%s".formatted(name.get(), photos));
          }
          context.info(sb.toString());
        })
      )
      .then(Argument.literal("create")
        .then(Argument.string("album")
          .executes(context -> {
            String name = context.get("album", String.class);
            GALLERY.createAlbum(name);
            context.customOutput(Ansi.Color.GREEN, "Successfully created album");
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
              context.customOutput(Ansi.Color.GREEN, "Renamed album successfully");
            })
          )
        )
      )
      .then(Argument.literal("delete")
        .then(Argument.album("album")
          .executes(context -> {
            Album album = context.get("album", Album.class);
            GALLERY.deleteAlbum(album);
            context.customOutput(Ansi.Color.GREEN, "Successfully deleted album");
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
                context.customOutput(Ansi.Color.GREEN, "Successfully added photo to album");
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
              context.customOutput(Ansi.Color.GREEN, "Successfully removed photo from album");
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