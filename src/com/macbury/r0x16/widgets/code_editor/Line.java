package com.macbury.r0x16.widgets.code_editor;

import java.util.ArrayList;

public class Line extends ArrayList<Element> {

  public int textLenght() {
    int sum = 0;
    for (Element e : this) {
      sum += e.text.length();
    }
    return sum;
  }

}
