package org.goodgallery.terminal.messages;

import org.jline.jansi.Ansi;

import java.util.Arrays;
import java.util.Collection;

public class CustomOutput extends AbstractOutput {

  private final Ansi.Color color;

  public CustomOutput(Ansi.Color color, Collection<String> lines) {
    super(lines);
    this.color = color;
  }

  public CustomOutput(Ansi.Color color, String... lines) {
    this(color, Arrays.asList(lines));
  }

  @Override
  public String toRenderedString() {
    return toBoxedString(color);
  }

}
