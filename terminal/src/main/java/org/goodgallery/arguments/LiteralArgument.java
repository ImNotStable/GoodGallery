package org.goodgallery.arguments;

public class LiteralArgument extends AbstractArgument<String> {

  protected LiteralArgument(String literal) {
    super(literal);
  }

  @Override
  public InternalArgument<String> toInternal() {
    return toQuickInternal(name(), input -> name().equalsIgnoreCase(input), _ -> name());
  }

}
