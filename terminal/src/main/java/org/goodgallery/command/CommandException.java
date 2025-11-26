package org.goodgallery.command;

public class CommandException extends Exception {

  public CommandException() {
    super("Failed to execute command.");
  }

  public CommandException(Throwable cause) {
    super("Failed to execute command due to \"%s\".".formatted(cause.getMessage()), cause);
  }

}
