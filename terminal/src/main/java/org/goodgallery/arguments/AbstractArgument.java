package org.goodgallery.arguments;

import org.goodgallery.command.CommandContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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

  protected String name() {
    return name;
  }

  protected List<Argument<?>> arguments() {
    return arguments;
  }

  protected Consumer<CommandContext> executable() {
    return executable;
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
