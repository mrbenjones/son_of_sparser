package com.bluefiddleguy.sparser.render;

import java.util.HashMap;
import java.util.Map;

public enum ParserState {
    TEXT("text"),
    TAG("tag"),
    CHILDREN("children"),
    REFERENCE("reference"),
    ROWS("rows"),
    CELLS("cells"),
    CELL("cell"),
    CAPTION("caption"),
    COUNTER("count"),
    INCREMENT_COUNTER("increment_counter"),
    TABLE("table"),
    HEADER_CELLS("header_cells"),
    ATTRIBUTE("attribute");

    private String stageName;
    private static final Map<String,ParserState> lookup = new HashMap<String,ParserState>();

    public String getStageName(){
        return this.stageName;
    }
    static {
        for (ParserState state:ParserState.values()){
            lookup.put(state.stageName,state);
        }
    }


    /**
     * Default parser state to text.
     * @param stageName
     * @return
     */
    public static ParserState get(String stageName){
        return lookup.getOrDefault(stageName,ParserState.TEXT);
    }
    ParserState(String stageName){
        this.stageName = stageName;
    }
}
