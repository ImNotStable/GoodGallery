package org.goodgallery.arguments;

import org.goodgallery.command.CommandContext;

import java.util.Set;
import java.util.function.Consumer;

abstract class InternalArgumentImpl<O> implements InternalArgument<O> {

  private final String name;
  private final Set<? extends InternalArgument<?>> arguments;
  private final Consumer<CommandContext> executable;

  InternalArgumentImpl(String name, Set<? extends InternalArgument<?>> arguments, Consumer<CommandContext> executable) {
    this.name = name;
    this.arguments = arguments;
    this.executable = executable;
  }

  public String name() {
    return name;
  }

  public abstract String getUsageForm();

  public abstract boolean isValidInput(String input);

  public abstract O parse(String input);

  @Override
  public boolean execute(CommandContext context) {

    if (!context.validateArgument(this))
      return false;

    O arg = parse(context.getNextArg());

    context.put(name, arg);

    if (!context.hasNextArg()) {
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
