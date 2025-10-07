package org.goodgallery.arguments;

import org.goodgallery.gallery.Album;
import org.goodgallery.gallery.Gallery;

import java.util.stream.Collectors;

public class AlbumArgument extends AbstractArgument<Album> {

  private final Gallery gallery;

  AlbumArgument(String name, Gallery gallery) {
    super(name);
    this.gallery = gallery;
  }

  @Override
  public InternalArgument<Album> toInternal() {
    return new InternalArgumentImpl<>(
      name(),
      arguments().stream().map(Argument::toInternal).collect(Collectors.toSet()),
      executable()
    ) {
      @Override
      public String getUsageForm() {
        return "\"album\"";
      }

      @Override
      public boolean isValidInput(String input) {
        return gallery.hasAlbum(input);
      }

      @Override
      public Album parse(String input) {
        return gallery.getAlbum(input);
      }
    };
  }

}
