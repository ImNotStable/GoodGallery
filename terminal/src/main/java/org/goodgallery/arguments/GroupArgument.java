package org.goodgallery.arguments;

import org.goodgallery.gallery.Gallery;
import org.goodgallery.gallery.Group;

public class GroupArgument extends AbstractArgument<Group> {

  private final Gallery gallery;

  GroupArgument(String name, Gallery gallery) {
    super(name);
    this.gallery = gallery;
  }

  @Override
  public InternalArgument<Group> toInternal() {
    return toInternal("\"group\"", gallery::hasGroup, input -> gallery.getGroup(input).orElse(null));
  }

}
