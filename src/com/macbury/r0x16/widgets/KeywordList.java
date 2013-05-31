package com.macbury.r0x16.widgets;

import com.badlogic.gdx.Gdx;

public class KeywordList {

  private static final String TAG = "KeywordList";
  private final String[] keywords;
  private final int maxLength;

  /**
   * Constructs a keyword list from an sorted list of keywords (sorted on char codes)
   * @param keywords the list of keywords
   */
  public KeywordList(String ... keywords) {
      int len = 0;
      for(String kw : keywords) {
          len = Math.max(len, kw.length());
      }
      
      this.keywords = keywords;
      this.maxLength = len;
  }

  public int getMaxLength() {
      return maxLength;
  }
  
  public boolean isKeyword(char[] buf, int start, int len) {
    if(len > maxLength) {
      return false;
    }
    
    int kwidx = 0;
    String text = "";
    for(int chpos=0 ; chpos < len ; chpos++) {
      text += buf[start + chpos];
    }
    
    for (String k : keywords) {
      if (k.equalsIgnoreCase(text)) {
        return true;
      }
    }
    
    return false;
  }
  
  /*public boolean isKeyword(char[] buf, int start, int len) {
      if(len > maxLength) {
          return false;
      }
      int kwidx = 0;
      for(int chpos=0 ; chpos<len ; chpos++) {
          char c = buf[start + chpos];
          for(;;) {
              String kw = keywords[kwidx];
              Gdx.app.log(TAG, kw);
              if(chpos < kw.length()) {
                  char kwc = kw.charAt(chpos);
                  if(kwc == c) {
                      break;
                  }
                  if(kwc > c) {
                      return false;
                  }
              }
              if(++kwidx == keywords.length) {
                  return false;
              }
          }
      }
      return keywords[kwidx].length() == len;
  }*/
}
