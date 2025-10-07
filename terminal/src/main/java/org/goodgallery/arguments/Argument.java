package org.goodgallery.arguments;

import org.goodgallery.command.CommandContext;
import org.goodgallery.gallery.GalleryInstance;

import java.util.function.Consumer;

public interface Argument<O> {

  static LiteralArgument literal(String literal) {
    return new LiteralArgument(literal);
  }

  static StringArgument string(String name) {
    return new StringArgument(name);
  }

  static PathArgument path(String name) {
    return new PathArgument(name);
  }

  static PhotoArgument photo(String name) {
    return new PhotoArgument(name, GalleryInstance.get());
  }

  static AlbumArgument album(String name) {
    return new AlbumArgument(name, GalleryInstance.get());
  }

  static GroupArgument group(String name) {
    return new GroupArgument(name, GalleryInstance.get());
  }

  <N> Argument<O> then(Argument<N> argument);

  Argument<O> executes(Consumer<CommandContext> context);

  InternalArgument<O> toInternal();

}
