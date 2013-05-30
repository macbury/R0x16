package com.macbury.r0x16.widgets.code_editor;

import java.util.ArrayList;

public class Line extends ArrayList<Element> {
  private String cachedFullText = "";
  public int textLenght() {
    return text().length();
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

  public void buildString() {
    setCachedFullText(text());
  }

  public String getCachedFullText() {
    return cachedFullText;
  }

  public void setCachedFullText(String cachedFullText) {
    this.cachedFullText = cachedFullText;
  }
}
