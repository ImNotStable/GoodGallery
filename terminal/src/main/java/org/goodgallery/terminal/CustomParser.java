package org.goodgallery.terminal;

import org.jline.reader.impl.DefaultParser;

public class CustomParser extends DefaultParser {

  @Override
  public boolean isEscapeChar(char c) {
    return false;
  }

}
