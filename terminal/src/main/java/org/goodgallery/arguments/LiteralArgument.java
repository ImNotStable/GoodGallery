package org.goodgallery.arguments;

public class LiteralArgument extends AbstractArgument<String> {

  protected LiteralArgument(String literal) {
    super(literal);
  }

  @Override
  public InternalArgument<String> toInternal() {
    return toInternal(name(), input -> name().equalsIgnoreCase(input.toLowerCase()), _ -> name());
  }

}
