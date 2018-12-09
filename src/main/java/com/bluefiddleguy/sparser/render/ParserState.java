package com.bluefiddleguy.sparser.render;

public enum ParserState {
    TEXT,
    TAG,
    CHILDREN,
    REFERENCE,
    ROWS,
    CELLS,
    CELL,
    CAPTION,
    COUNTER,
    INCREMENT_COUNTER,
    CREATE_COUNTER,
    TABLE,
    HEADER_CELLS,
    ATTRIBUTE
}
