package com.bluefiddleguy.sparser.render;

import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SparserTokenGeneratorFactory implements  TokenGeneratorFactory{

    Pattern tagPattern = Pattern.compile("(.*?)\\{\\{(.*?)\\}\\}",Pattern.MULTILINE|Pattern.DOTALL);
    Pattern counterPattern = Pattern.compile("(\\W)(\\w+)",Pattern.DOTALL|Pattern.MULTILINE);
    Pattern renderPattern = Pattern.compile("render\\.(\\w+)\\.?(\\w*)",Pattern.DOTALL|Pattern.MULTILINE);
    Pattern endText = Pattern.compile("[^\\}]*?$",Pattern.DOTALL|Pattern.MULTILINE);
    /**
     * Buidling a render pair object (not just an attribute reference.
     * @param tagInit
     * @param spec
     * @param content
     * @return
     */
    private Pair<ParserState,String>  buildPair(String tagInit,String spec,String content){
        ParserState tagState = ParserState.TEXT;
        return new Pair<ParserState,String>(tagState,tagInit+"::"+content);
    };

    /**
     * For plain attribute references
     * @param attribute
     * @return
     */
    private Pair<ParserState,String> attributePair(String attribute){
        return new Pair<ParserState,String>(ParserState.ATTRIBUTE,attribute);
    }

    /**
     * For plaintext objects.
     * @param content
     * @return
     */
    private Pair<ParserState,String> buildTextPair(String content){
        return new Pair<ParserState,String>(ParserState.TEXT,content);
    };

    private List<Pair<ParserState,String>> generateTokens(String templateText){

        List<Pair<ParserState,String>> stageStream = new ArrayList<Pair<ParserState,String>>();
        Matcher tags = this.tagPattern.matcher(templateText);
        int matchEnd = 0;
        while (tags.find()){
            System.out.println(tags.group(0));
            String preMatch = tags.group(1);
            String tagContent = tags.group(2);
            if (preMatch.length()>0){
                stageStream.add(this.buildTextPair(preMatch));
            }
            // add any tags.
            Matcher render = this.renderPattern.matcher(tagContent);
            Matcher counter = this.counterPattern.matcher(tagContent);
            if (render.matches()){
                stageStream.add(this.buildPair(render.group(1),render.group(2),render.group(3)));
            }
            else if (counter.matches()){
                stageStream.add(this.buildPair(render.group(1),render.group(2),""));
            }
            else{
                stageStream.add(this.attributePair(tagContent));
            }
            matchEnd = tags.end(0);
        }
        // get any tailing stream.

    try{
        stageStream.add(
                this.buildTextPair(templateText.substring(matchEnd))
        );
    }
    catch (Exception e){
        System.out.println(templateText);
    }
        return stageStream;

    }
    @Override
    public TemplateTokenGenerator fromTemplateFile(InputStream templateSource){
        String content = "";
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(templateSource))) {
            content = buffer.lines().collect(Collectors.joining("\n"));
        }
        catch (IOException e){

        }
        return new TemplateTokenGenerator(this.generateTokens(content));
    }
}
