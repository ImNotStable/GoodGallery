package org.goodgallery.command;

import java.io.Closeable;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CommandDispatcher implements Closeable {

  private final InputStream in;
  private final PrintStream out;
  private final Map<String, Command> commands;
  private final ExecutorService thread;
  private boolean isActive;

  public CommandDispatcher(InputStream inputStream, PrintStream outputStream) {
    this.in = inputStream;
    this.out = outputStream;
    this.commands = new HashMap<>();
    registerHelp();
    this.thread = Executors.newSingleThreadExecutor();
    start();
  }

  public CommandDispatcher() {
    this(System.in, System.out);
  }

  public void addCommand(Command command) {
    commands.put(command.toString(), command);
  }

  private void registerHelp() {
    Command.builder("help")
      .executes(_ -> {
        out.println("Available commands:");
        for (Command command : commands.values())
          out.printf(" - %s%n", command.toString());
      })
      .register(this);
  }

  public void start() {
    isActive = true;
    thread.execute(() -> {
      Scanner scanner = new Scanner(in);

      while (isActive) {
        out.print("GoodGallery > ");
        String command = scanner.nextLine();
        CommandContext context = new CommandContext(out, command);
        execute(context);
      }

      scanner.close();
    });
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

  @Override
  public void close() {
    isActive = false;
    thread.shutdown();

    try {
      if (thread.awaitTermination(5, TimeUnit.SECONDS)) {
        thread.shutdownNow();
      }
    } catch (InterruptedException e) {
      thread.shutdownNow();
    }
  }

}
