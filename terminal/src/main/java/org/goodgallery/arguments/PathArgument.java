package org.goodgallery.arguments;

import java.nio.file.Path;
import java.util.stream.Collectors;

public class PathArgument extends AbstractArgument<Path> {

  PathArgument(String name) {
    super(name);
  }

  @Override
  public InternalArgument<Path> toInternal() {
    return new InternalArgumentImpl<>(
      name(),
      arguments().stream().map(Argument::toInternal).collect(Collectors.toSet()),
      executable()
    ) {

      @Override
      public String getUsageForm() {
        return "\"path\"";
      }

      @Override
      public boolean isValidInput(String input) {
        return true;
      }

      @Override
      public Path parse(String input) {
        return Path.of(input).normalize().toAbsolutePath();
      }
    };
  }

}
