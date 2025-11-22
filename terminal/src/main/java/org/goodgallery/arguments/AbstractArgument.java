package org.goodgallery.arguments;

import org.goodgallery.command.CommandContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class AbstractArgument<I> implements Argument<I> {

  private final String name;
  private final List<Argument<?>> arguments;
  private Consumer<CommandContext> executable;

  protected AbstractArgument(String name) {
    this.name = name.toLowerCase();
    this.arguments = new ArrayList<>();
    this.executable = _ -> {
    };
  }

  protected abstract String getUsage();
  protected abstract boolean isValidInput(CommandContext context);
  protected abstract I parse(CommandContext context);

  protected String name() {
    return name;
  }

  protected List<Argument<?>> arguments() {
    return arguments;
  }

  protected Consumer<CommandContext> executable() {
    return executable;
  }

  public InternalArgument<I> toInternal() {
    return new InternalArgument<>(
      name(),
      arguments().stream().map(Argument::toInternal).collect(Collectors.toSet()),
      executable()
    ) {

      @Override
      public String getUsage() {
        return AbstractArgument.this.getUsage();
      }

      @Override
      public boolean isValidInput(CommandContext input) {
        return AbstractArgument.this.isValidInput(input);
      }

      @Override
      public I parse(CommandContext input) {
        return AbstractArgument.this.parse(input);
      }
    };
  }

  @Override
  public <N> Argument<I> then(Argument<N> argument) {
    arguments.add(argument);
    return this;
  }

  @Override
  public Argument<I> executes(Consumer<CommandContext> executable) {
    this.executable = executable;
    return this;
  }

  @Override
  public String toString() {
    return name;
  }

}
