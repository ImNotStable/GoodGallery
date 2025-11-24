package org.goodgallery.command;

import org.goodgallery.terminal.TerminalContext;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CommandDispatcher {

  private final InputStream in;
  private final PrintStream out;
  private final Map<String, Command> commands;

  public CommandDispatcher(InputStream inputStream, PrintStream outputStream) {
    this.in = inputStream;
    this.out = outputStream;
    this.commands = new HashMap<>();
    registerHelp();
  }

  public CommandDispatcher() {
    this(System.in, System.out);
  }

  public void register(Command command) {
    commands.put(command.toString(), command);
  }

  private void registerHelp() {
    Command.builder("help")
      .executes(context -> {
        Collection<String> messages = new ArrayList<>();
        messages.add("Available commands");
        messages.addAll(commands.values().stream().map(Command::toString).toList());
        context.info(messages);
      })
      .register(this);
  }

  public boolean execute(TerminalContext terminalContext, String rawCommand) throws CommandException {
    return execute(new CommandContext(terminalContext, rawCommand));
  }

  private boolean execute(CommandContext context) throws CommandException {
    Command command = commands.get(context.getLabel());

    if (command == null) {
      out.println("Unknown command.");
      return false;
    }

    boolean result;
    try {
      result = command.execute(context);
    } catch (Exception exception) {
      throw new CommandException(exception);
    }

    if (!result)
      out.println("Command Failed.");

    return result;
  }

  public InputStream in() {
    return in;
  }

  public PrintStream out() {
    return out;
  }

}
