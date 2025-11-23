package org.goodgallery.terminal.messages;

import org.jline.jansi.Ansi;

import java.util.Arrays;
import java.util.Collection;

public class Error extends AbstractOutput {

  public Error(Collection<String> lines) {
    super(lines);
  }

  public Error(String... lines) {
    super(Arrays.asList(lines));
  }

  public Error(Exception exception) {
    StringBuilder sb = new StringBuilder(exception.getMessage());
    for (StackTraceElement element : exception.getStackTrace()) {
      sb.append(System.lineSeparator())
        .append(" ")
        .append(element.toString());
    }
    this(sb.toString());
  }

  @Override
  public String toRenderedString() {
    return toBoxedString(Ansi.Color.RED);
  }

}
