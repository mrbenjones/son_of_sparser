package com.bluefiddleguy.sparser.content;

import java.util.HashMap;
import java.util.Map;

public enum MarkupTag {
    PLAIN("plain"),
    DEFAUL("default");

    private String config;

    MarkupTag(String config){
        this.config = config;
    }

    public String getConfig(){
        return this.config;
    }

    static final Map<String,MarkupTag> lookup = new HashMap<String,MarkupTag>();
    static {
        for (MarkupTag tag : MarkupTag.values()){
            lookup.put(tag.config,tag);
        }
    }

    public static MarkupTag get(String config){
        return lookup.get(config);
    }
}
