package org.goodgallery.arguments;

import java.util.stream.Collectors;

public class LiteralArgument extends AbstractArgument<String> {

  protected LiteralArgument(String literal) {
    super(literal);
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
        return name();
      }

      @Override
      public boolean isValidInput(String input) {
        return name().equalsIgnoreCase(input.toLowerCase());
      }

      @Override
      public String parse(String input) {
        return name();
      }

    };
  }

}
