package org.goodgallery.terminal;

import org.goodgallery.command.CommandDispatcher;
import org.goodgallery.command.CommandException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;

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
      .dumb(false)
      .ffm(true)
      .build();
    this.reader = LineReaderBuilder.builder()
      .terminal(terminal)
      .appName("GoodGallery")
      .parser(new DefaultParser() {
        @Override
        public boolean isEscapeChar(char c) {
          return false;
        }
      })
      .build();
  }

  @Override
  public void run() {
    String prompt = "╭──────────────────────────── GoodGallery ────────────────────────────╮"
      + System.lineSeparator()
      + "│ Type 'help' to see a list of available commands.                    │" +
      System.lineSeparator()
      + "├─➤                                                                   │"
      + System.lineSeparator()
      + "╰─────────────────────────────────────────────────────────────────────╯";

    TerminalContext context = new TerminalContext(terminal);
    while (isOpen) {
      terminal.writer().print(prompt);
      terminal.writer().flush();
      terminal.puts(InfoCmp.Capability.cursor_up);
      terminal.puts(InfoCmp.Capability.cursor_left, null, 5);
      terminal.writer().flush();
      String input = reader.readLine();
      try {
        dispatcher.execute(context, input);
      } catch (CommandException exception) {
        context.print(Output.exception(exception));
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
