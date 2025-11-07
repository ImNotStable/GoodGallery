package org.goodgallery.arguments;

public class StringArgument extends AbstractArgument<String> {

  StringArgument(String name) {
    super(name);
  }

  @Override
  public InternalArgument<String> toInternal() {
    return toInternal("\"%s\"".formatted(name()), _ -> true, input -> input);
  }

}
