package org.goodgallery.terminal.output;

import org.jetbrains.annotations.NotNull;
import org.jline.jansi.Ansi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public abstract class AbstractOutput implements Output {

  private static final String RIGHT_TOP_BORDER = "╭─";
  private static final String HORIZONTAL_BORDER = "─";
  private static final String LEFT_TOP_BORDER = "─╮";
  private static final String TOP_BORDER = RIGHT_TOP_BORDER + "%s" + LEFT_TOP_BORDER;
  private static final String MIDDLE_BORDER = "│ %s%s │";
  private static final String BOTTOM_BORDER = "╰─%s─╯";

  private final String title;
  private final Collection<String> lines;

  protected AbstractOutput(String title, Collection<String> lines) {
    this.title = title;
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

  protected AbstractOutput(Collection<String> lines) {
    this("", lines);
  }

  protected @NotNull String toBoxedString(Ansi.Color color) {
    int maxLength = lines.stream().map(String::length).max(Integer::compareTo).orElse(0);
    StringBuilder rendered = new StringBuilder();

    rendered.append(Ansi.ansi().fg(color));

    if (title != null && !title.isEmpty()) {
      int sideLength = ((maxLength - title.length()) / 2) - 1;
      rendered.append(RIGHT_TOP_BORDER)
        .append(HORIZONTAL_BORDER.repeat(Math.max(0, sideLength)))
        .append(" ")
        .append(title)
        .append(" ")
        .append(HORIZONTAL_BORDER.repeat(Math.max(0, sideLength)))
        .append(LEFT_TOP_BORDER)
        .append(System.lineSeparator());
    }
    else
      rendered.append(TOP_BORDER.formatted(HORIZONTAL_BORDER.repeat(maxLength)))
        .append(System.lineSeparator());

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
