package org.goodgallery.terminal.output;

import org.jline.jansi.Ansi;

import java.util.ArrayList;
import java.util.Collection;

public class Error extends AbstractOutput {

  public Error(Collection<String> lines) {
    super(lines);
  }

  public Error(Exception exception) {
    Collection<String> output = new ArrayList<>();
    output.add(exception.getMessage());
    for (StackTraceElement element : exception.getStackTrace())
      output.add(" " + element.toString());
    super("Exception", output);
  }

  @Override
  public String toRenderedString() {
    return toBoxedString(Ansi.Color.RED);
  }

}
