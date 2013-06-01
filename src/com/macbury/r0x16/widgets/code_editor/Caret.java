package com.macbury.r0x16.widgets.code_editor;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;

public class Caret {
  private static final String TAG = "Caret";
  private int col = 0;
  private int row = 0;
  private ArrayList<Line> lines;
  private boolean haveSelection = false;
  private int selectionStartRow;
  private int selectionStartCol;
  private int rowScrollPosition = 0;
  private int colScrollPosition = 0;
  
  public Caret(ArrayList<Line> linesArray) {
    this.lines = linesArray;
  }
  
  public int getRow() {
    return row;
  }

  public void moveRowDown() {
    setRow(getRow()+1);
  }
  
  public void moveRowUp() {
    setRow(getRow()-1);
  }
  
  public void setRow(int row) {
    this.row = row;
    fixRowBoundsValue();
  }

  public int getCol() {
    return col;
  }

  public void setCol(int col) {
    this.col = col;
    fixColBoundsValue();
  }
  
  public int getCaretPosition() {
    return getCaretPositionFor(row, col);
  }
  
  public Line getCurrentLine() {
    return getLineForRow(row);
  }
  
  public boolean moveOneCharLeft() {
    col--;
    if (col < 0) {
      row--;
      Line line = getCurrentLine();
      if (line == null) {
        col = 0;
        row = 0;
        return false;
      } else {
        col = line.textLenght();
        return true;
      }
    }
    return true;
  }
  
  public boolean moveOneCharRight() {
    Line line = getCurrentLine();
    
    if (line != null) {
      if (col >= line.textLenght()) {
        row++;
        line = getCurrentLine();
        
        if (line == null) {
          row--;
          return false;
        } else {
          col = 0;
          return true;
        }
      } else {
        col++;
        return true;
      }
    } else {
      col = 0;
      return true;
    }
  }
  
  public char getCurrentChar() {
    Line line = getCurrentLine();
    
    if (line != null) {
      return line.charAt(col);
    } else {
      return ' ';
    }
  }
  
  public boolean canMoveCursorInDirection(int direction) {
    Line line = getCurrentLine();
    int nr    = row;
    int nc    = col + direction;
    if (line != null) {
      if (nc < 0) {
        nr--;
        line = getLineForRow(nr);
        if (line == null) {
          nr = 0;
          return false;
        } else {
          return true;
        }
      } else if (nc >= line.textLenght()) {
        nr++;
        line = getLineForRow(nr);
        if (line == null) {
          return false;
        } else {
          return true;
        }
      } else {
        return true;
      }
    } else {
      return false;
    }
  }
  
  public char getCharInDirection(int direction) {
    Line line = getCurrentLine();
    int nr    = row;
    int nc    = col + direction;
    if (line != null) {
      if (nc < 0) {
        nr--;
        line = getLineForRow(nr);
        if (line == null) {
          nr = 0;
          return ' ';
        } else {
          nc = line.textLenght();
          return line.charAt(nc);
        }
      } else if (nc >= line.textLenght()) {
        nr++;
        line = getLineForRow(nr);
        if (line == null) {
          return ' ';
        } else {
          nc = 0;
          return line.charAt(nc);
        }
      } else {
        return line.charAt(nc);
      }
    } else {
      return ' ';
    }
  }
  
  public void moveByWordInDirection(int direction) {
    while (canMoveCursorInDirection(direction)) {
      char c = getCharInDirection(direction);
      Gdx.app.log(TAG, String.valueOf(c));
      if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')) {
        moveOneCharInDirection(direction);
      } else {
        break;
      }
    }
  }
  
  public void moveOneCharInDirection(int direction) {
    if (direction == -1) {
      moveOneCharLeft();
    } else {
      moveOneCharRight();
    }
  }
  
  public void setCursorPosition(int x, int y) {
    //Gdx.app.log(TAG, "Set cursor position at: " + x + "x"+y + "  ");
    Line line = getLineForRow(y);
    
    if (line == null) {
      row = (this.lines.size() - 1);
      line = this.getLineForRow(row);
    } else {
      row = y;
    }
    
    col = x;
    fixColBoundsValue();
  }
  
  public void fixColBoundsValue() {
    Line line = getCurrentLine();
    if (col > line.textLenght()) {
      col = line.textLenght();
    }
  }
  
  public void fixRowBoundsValue() {
    if (row < 0) {
      row = 0;
    }
    
    if (row >= this.lines.size()) {
      row = this.lines.size() - 1;
    }
    
    fixColBoundsValue();
  }

  public Line getLineForRow(int y) {
    try {
      return lines.get(y);
    } catch (IndexOutOfBoundsException e) {
      return null;
    }
  }
  
  public int getCaretPositionFor(int r, int c) {
    Line line = getLineForRow(r);
    if (line == null) {
      return 0;
    } else {
      int width = c;
      for (int y = 0; y < r; y++) {
        line = getLineForRow(y);
        width += line.textLenght();
      }
      return width;
    }
  }
  


  public void setColHome() {
    setCol(0);
  }

  public void setColEnd() {
    Line line = getCurrentLine();
    if (line != null) {
      setCol(line.textLenght());
    } else {
      setCol(0);
    }
    //clearSelection();
  }

  public void moveByWordInLeft() {
    while (col-- > 0) {
      char c = getChar(col - 1);
      if (c >= 'A' && c <= 'Z') continue;
      if (c >= 'a' && c <= 'z') continue;
      if (c >= '0' && c <= '9') continue;
      break;
    }
    if (col < 0 && row > 0) {
      moveRowUp();
      Line line = getCurrentLine();
      col = line.textLenght();
    } else if (col < 0 && row == 0) {
      col = 0;
    }
  }

  public void moveByWordInRight() {
    int length = getCurrentLine().textLenght();
    while (col < length) {
      col++;
      char c = getChar(col - 1);
      if (c >= 'A' && c <= 'Z') continue;
      if (c >= 'a' && c <= 'z') continue;
      if (c >= '0' && c <= '9') continue;
      break;
    }
    
    if (col > length) {
      moveRowDown();
      col = 0;
    }
  }
  
  public char getChar(int i) {
    return getCurrentLine().charAt(i);
  }

  public void incCol(int i) {
    this.col++;
  }

  public void incRow() {
    this.row++;
  }

  public boolean haveSelection() {
    return haveSelection;
  }

  public void startSelection() {
    if (!haveSelection()) {
      haveSelection = true;
      //Gdx.app.log(TAG, "Start selection");
      this.selectionStartCol = col;
      this.selectionStartRow = row;
    }
    
  }
  
  public void clearSelection() {
    if (haveSelection) {
      //Gdx.app.log(TAG, "Stoping selection");
      haveSelection = false;
    }
  }

  public int getSelectionStartRow() {
    return selectionStartRow;
  }

  public int getSelectionStartCol() {
    return selectionStartCol;
  }

  public int getRowScrollPosition() {
    return rowScrollPosition;
  }

  public void setRowScrollPosition(int rowScrollPosition) {
    this.rowScrollPosition = rowScrollPosition;
  }

  public int getColScrollPosition() {
    return colScrollPosition;
  }

  public void setColScrollPosition(int colScrollPosition) {
    this.colScrollPosition = colScrollPosition;
  }
}
