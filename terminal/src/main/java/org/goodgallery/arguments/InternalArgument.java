package org.goodgallery.arguments;

import org.goodgallery.command.CommandContext;

import java.util.Set;
import java.util.function.Consumer;

public abstract class InternalArgument<O> {

  private final String name;
  private final Set<? extends InternalArgument<?>> arguments;
  private final Consumer<CommandContext> executable;

  InternalArgument(String name, Set<? extends InternalArgument<?>> arguments, Consumer<CommandContext> executable) {
    this.name = name;
    this.arguments = arguments;
    this.executable = executable;
  }

  public String name() {
    return name;
  }

  public abstract String getUsage();

  public abstract boolean isValidInput(CommandContext input);

  public abstract O parse(CommandContext input);

  public boolean execute(CommandContext context) {
    if (!isValidInput(context))
      return false;

    O arg = parse(context);

    context.put(name, arg);

    if (!context.hasNext()) {
      executable.accept(context);
      return true;
    }

    for (InternalArgument<?> argument : arguments)
      if (argument.execute(context))
        return true;

    return false;
  }

  @Override
  public String toString() {
    return name;
  }

}
