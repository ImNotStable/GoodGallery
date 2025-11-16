package org.goodgallery.arguments;

import org.goodgallery.command.CommandContext;

public class StringArgument extends AbstractArgument<String> {

  StringArgument(String name) {
    super(name);
  }

  @Override
  public InternalArgument<String> toInternal() {
    return toInternal("\"%s\"".formatted(name()), _ -> true, CommandContext::getGreedyArgs);
  }

}
