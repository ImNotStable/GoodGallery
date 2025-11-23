package org.goodgallery.terminal;

import org.goodgallery.command.CommandDispatcher;
import org.goodgallery.command.CommandException;
import org.goodgallery.terminal.messages.Error;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.Closeable;
import java.io.IOException;

public class TerminalManager extends Thread implements Closeable {

  private boolean isOpen = true;
  private final CommandDispatcher dispatcher;
  private final Terminal terminal;
  private final LineReader reader;

  public TerminalManager(CommandDispatcher dispatcher) throws IOException {
    this.dispatcher = dispatcher;
    this.terminal = TerminalBuilder.builder()
      .system(true)
      .streams(dispatcher.in(), dispatcher.out())
      .build();
    this.reader = LineReaderBuilder.builder()
      .terminal(terminal)
      .appName("GoodGallery")
      .parser(new CustomParser())
      .build();
  }

  @Override
  public void run() {
    String prompt = "╭────────────────────────── GoodGallery ────────────────────────────╮"
      + System.lineSeparator()
      + "│ Type 'help' to see a list of available commands.                  │" +
      System.lineSeparator()
      + "╰─➤ ";

    TerminalContext context = new TerminalContext(terminal);
    while (isOpen) {
      String input = reader.readLine(prompt);
      try {
        dispatcher.execute(context, input);
      } catch (CommandException exception) {
        context.print(new Error(exception));
      }
    }
  }

  @Override
  public void close() throws IOException {
    isOpen = false;
    terminal.flush();
    terminal.close();
  }

}
