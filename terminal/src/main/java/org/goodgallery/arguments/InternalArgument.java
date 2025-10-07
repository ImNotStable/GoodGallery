package org.goodgallery.arguments;

import org.goodgallery.command.CommandContext;

public interface InternalArgument<O> {

  boolean isValidInput(String input);

  O parse(String input);

  boolean execute(CommandContext context);

}
