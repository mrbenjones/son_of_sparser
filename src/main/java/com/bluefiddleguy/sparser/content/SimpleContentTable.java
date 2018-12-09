package com.bluefiddleguy.sparser.content;

import java.util.ArrayList;
import java.util.List;

public class SimpleContentTable implements ContentTable {
    @Override
    public int getNRows() {
        return rows;
    }

    @Override
    public int getNCols() {
        return cols;
    }

    @Override
    public String getCaption() {
        return caption;
    }

    @Override
    public String getHeader(int column) {
        if (column >= this.headers.size()){
            return "";
        }
        else{
            return this.headers.get(column);
        }
    }

    @Override
    public String getCell(int row, int column) {
        List<String> inrow = this.getRow(row);
        if (column >= inrow.size()){
            return "";
        }
        else{
            return inrow.get(column);
        }
    }

    @Override
    public List<String> getRow(int row) {
        if (row >= this.content.size()){
            return new ArrayList<String>();
        }
        return this.content.get(row);
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }


    private String caption;
    private List<String> headers;
    private List<List<String>> content;

    private int rows;


    private int cols;

    public SimpleContentTable(String caption, List<String> headers, List<List<String>> content) {
        this.caption = caption;
        this.headers = headers;
        this.content = content;
        this.rows = content.size();
        // the number of columns is the maximum of the size of all of the rows and the headers.
        this.cols = Math.max(headers.size(),
                content.stream().mapToInt(v -> v.size()).max().getAsInt());
    }


}
