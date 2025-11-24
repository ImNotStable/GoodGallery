package org.goodgallery.terminal.output;

import org.jline.jansi.Ansi;

import java.util.Collection;

public class Warning extends AbstractOutput {

  public Warning(Collection<String> lines) {
    super(lines);
  }

  @Override
  public String toRenderedString() {
    return toBoxedString(Ansi.Color.YELLOW);
  }

}
