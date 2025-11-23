package org.goodgallery.terminal.messages;

import org.jline.jansi.Ansi;

import java.util.Arrays;
import java.util.Collection;

public class Warning extends AbstractOutput {

  public Warning(Collection<String> lines) {
    super(lines);
  }

  public Warning(String... lines) {
    super(Arrays.asList(lines));
  }

  @Override
  public String toRenderedString() {
    return toBoxedString(Ansi.Color.YELLOW);
  }

}
