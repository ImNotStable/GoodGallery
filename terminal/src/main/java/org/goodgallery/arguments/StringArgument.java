package org.goodgallery.arguments;

import org.goodgallery.command.CommandContext;

public class StringArgument extends AbstractArgument<String> {

  StringArgument(String name) {
    super(name);
  }

  @Override
  protected String getUsage() {
    return "<%s>".formatted(name());
  }

  @Override
  protected boolean isValidInput(CommandContext context) {
    return true;
  }

  @Override
  protected String parse(CommandContext context) {
    return context.next();
  }

}
