package com.bluefiddleguy.sparser.content;

import java.util.List;

public class ContentTableFactory {
    public ContentTable newTable(String caption, List<String> headers, List<List<String>> content){
        return new SimpleContentTable(caption, headers, content);
    }
}
