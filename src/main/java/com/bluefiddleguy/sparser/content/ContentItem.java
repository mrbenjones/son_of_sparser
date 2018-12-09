package com.bluefiddleguy.sparser.content;

import java.util.List;
import java.util.Map;

public interface ContentItem {
    public String getValue(String key);
    public int getCounter(String key);
    public MarkupTag getTag();
    public ContentTable getTable();
    public List<ContentItem> getChildren();
    public ContentItem getReference(String key);
    public String getContent();

}
