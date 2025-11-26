package org.goodgallery.command;

import lombok.Getter;
import org.goodgallery.terminal.Output;
import org.goodgallery.terminal.TerminalContext;
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

  public void print(Output output) {
    terminalContext.print(output);
  }

  public void info(String title, Collection<String> messages) {
    print(Output.info(title, messages));
  }

  public void info(Collection<String> messages) {
    print(Output.info(messages));
  }

  public void info(String... messages) {
    print(Output.info(messages));
  }

  public void warn(String title, Collection<String> messages) {
    print(Output.warn(title, messages));
  }

  public void warn(Collection<String> messages) {
    print(Output.warn(messages));
  }

  public void warn(String... messages) {
    print(Output.warn(messages));
  }

  public void error(String title, Collection<String> messages) {
    print(Output.error(title, messages));
  }

  public void error(Collection<String> messages) {
    print(Output.error(messages));
  }

  public void error(String... messages) {
    print(Output.error(messages));
  }

  public void exception(String title, Exception exception) {
    print(Output.exception(title, exception));
  }

  public void exception(Exception exception) {
    print(Output.exception(exception));
  }

  public void output(Ansi.Color color, String title, Collection<String> messages) {
    print(new Output(color, title, messages));
  }

  public void output(Ansi.Color color, Collection<String> messages) {
    print(new Output(color, "", messages));
  }

  public void output(Ansi.Color color, String... messages) {
    print(new Output(color, "", Arrays.asList(messages)));
  }

}
