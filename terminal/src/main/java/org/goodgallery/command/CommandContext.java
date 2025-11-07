package org.goodgallery.command;

import lombok.Getter;
import org.goodgallery.arguments.InternalArgument;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CommandContext {

  private final PrintStream out;
  @Getter
  private final String label;
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
    this(out, rawCommand.split(" "));
  }

  public PrintStream out() {
    return out;
  }

  public boolean validateArgument(InternalArgument<?> argument) {
    return argument.isValidInput(args[index]);
  }

  public boolean hasNextArg() {
    return index < args.length;
  }

  public String getNextArg() {
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

  @Override
  public String toString() {
    return "label=" + label + ", args=" + Arrays.toString(args);
  }

}
