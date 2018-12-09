package com.bluefiddleguy.sparser;

import com.bluefiddleguy.sparser.content.BareTableContent;
import com.bluefiddleguy.sparser.content.ContentTable;
import com.bluefiddleguy.sparser.content.ContentTableFactory;
import com.bluefiddleguy.sparser.content.MarkupTag;
import com.bluefiddleguy.sparser.render.HTMLTemplateRenderer;
import com.bluefiddleguy.sparser.render.ParserState;
import com.bluefiddleguy.sparser.render.TemplateConfiguration;
import com.bluefiddleguy.sparser.render.TemplateTokenGenerator;
import javafx.util.Pair;
import org.junit.Test;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TemplateRendererTests {
    class TestTemplateConfiguration implements TemplateConfiguration{

        @Override
        public TemplateTokenGenerator getContentTemplate(MarkupTag tag) {
            return new TemplateTokenGenerator(Stream.of(new Pair<ParserState,String>(ParserState.TEXT,"MAIN TAG "+tag+","),
                    new Pair<ParserState,String>(ParserState.TABLE,"default"),
            new Pair<ParserState,String>(ParserState.TEXT,"END "+tag+"\n")));
        }

        @Override
        public TemplateTokenGenerator getTableTemplate(MarkupTag tag) {
            return new TemplateTokenGenerator(Stream.of(new Pair<ParserState,String>(ParserState.TEXT,"<<TABLE TAG "+tag+","),
                    new Pair<ParserState,String>(ParserState.CAPTION,"default"),
            new Pair<ParserState,String>(ParserState.ROWS,"default"),
            new Pair<ParserState,String>(ParserState.TEXT,"END "+tag+">>\n")));
        }

        @Override
        public TemplateTokenGenerator getRowTemplate(MarkupTag tag) {
            return new TemplateTokenGenerator(Stream.of(new Pair<ParserState,String>(ParserState.TEXT,"<<ROW TAG "+tag+","),
                    new Pair<ParserState,String>(ParserState.CELLS,"default"),
            new Pair<ParserState,String>(ParserState.TEXT,"END "+tag+"\n")));
        }

        @Override
        public TemplateTokenGenerator getHeaderCellTemplate(MarkupTag tag) {
            return new TemplateTokenGenerator(Stream.of(new Pair<ParserState,String>(ParserState.TEXT,"<<TAG "+tag+","),
                    new Pair<ParserState,String>(ParserState.CELL,"default"),
            new Pair<ParserState,String>(ParserState.TEXT,"END "+tag+">>")));
        }

        @Override
        public TemplateTokenGenerator getCellTemplate(MarkupTag tag) {
            return new TemplateTokenGenerator(Stream.of(new Pair<ParserState,String>(ParserState.TEXT,"<<TAG "+tag),
                    new Pair<ParserState,String>(ParserState.CELL,"default"),
                    new Pair<ParserState,String>(ParserState.TEXT,"END "+tag+">>")));
        }
    }
    @Test
    public void testAwake(){
        assert(true);
    }

    public ContentTable sampleTable(){
        List<String> nheaderList = new ArrayList<String>();
        nheaderList.add("Col A");
        nheaderList.add("Col B");
        nheaderList.add("Col C");
        List<String> row1 = Arrays.asList("a1 b1 c1".split(" "));
        List<String> row2 = Arrays.asList("a2 b2 c2 x2 y2 z2".split(" "));
        List<String> row3 = Arrays.asList("a3 b3".split(" "));
        List<List<String>> content = Stream.of(row1,row2,row3).collect(Collectors.toList());
        ContentTable table = new ContentTableFactory().newTable("test",nheaderList,content);
        return table;
    }
    @Test
    public void testTable(){
        ContentTable table = this.sampleTable();
        assert(table.getNRows() == 3);
        assert(table.getNCols() == 6);
        assert(table.getCaption()=="test");
        assert(table.getHeader(2)=="Col C");
        assert(table.getCell(1,5).equals("z2"));
        assert(table.getHeader(10).equals(""));
        assert(table.getCell(2,4).equals(""));
    }

    @Test
    public void testCellRendering(){
        List<Pair<ParserState,String>> bonehead = Stream.of(
                new Pair<ParserState,String>(ParserState.TEXT,"sample")
        ).collect(Collectors.toList());

        Iterator<Pair<ParserState,String>> parseStream = bonehead.iterator();
        boolean executed = false;

        for (Pair<ParserState,String> event: bonehead){
            assert(event.getKey().equals(ParserState.TEXT));
            assert(event.getValue().length()>0);
            executed = true;
        }
        assert(executed);


    }

    @Test
    public void testTableTemplating(){
        // test template manager rendering.
        TemplateConfiguration tconfig = new TestTemplateConfiguration();
        BareTableContent table = new BareTableContent(this.sampleTable());
        StringWriter out = new StringWriter();
        HTMLTemplateRenderer outManager = new HTMLTemplateRenderer(out,tconfig);
        outManager.writeStage(new Pair(ParserState.TABLE,""),table);
        System.out.println(out.toString());


    }
}
