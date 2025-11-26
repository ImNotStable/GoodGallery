package org.goodgallery.terminal;

import org.jetbrains.annotations.NotNull;
import org.jline.jansi.Ansi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class Output {

  private static final String TOP_LEFT_BORDER = "╭─", HORIZONTAL_BORDER = "─", TOP_RIGHT_BORDER = "─╮",
    VERTICAL_BORDER = "│",
    BOTTOM_LEFT_BORDER = "╰─", BOTTOM_RIGHT_BORDER = "─╯";
  private static final String TOP_BORDER = TOP_LEFT_BORDER + "%s" + TOP_RIGHT_BORDER;
  private static final String MIDDLE_BORDER = VERTICAL_BORDER + " %s%s " + VERTICAL_BORDER;
  private static final String BOTTOM_BORDER = BOTTOM_LEFT_BORDER + "%s" + BOTTOM_RIGHT_BORDER;


  public static Output info(String title, Collection<String> lines) {
    return new Output(Ansi.Color.WHITE, title, lines);
  }

  public static Output info(String title, String... lines) {
    return info(title, Arrays.asList(lines));
  }

  public static Output info(Collection<String> lines) {
    return info("", lines);
  }

  public static Output info(String... lines) {
    return info(Arrays.asList(lines));
  }

  public static Output warn(String title, Collection<String> lines) {
    return new Output(Ansi.Color.YELLOW, title, lines);
  }

  public static Output warn(String title, String... lines) {
    return warn(title, Arrays.asList(lines));
  }

  public static Output warn(Collection<String> lines) {
    return warn("", lines);
  }

  public static Output warn(String... lines) {
    return warn(Arrays.asList(lines));
  }

  public static Output error(String title, Collection<String> lines) {
    return new Output(Ansi.Color.RED, title, lines);
  }

  public static Output error(String title, String... lines) {
    return error(title, Arrays.asList(lines));
  }

  public static Output error(Collection<String> lines) {
    return error("", lines);
  }

  public static Output error(String... lines) {
    return error(Arrays.asList(lines));
  }

  public static Output exception(String title, Exception exception) {
    Collection<String> lines = new ArrayList<>();
    lines.add(exception.getMessage());
    for (StackTraceElement element : exception.getStackTrace())
      lines.add(" " + element.toString());
    return new Output(Ansi.Color.RED, title, lines);
  }

  public static Output exception(Exception exception) {
    return exception("Exception", exception);
  }

  private final Ansi.Color color;
  private final String title;
  private final Collection<String> lines;

  public Output(Ansi.Color color, String title, Collection<String> lines) {
    this.color = color;
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

  public @NotNull String toRenderedString() {
    int maxLength = lines.stream().map(String::length).max(Integer::compareTo).orElse(0);
    StringBuilder rendered = new StringBuilder();

    rendered.append(Ansi.ansi().fg(color));

    if (title != null && !title.isEmpty()) {
      int length = title.length() + 2;
      double sideLength = ((maxLength - length) / 2.0);
      int leftSideLength = (int) Math.floor(sideLength);
      int rightSideLength = (int) Math.ceil(sideLength);
      rendered.append(TOP_LEFT_BORDER)
        .append(HORIZONTAL_BORDER.repeat(Math.max(0, leftSideLength)))
        .append(" ")
        .append(title)
        .append(" ")
        .append(HORIZONTAL_BORDER.repeat(Math.max(0, rightSideLength)))
        .append(TOP_RIGHT_BORDER)
        .append(System.lineSeparator());
      if (maxLength < length)
        maxLength = length;
    } else
      rendered.append(TOP_BORDER.formatted(HORIZONTAL_BORDER.repeat(maxLength)))
        .append(System.lineSeparator());

    for (String line : lines) {
      int paddingSize = maxLength - line.length();
      String padding = " ".repeat(paddingSize);
      rendered.append(MIDDLE_BORDER.formatted(line, padding)).append(System.lineSeparator());
    }

    rendered.append(BOTTOM_BORDER.formatted(HORIZONTAL_BORDER.repeat(maxLength)))
      .append(Ansi.ansi().reset());

    return rendered.toString();
  }

}
