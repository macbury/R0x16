package com.macbury.r0x16.widgets;

import java.io.IOException;
import java.io.Reader;

public final class CharacterIterator {

  public static final int EOF = -1;

  private final Reader r;
  
  private char[] buffer;
  private int bufferStart;
  private int pos;
  private int start;
  private int end;
  private int marker;
  private boolean skipCR;
  private boolean atEOF;

  public CharacterIterator(CharSequence cs) {
      int len = cs.length();
      this.r = null;
      this.buffer = new char[len];
      this.end = len;
      
      if(cs instanceof String) {
          ((String)cs).getChars(0, len, buffer, 0);
      } else {
          for(int i=0 ; i<len ; i++) {
              buffer[i] = cs.charAt(i);
          }
      }
  }
  
  public CharacterIterator(Reader r) {
      this.r = r;
      this.buffer = new char[4096];
      this.marker = -1;
  }

  public int length() {
      return pos - start;
  }

  public String getString() {
      return new String(buffer, start, length());
  }

  public void clear() {
      start = pos;
      marker = -1;
  }

  public int peek() {
      if(pos < end || refill()) {
          char ch = buffer[pos];
          if(ch == '\r') {
              if(skipCR) {
                  ++pos;
                  skipCR = false;
                  return peek();
              }
              ch = '\n';
          } else if(ch == '\n') {
              skipCR = true;
          }
          return ch;
      }
      atEOF = true;
      return EOF;
  }

  public void pushback() {
      if(pos > start && !atEOF) {
          pos--;
          marker = -1;
      }
  }
  
  public void advanceToEOL() {
      for(;;) {
          int ch = peek();
          if(ch < 0 || ch == '\n') {
              return;
          }
          pos++;
      }
  }

  public void advanceIdentifier() {
      while(Character.isJavaIdentifierPart(peek())) {
          pos++;
      }
  }

  public int next() {
      int ch = peek();
      if(ch >= 0) {
          pos++;
      }
      return ch;
  }

  public boolean check(String characters) {
      if(pos < end || refill()) {
          return characters.indexOf(buffer[pos]) >= 0;
      }
      return false;
  }

  public void setMarker(boolean pushback) {
      marker = pos;
      if(pushback && pos > start) {
          marker--;
      }
  }

  public boolean isMarkerAtStart() {
      return marker == start;
  }
  
  public void rewindToMarker() {
      if(marker >= start) {
          pos = marker;
          marker = -1;
      }
  }

  public boolean isKeyword(KeywordList list) {
      return marker >= 0 && list.isKeyword(buffer, marker, pos - marker);
  }

  public int getCurrentPosition() {
      return bufferStart + pos;
  }
  
  private void compact() {
      bufferStart += start;
      pos -= start;
      marker -= start;
      end -= start;
      if(pos > buffer.length*3/2) {
          char[] newBuffer = new char[buffer.length * 2];
          System.arraycopy(buffer, start, newBuffer, 0, end);
          buffer = newBuffer;
      } else if(end > 0) {
          System.arraycopy(buffer, start, buffer, 0, end);
      }
      start = 0;
  }

  private boolean refill() {
      if(r == null) {
          return false;
      }
      
      compact();

      try {
          int read = r.read(buffer, end, buffer.length - end);
          if(read <= 0) {
              return false;
          }
          end += read;
          return true;
      } catch (IOException ignored) {
          return false;
      }
  }

}
