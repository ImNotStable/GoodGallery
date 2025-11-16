package org.goodgallery.arguments;

import org.goodgallery.command.CommandContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
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

  protected String name() {
    return name;
  }

  protected List<Argument<?>> arguments() {
    return arguments;
  }

  protected Consumer<CommandContext> executable() {
    return executable;
  }

  protected InternalArgument<I> toInternal(String usage, Predicate<CommandContext> inputValidator, Function<CommandContext, I> parser) {
    return new InternalArgument<>(
      name(),
      arguments().stream().map(Argument::toInternal).collect(Collectors.toSet()),
      executable()
    ) {

      @Override
      public String getUsage() {
        return usage;
      }

      @Override
      public boolean isValidInput(CommandContext input) {
        return inputValidator.test(input);
      }

      @Override
      public I parse(CommandContext input) {
        return parser.apply(input);
      }
    };
  }

  protected InternalArgument<I> toQuickInternal(String usage, Predicate<String> inputValidator, Function<String, I> parser) {
    return new InternalArgument<>(
      name(),
      arguments().stream().map(Argument::toInternal).collect(Collectors.toSet()),
      executable()
    ) {

      @Override
      public String getUsage() {
        return usage;
      }

      @Override
      public boolean isValidInput(CommandContext input) {
        return inputValidator.test(input.peakNextArg());
      }

      @Override
      public I parse(CommandContext input) {
        return parser.apply(input.getNextArg());
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
