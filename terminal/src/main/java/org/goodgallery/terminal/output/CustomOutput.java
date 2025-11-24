package org.goodgallery.terminal.output;

import org.jline.jansi.Ansi;

import java.util.Collection;

public class CustomOutput extends AbstractOutput {

  private final Ansi.Color color;

  public CustomOutput(Ansi.Color color, String title, Collection<String> lines) {
    super(title, lines);
    this.color = color;
  }

  public CustomOutput(Ansi.Color color, Collection<String> lines) {
    super(lines);
    this.color = color;
  }

  @Override
  public String toRenderedString() {
    return toBoxedString(color);
  }

}
