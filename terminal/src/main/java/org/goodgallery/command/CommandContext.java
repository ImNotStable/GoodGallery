package org.goodgallery.command;

import lombok.Getter;

import java.io.PrintStream;
import java.util.*;

public class CommandContext {

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

  private final PrintStream out;
  @Getter
  private final String label;
  @Getter
  private final String[] args;
  private int index;

  private final Map<String, Object> parsedArguments;

  public CommandContext(PrintStream out, String label, String[] args) {
    this.out = out;
    this.label = label;
    this.args = args;
    this.index = 0;
    this.parsedArguments = new HashMap<>();
  }

  public CommandContext(PrintStream out, String[] command) {
    this(out, command[0], Arrays.copyOfRange(command, 1, command.length));
  }

  public CommandContext(PrintStream out, String rawCommand) {
    this(out, tokenize(rawCommand));
  }

  public PrintStream out() {
    return out;
  }

  public boolean hasNextArg() {
    return index < args.length;
  }

  public String getNextArg() {
    return args[index++];
  }

  public String getGreedyArgs() {
    StringBuilder sb = new StringBuilder();
    while (hasNextArg()) {
      sb.append(getNextArg());
      if (hasNextArg())
        sb.append(" ");
    }
    return sb.toString();
  }

  public String peakNextArg() {
    return args[index];
  }

  public String peakGreedyArgs() {
    StringBuilder sb = new StringBuilder();
    int tempIndex = index;
    while (tempIndex < args.length) {
      sb.append(args[tempIndex++]);
      if (tempIndex < args.length)
        sb.append(" ");
    }
    return sb.toString();
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
