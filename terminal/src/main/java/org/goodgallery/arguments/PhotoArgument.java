package org.goodgallery.arguments;

import org.goodgallery.command.CommandContext;
import org.goodgallery.gallery.Gallery;
import org.goodgallery.gallery.Photo;

public class PhotoArgument extends AbstractArgument<Photo> {

  private final Gallery gallery;

  PhotoArgument(String name, Gallery gallery) {
    super(name);
    this.gallery = gallery;
  }

  @Override
  protected String getUsage() {
    return "<photo>";
  }

  @Override
  protected boolean isValidInput(CommandContext context) {
    return gallery.hasPhoto(context.peak());
  }

  @Override
  protected Photo parse(CommandContext context) {
    return gallery.getPhoto(context.next()).orElse(null);
  }

}
