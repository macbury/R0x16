package com.macbury.r0x16.widgets.code_editor;

import com.macbury.r0x16.widgets.JavaScriptScanner;
import com.macbury.r0x16.widgets.JavaScriptScanner.Kind;

public class Element {
  public JavaScriptScanner.Kind kind;
  public String text;
  
  public Element(Kind k, String t) {
    text = t;
    kind = k;
  }

  public int countSpaces() {
    int sum = 0;
    
    for (int i = 0; i < text.length(); i++) {
      char c = text.charAt(i);
      if (c == ' ') {
        sum += 1;
      } else {
        break;
      }
    }
    
    return sum;
  }
}
