package org.goodgallery.terminal;

import org.goodgallery.terminal.messages.Output;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;

import java.io.PrintWriter;

public record TerminalContext(PrintWriter writer, Size size) {

  public TerminalContext(Terminal terminal) {
    this(terminal.writer(), terminal.getSize());
  }

  public void print(Output output) {
    writer.println(output.toRenderedString());
    writer.flush();
  }

}
