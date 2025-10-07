package org.goodgallery.arguments;

import org.goodgallery.gallery.Gallery;
import org.goodgallery.gallery.Photo;

import java.util.stream.Collectors;

public class PhotoArgument extends AbstractArgument<Photo> {

  private final Gallery gallery;

  PhotoArgument(String name, Gallery gallery) {
    super(name);
    this.gallery = gallery;
  }

  @Override
  public InternalArgument<Photo> toInternal() {
    return new InternalArgumentImpl<>(
      name(),
      arguments().stream().map(Argument::toInternal).collect(Collectors.toSet()),
      executable()
    ) {
      @Override
      public String getUsageForm() {
        return "\"photo\"";
      }

      @Override
      public boolean isValidInput(String input) {
        return gallery.hasPhoto(input);
      }

      @Override
      public Photo parse(String input) {
        return gallery.getPhoto(input);
      }
    };
  }

}
