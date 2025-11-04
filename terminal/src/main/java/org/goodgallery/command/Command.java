package org.goodgallery.command;

import org.goodgallery.arguments.Argument;
import org.goodgallery.arguments.InternalArgument;
import org.goodgallery.arguments.LiteralArgument;

import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class Command {

  public static Builder builder(String command) {
    return new Builder(command);
  }

  private final String command;
  private final Set<? extends InternalArgument<?>> arguments;
  private final Consumer<CommandContext> executable;

  private Command(String command, Set<? extends InternalArgument<?>> arguments, Consumer<CommandContext> executable) {
    this.command = command;
    this.arguments = arguments;
    this.executable = executable;
  }

  public boolean isCommand(String command) {
    return this.command.equals(command);
  }

  public boolean execute(CommandContext context) {
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
    return command;
  }

  public static class Builder extends LiteralArgument {

    public Builder(String command) {
      super(command);
    }

    public Command build() {
      return new Command(
        name(),
        arguments().stream().map(Argument::toInternal).collect(Collectors.toSet()),
        executable()
      );
    }

    public void register(CommandDispatcher dispatcher) {
      dispatcher.addCommand(build());
    }

    @Override
    public <N> Builder then(Argument<N> argument) {
      super.then(argument);
      return this;
    }

    @Override
    public Builder executes(Consumer<CommandContext> executable) {
      super.executes(executable);
      return this;
    }

  }

}
