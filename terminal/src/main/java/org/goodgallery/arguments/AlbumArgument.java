package org.goodgallery.arguments;

import org.goodgallery.gallery.Album;
import org.goodgallery.gallery.Gallery;

public class AlbumArgument extends AbstractArgument<Album> {

  private final Gallery gallery;

  AlbumArgument(String name, Gallery gallery) {
    super(name);
    this.gallery = gallery;
  }

  @Override
  public InternalArgument<Album> toInternal() {
    return toInternal("\"album\"", gallery::hasAlbum, input -> gallery.getAlbum(input).orElse(null));
  }

}
