package org.goodgallery.command;

import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
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

  public void addCommand(Command command) {
    commands.put(command.toString(), command);
  }

  private void registerHelp() {
    String title = "Available commands";
    Command.builder("help")
      .executes(context -> {
        context.writer().println("╭────────────────────╮");
        context.writer().printf("│ %s │%n", title);
        for (Command command : commands.values()) {
          String label = command.toString();
          context.writer().printf("│ %s%s │%n", label, " ".repeat(Math.max(0, title.length() - label.length())));
        }
        context.writer().println("╰────────────────────╯");
      })
      .register(this);
  }

  public boolean execute(PrintWriter writer, String rawCommand) {
    CommandContext context = new CommandContext(writer, rawCommand);
    return execute(context);
  }

  private boolean execute(CommandContext context) {
    Command command = commands.get(context.getLabel());

    if (command == null) {
      out.println("Unknown command.");
      return false;
    }

    boolean result = command.execute(context);

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
