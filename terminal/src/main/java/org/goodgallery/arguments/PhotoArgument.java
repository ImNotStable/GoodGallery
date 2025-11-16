package org.goodgallery.arguments;

import org.goodgallery.gallery.Gallery;
import org.goodgallery.gallery.Photo;

public class PhotoArgument extends AbstractArgument<Photo> {

  private final Gallery gallery;

  PhotoArgument(String name, Gallery gallery) {
    super(name);
    this.gallery = gallery;
  }

  @Override
  public InternalArgument<Photo> toInternal() {
    return toInternal("\"photo\"", context -> gallery.hasPhoto(context.peakGreedyArgs()), context -> gallery.getPhoto(context.getGreedyArgs()).orElse(null));
  }

}
