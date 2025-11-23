package org.goodgallery.terminal.messages;

import org.jetbrains.annotations.NotNull;
import org.jline.jansi.Ansi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public abstract class AbstractOutput implements Output {

  private static final String TOP_BORDER = "╭─%s─╮";
  private static final String MIDDLE_BORDER = "│ %s%s │";
  private static final String BOTTOM_BORDER = "╰─%s─╯";

  private final Collection<String> lines;

  protected AbstractOutput(Collection<String> lines) {
    Collection<String> validatedLines = new ArrayList<>();
    for (String line : lines) {
      if (line.contains(System.lineSeparator())) {
        String[] splitLines = line.split(System.lineSeparator());
        Collections.addAll(validatedLines, splitLines);
        continue;
      }
      validatedLines.add(line);
    }
    this.lines = validatedLines;
  }

  protected @NotNull String toBoxedString(Ansi.Color color) {
    int maxLength = lines.stream().map(String::length).max(Integer::compareTo).orElse(0);
    StringBuilder rendered = new StringBuilder();

    rendered.append(Ansi.ansi().fg(color))
      .append(TOP_BORDER.formatted("─".repeat(maxLength))).append(System.lineSeparator());

    for (String line : lines) {
      int paddingSize = maxLength - line.length();
      String padding = " ".repeat(paddingSize);
      rendered.append(MIDDLE_BORDER.formatted(line, padding)).append(System.lineSeparator());
    }

    rendered.append(BOTTOM_BORDER.formatted("─".repeat(maxLength)))
      .append(Ansi.ansi().reset());

    return rendered.toString();
  }

}
