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

  public String text() {
    String s = "";
    for (Element e : this) {
      s += e.text;
    }
    return s;
  }
  
  public char charAt(int col) {
    try {
      return text().charAt(col);
    } catch(StringIndexOutOfBoundsException e) {
      return ' ';
    }
  }

}
