package org.goodgallery.command;

import lombok.Getter;
import org.goodgallery.terminal.TerminalContext;
import org.goodgallery.terminal.output.*;
import org.goodgallery.terminal.output.Error;
import org.jline.jansi.Ansi;

import java.util.*;

public class CommandContext implements Iterator<String> {

  private static String[] tokenize(String rawCommand) {
    List<String> tokens = new ArrayList<>();

    tokens.add(rawCommand.split(" ")[0]);
    rawCommand = rawCommand.substring(tokens.getFirst().length()).trim();
    StringBuilder currentToken = new StringBuilder();
    boolean inQuotes = false;

    for (char c : rawCommand.toCharArray()) {
      if (c == '"')
        inQuotes = !inQuotes;
      else if (inQuotes)
        currentToken.append(c);
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

  private final TerminalContext terminalContext;

  @Getter
  private final String label;
  @Getter
  private final String[] args;
  private int index;

  private final Map<String, Object> parsedArguments;

  public CommandContext(TerminalContext terminalContext, String rawCommand) {
    this.terminalContext = terminalContext;
    String[] tokens = tokenize(rawCommand);
    this.label = tokens[0];
    this.args = Arrays.copyOfRange(tokens, 1, tokens.length);
    this.index = 0;
    this.parsedArguments = new HashMap<>();
  }

  public void info(Collection<String> messages) {
    terminalContext.print(new Info(messages));
  }

  public void info(String... messages) {
    info(Arrays.asList(messages));
  }

  public void info(String message, Object... args) {
    info(message.formatted(args));
  }

  public void warn(Collection<String> messages) {
    terminalContext.print(new Warning(messages));
  }

  public void warn(String... messages) {
    warn(Arrays.asList(messages));
  }

  public void warn(String message, Object... args) {
    warn(message.formatted(args));
  }

  public void error(Collection<String> messages) {
    terminalContext.print(new Error(messages));
  }

  public void error(String... messages) {
    error(Arrays.asList(messages));
  }

  public void error(String message, Object... args) {
    error(message.formatted(args));
  }

  public void exception(Exception exception) {
    terminalContext.print(new Error(exception));
  }

  public void customOutput(Ansi.Color color, String... messages) {
    terminalContext.print(new CustomOutput(color, Arrays.asList(messages)));
  }

  public void customOutput(Ansi.Color color, String message, Object... args) {
    customOutput(color, message.formatted(args));
  }

  public void print(Output output) {
    terminalContext.print(output);
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
