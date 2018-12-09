package com.bluefiddleguy.sparser.render;

import javafx.util.Pair;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TemplateTokenGenerator {
    private List<Pair<ParserState,String>> tokenList;
    public TemplateTokenGenerator(Stream<Pair<ParserState,String>> tokens){
        this.tokenList = tokens.collect(Collectors.toList());
    }

    public Stream<Pair<ParserState,String>> tokens(){
        return this.tokenList.stream();
    }
}
