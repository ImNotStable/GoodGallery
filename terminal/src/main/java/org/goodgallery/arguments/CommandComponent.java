package org.goodgallery.arguments;

public interface CommandComponent {

  String getName();
  default String getLabel() {
    return getName();
  }
  

}
