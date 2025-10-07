package org.goodgallery.command;

import java.io.Closeable;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CommandDispatcher implements Closeable {

  private final InputStream in;
  private final PrintStream out;
  private final Set<Command> commands;
  private final Executor thread;
  private boolean isActive;

  public CommandDispatcher(InputStream inputStream, PrintStream outputStream) {
    this.in = inputStream;
    this.out = outputStream;
    this.commands = new HashSet<>();
    registerHelp();
    this.thread = Executors.newSingleThreadExecutor();
    start();
  }

  public CommandDispatcher() {
    this(System.in, System.out);
  }

  public void addCommand(Command command) {
    commands.add(command);
  }

  private void registerHelp() {
    Command.builder("help")
      .executes(context -> {
        out.println("Available commands:");
        for (Command command : commands) {
          out.printf(" - %s%n", command.toString());
        }
      })
      .register(this);
  }

  public void start() {
    isActive = true;
    thread.execute(() -> {
      Scanner scanner = new Scanner(in);

      while (isActive) {
        out.print("BPG > ");
        String command = scanner.nextLine();
        CommandContext context = new CommandContext(in, out, command);
        execute(context);
      }

      scanner.close();
    });
  }

  private boolean execute(CommandContext context) {
    Optional<Command> optionalCommand = commands.stream()
      .filter(command -> command.isCommand(context.getLabel()))
      .findFirst();

    if (optionalCommand.isEmpty()) {
      out.print("Unknown command.");
      return false;
    }

    boolean result = optionalCommand.get().execute(context);

    if (!result)
      out.println("Command Failed.");

    return result;
  }

  @Override
  public void close() {
    isActive = false;
  }

}
