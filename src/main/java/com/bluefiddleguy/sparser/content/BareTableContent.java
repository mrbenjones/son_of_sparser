package com.bluefiddleguy.sparser.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BareTableContent implements  ContentItem{

    private String content;
    private MarkupTag tag;
    private ContentTable table;
    private List<ContentItem> children = new ArrayList<ContentItem>();
    private Map<String,ContentItem> references = new HashMap<String,ContentItem>();
    private Map<String,String> attributes = new HashMap<String,String>();
    public BareTableContent(ContentTable table){
        this.tag = MarkupTag.PLAIN;
        this.content = "TABLE\n";
        this.table = table;
    }
    @Override
    public String getValue(String key) {
        return this.attributes.get(key);
    }

    @Override
    public int getCounter(String key) {
        return 0;
    }

    @Override
    public MarkupTag getTag() {
        return this.tag;
    }

    @Override
    public ContentTable getTable() {
        return this.table;
    }

    @Override
    public List<ContentItem> getChildren() {
        return this.children;
    }

    @Override
    public ContentItem getReference(String key) {
        return this.references.get(key);
    }

    @Override
    public String getContent() {
        return this.content;
    }
}
