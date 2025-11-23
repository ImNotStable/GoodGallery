package org.goodgallery.command;

import lombok.Getter;

import java.io.PrintWriter;
import java.util.*;

public class CommandContext implements Iterator<String> {

  private static String[] tokenize(String rawCommand) {
    List<String> tokens = new ArrayList<>();

    tokens.add(rawCommand.split(" ")[0]);
    rawCommand = rawCommand.substring(tokens.getFirst().length()).trim();
    StringBuilder currentToken = new StringBuilder();
    boolean escapeNext = false;
    boolean inQuotes = false;

    for (char c : rawCommand.toCharArray()) {
      if (escapeNext) {
        currentToken.append(c);
        escapeNext = false;
      }
      else if (c == '"')
        inQuotes = !inQuotes;
      else if (inQuotes)
        currentToken.append(c);
      else if (c == '\\')
        escapeNext = true;
      else if (Character.isWhitespace(c)) {
        if (!currentToken.isEmpty()) {
          tokens.add(currentToken.toString());
          currentToken.setLength(0);
        }
      } else
        currentToken.append(c);
    }
    if (!currentToken.isEmpty())
      tokens.add(currentToken.toString());

    return tokens.toArray(new String[0]);
  }

  private final PrintWriter writer;
  @Getter
  private final String label;
  @Getter
  private final String[] args;
  private int index;

  private final Map<String, Object> parsedArguments;

  public CommandContext(PrintWriter writer, String label, String[] args) {
    this.writer = writer;
    this.label = label;
    this.args = args;
    this.index = 0;
    this.parsedArguments = new HashMap<>();
  }

  public CommandContext(PrintWriter writer, String[] command) {
    this(writer, command[0], Arrays.copyOfRange(command, 1, command.length));
  }

  public CommandContext(PrintWriter writer, String rawCommand) {
    this(writer, tokenize(rawCommand));
  }

  public PrintWriter writer() {
    return writer;
  }

  public boolean hasNext() {
    return index < args.length;
  }

  public String peek() {
    return args[index];
  }

  public String next() {
    return args[index++];
  }

  public void put(String argumentKey, Object parsedArgument) {
    parsedArguments.put(argumentKey, parsedArgument);
  }

  public Object get(String argumentKey) {
    return parsedArguments.get(argumentKey);
  }

  public <T> T get(String argumentKey, Class<T> clazz) {
    return clazz.cast(get(argumentKey));
  }

}
