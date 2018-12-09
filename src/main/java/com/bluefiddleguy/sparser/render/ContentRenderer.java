package com.bluefiddleguy.sparser.render;

import com.bluefiddleguy.sparser.content.ContentItem;
import javafx.util.Pair;

import java.io.IOException;
import java.util.stream.Stream;

public interface ContentRenderer {
    void writeStages(TemplateTokenGenerator template, ContentItem info) ;

    void writeStage(Pair<ParserState,String> template, ContentItem info) throws  IOException;

}
