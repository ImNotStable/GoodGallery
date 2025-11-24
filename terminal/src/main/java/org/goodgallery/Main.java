package org.goodgallery;

import org.goodgallery.arguments.Argument;
import org.goodgallery.command.Command;
import org.goodgallery.command.CommandDispatcher;
import org.goodgallery.gallery.*;
import org.goodgallery.gallery.properties.Properties;
import org.goodgallery.terminal.TerminalManager;
import org.jline.jansi.Ansi;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

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
    Command.builder("photos")
      .then(Argument.literal("list")
        .executes(context -> {
          Collection<String> message = new ArrayList<>();
          message.add("Photos (%d)".formatted(GALLERY.getPhotos().size()));
          message.addAll(GALLERY.getPhotos().stream().map(photo -> {
            Optional<String> name = photo.getName();
            Optional<Path> path = photo.getPath();

            if (name.isEmpty() || path.isEmpty()) return null;

            return " %s (%s)".formatted(name.get(), path.get());
          }).filter(Objects::nonNull).toList());
          context.info(message);
        })
      )
      .then(Argument.literal("copy")
        .then(Argument.path("path")
          .executes(context -> {
            try {
              Path path = context.get("path", Path.class);
              GALLERY.copyPhoto(path);
              context.output(Ansi.Color.GREEN, "Successfully copied photo");
            } catch (Exception exception) {
              context.exception("Failed to copy photo", exception);
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
              context.output(Ansi.Color.GREEN, "Successfully cut photo");
            } catch (Exception exception) {
              context.exception("Failed to cut photo", exception);
            }
          })
        )
      )
      .then(Argument.literal("delete")
        .then(Argument.photo("photo")
          .executes(context -> {
              try {
                Photo photo = context.get("photo", Photo.class);
                GALLERY.deletePhoto(photo);
                context.output(Ansi.Color.GREEN, "Successfully deleted photo");
              } catch (IOException exception) {
                context.exception("Failed to delete photo", exception);
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
              context.output(Ansi.Color.GREEN, "Renamed photo successfully");
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
              context.exception("Failed to load image", exception);
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
          Collection<String> message = new ArrayList<>();
          message.add("Albums (%d)".formatted(GALLERY.getAlbums().size()));
          GALLERY.getAlbums().stream().map(album -> {
            Optional<String> name = album.getName();
            if (name.isEmpty()) return null;
            Collection<String> subMessage = new ArrayList<>();
            subMessage.add(" %s".formatted(name.get()));
            subMessage.addAll(
            album.getPhotos().stream()
              .map(photo -> {
                Optional<String> photoName = photo.getName();
                Optional<Path> photoPath = photo.getPath();

                if (photoName.isEmpty() || photoPath.isEmpty()) return null;

                return "  %s (%s)".formatted(photoName.get(), photoPath.get());
              })
              .filter(Objects::nonNull)
              .toList());

            return subMessage;
          }).filter(Objects::nonNull).forEach(message::addAll);

          context.info(message);
        })
      )
      .then(Argument.literal("create")
        .then(Argument.string("album")
          .executes(context -> {
            String name = context.get("album", String.class);
            GALLERY.createAlbum(name);
            context.output(Ansi.Color.GREEN, "Successfully created album");
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
              context.output(Ansi.Color.GREEN, "Renamed album successfully");
            })
          )
        )
      )
      .then(Argument.literal("delete")
        .then(Argument.album("album")
          .executes(context -> {
            Album album = context.get("album", Album.class);
            GALLERY.deleteAlbum(album);
            context.output(Ansi.Color.GREEN, "Successfully deleted album");
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
                context.output(Ansi.Color.GREEN, "Successfully added photo to album");
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
              context.output(Ansi.Color.GREEN, "Successfully removed photo from album");
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