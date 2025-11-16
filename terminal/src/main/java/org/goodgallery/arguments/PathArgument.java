package org.goodgallery.arguments;

import java.nio.file.Path;

public class PathArgument extends AbstractArgument<Path> {

  PathArgument(String name) {
    super(name);
  }

  @Override
  public InternalArgument<Path> toInternal() {
    return toInternal("\"path\"", _ -> true, context -> Path.of(context.getGreedyArgs()).normalize().toAbsolutePath());
  }

}
