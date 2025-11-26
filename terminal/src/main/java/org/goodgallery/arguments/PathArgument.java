package org.goodgallery.arguments;

import org.goodgallery.command.CommandContext;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;

public class PathArgument extends AbstractArgument<Path> {

  PathArgument(String name) {
    super(name);
  }

  @Override
  protected String getUsage() {
    return "<path>";
  }

  @Override
  protected boolean isValidInput(CommandContext context) {
    try {
      context.peek();
      return true;
    } catch (InvalidPathException _) {
      return false;
    }
  }

  @Override
  protected Path parse(CommandContext context) {
    return Path.of(context.next());
  }

}
