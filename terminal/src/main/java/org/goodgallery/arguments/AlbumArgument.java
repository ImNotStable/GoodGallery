package org.goodgallery.arguments;

import org.goodgallery.command.CommandContext;
import org.goodgallery.gallery.Album;
import org.goodgallery.gallery.Gallery;

public class AlbumArgument extends AbstractArgument<Album> {

  private final Gallery gallery;

  AlbumArgument(String name, Gallery gallery) {
    super(name);
    this.gallery = gallery;
  }

  @Override
  protected String getUsage() {
    return "<album>";
  }

  @Override
  protected boolean isValidInput(CommandContext context) {
    return gallery.hasAlbum(context.peak());
  }

  @Override
  protected Album parse(CommandContext context) {
    return gallery.getAlbum(context.next()).orElse(null);
  }

}
