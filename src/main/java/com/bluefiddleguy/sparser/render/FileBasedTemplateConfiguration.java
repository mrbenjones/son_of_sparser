package com.bluefiddleguy.sparser.render;

import com.bluefiddleguy.sparser.content.MarkupTag;
import javafx.util.Pair;

import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FileBasedTemplateConfiguration implements TemplateConfiguration {

    private final Map<Pair<MarkupTag,ParserState>,String> filePointer = new
            HashMap<Pair<MarkupTag,ParserState>,String>();

    private TokenGeneratorFactory templateReader;

    public static TemplateTokenGenerator errorTemplate(MarkupTag tagTried,ParserState stage){
        List<Pair<ParserState,String>>  response = new ArrayList();
        response.add(new Pair(ParserState.TEXT,"Template "+(tagTried.getConfig())+" : "+
                stage.getStageName()));
        return new TemplateTokenGenerator(response);
    }

    public FileBasedTemplateConfiguration(Map<Pair<String,String>,String> inputMap){
        for (Pair<String,String> keyPair : inputMap.keySet()){
            Pair<MarkupTag,ParserState> enumKey = new Pair<MarkupTag,ParserState>(
                    MarkupTag.get(keyPair.getKey()),
                    ParserState.get(keyPair.getValue())
            );
            this.filePointer.put(enumKey,inputMap.get(keyPair));
        }
         this.templateReader = new SparserTokenGeneratorFactory();

    }

    private InputStream fileStreamFromKey(Pair<MarkupTag,ParserState> key){
        return this.getClass().getResourceAsStream(this.filePointer.get(key));
    }

    @Override
    public TemplateTokenGenerator getContentTemplate(MarkupTag tag) {
        Pair<MarkupTag,ParserState> key = new Pair<MarkupTag,ParserState>(
                tag,ParserState.TEXT
        );
        return  this.templateReader.fromTemplateFile(this.fileStreamFromKey(key));
    }

    @Override
    public TemplateTokenGenerator getTableTemplate(MarkupTag tag) {
        Pair<MarkupTag,ParserState> key = new Pair<MarkupTag,ParserState>(
                tag,ParserState.TABLE
        );
        return  this.templateReader.fromTemplateFile(this.fileStreamFromKey(key));
    }

    @Override
    public TemplateTokenGenerator getRowTemplate(MarkupTag tag) {
        Pair<MarkupTag,ParserState> key = new Pair<MarkupTag,ParserState>(
                tag,ParserState.ROWS
        );
        return  this.templateReader.fromTemplateFile(this.fileStreamFromKey(key));
    }

    @Override
    public TemplateTokenGenerator getHeaderCellTemplate(MarkupTag tag) {
        Pair<MarkupTag,ParserState> key = new Pair<MarkupTag,ParserState>(
                tag,ParserState.HEADER_CELLS
        );
        return  this.templateReader.fromTemplateFile(this.fileStreamFromKey(key));
    }

    @Override
    public TemplateTokenGenerator getCellTemplate(MarkupTag tag) {
        Pair<MarkupTag,ParserState> key = new Pair<MarkupTag,ParserState>(
                tag,ParserState.CELLS
        );
        return  this.templateReader.fromTemplateFile(this.fileStreamFromKey(key));
    }
}
