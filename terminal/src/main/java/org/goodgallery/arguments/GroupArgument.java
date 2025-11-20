package org.goodgallery.arguments;

import org.goodgallery.command.CommandContext;
import org.goodgallery.gallery.Gallery;
import org.goodgallery.gallery.Group;

public class GroupArgument extends AbstractArgument<Group> {

  private final Gallery gallery;

  GroupArgument(String name, Gallery gallery) {
    super(name);
    this.gallery = gallery;
  }

  @Override
  protected String getUsage() {
    return "<group>";
  }

  @Override
  protected boolean isValidInput(CommandContext context) {
    return gallery.hasGroup(context.peak());
  }

  @Override
  protected Group parse(CommandContext context) {
    return gallery.getGroup(context.next()).orElse(null);
  }

}
