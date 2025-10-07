package org.goodgallery.arguments;

import java.util.stream.Collectors;

public class StringArgument extends AbstractArgument<String> {

  StringArgument(String name) {
    super(name);
  }

  @Override
  public InternalArgument<String> toInternal() {
    return new InternalArgumentImpl<>(
      name(),
      arguments().stream().map(Argument::toInternal).collect(Collectors.toSet()),
      executable()
    ) {

      @Override
      public String getUsageForm() {
        return "\"%s\"".formatted(name());
      }

      @Override
      public boolean isValidInput(String input) {
        return true;
      }

      @Override
      public String parse(String input) {
        return input;
      }

    };
  }

}
