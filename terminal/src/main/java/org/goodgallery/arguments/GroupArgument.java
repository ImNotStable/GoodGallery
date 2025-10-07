package org.goodgallery.arguments;

import org.goodgallery.gallery.Gallery;
import org.goodgallery.gallery.Group;

import java.util.stream.Collectors;

public class GroupArgument extends AbstractArgument<Group> {

  private final Gallery gallery;

  GroupArgument(String name, Gallery gallery) {
    super(name);
    this.gallery = gallery;
  }

  @Override
  public InternalArgument<Group> toInternal() {
    return new InternalArgumentImpl<>(
      name(),
      arguments().stream().map(Argument::toInternal).collect(Collectors.toSet()),
      executable()
    ) {
      @Override
      public String getUsageForm() {
        return "\"group\"";
      }

      @Override
      public boolean isValidInput(String input) {
        return gallery.hasGroup(input);
      }

      @Override
      public Group parse(String input) {
        return gallery.getGroup(input);
      }
    };
  }

}
