package com.bluefiddleguy.sparser.content;

import java.util.List;

public interface ContentTable {
    int getNRows();
    int getNCols();
    String getCaption();
    String getHeader(int column);
    String getCell(int row, int column);
    List<String> getRow(int row);
}
