package org.goodgallery.arguments;

import org.goodgallery.command.CommandContext;

public class LiteralArgument extends AbstractArgument<String> {

  protected LiteralArgument(String literal) {
    super(literal);
  }

  @Override
  protected String getUsage() {
    return name();
  }

  @Override
  protected boolean isValidInput(CommandContext context) {
    return name().equalsIgnoreCase(context.peak());
  }

  @Override
  protected String parse(CommandContext context) {
    return context.next();
  }

}
