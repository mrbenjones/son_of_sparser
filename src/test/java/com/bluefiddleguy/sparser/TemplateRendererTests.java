package com.bluefiddleguy.sparser;

import com.bluefiddleguy.sparser.content.BareTableContent;
import com.bluefiddleguy.sparser.content.ContentTable;
import com.bluefiddleguy.sparser.content.ContentTableFactory;
import com.bluefiddleguy.sparser.content.MarkupTag;
import com.bluefiddleguy.sparser.render.*;
import javafx.util.Pair;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
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
            return new TemplateTokenGenerator(Stream.of(new Pair<ParserState,String>(ParserState.TEXT,"MAIN TAG >\n"),
                    new Pair<ParserState,String>(ParserState.TABLE,"default"),
            new Pair<ParserState,String>(ParserState.TEXT,"END "+tag+"\n")));
        }

        @Override
        public TemplateTokenGenerator getTableTemplate(MarkupTag tag) {
            return new TemplateTokenGenerator(Stream.of(new Pair<ParserState,String>(ParserState.TEXT,"\n<<TABLE TAG>\n"),
                    new Pair<ParserState,String>(ParserState.CAPTION,"default"),
            new Pair<ParserState,String>(ParserState.ROWS,"default"),
            new Pair<ParserState,String>(ParserState.TEXT,"END "+tag+">>\n")));
        }

        @Override
        public TemplateTokenGenerator getRowTemplate(MarkupTag tag) {
            return new TemplateTokenGenerator(Stream.of(new Pair<ParserState,String>(ParserState.TEXT,"\n<<ROW TAG >\n"),
                    new Pair<ParserState,String>(ParserState.CELLS,"default"),
            new Pair<ParserState,String>(ParserState.TEXT,"END "+tag+"\n")));
        }

        @Override
        public TemplateTokenGenerator getHeaderCellTemplate(MarkupTag tag) {
            return new TemplateTokenGenerator(Stream.of(new Pair<ParserState,String>(ParserState.TEXT,"\n<<TAG >\n"),
                    new Pair<ParserState,String>(ParserState.CELL,"default"),
            new Pair<ParserState,String>(ParserState.TEXT,"END "+tag+">>")));
        }

        @Override
        public TemplateTokenGenerator getCellTemplate(MarkupTag tag) {
            return new TemplateTokenGenerator(Stream.of(new Pair<ParserState,String>(ParserState.TEXT,"\n<<TAG a=\""),
                    new Pair<ParserState,String>(ParserState.CELL,"default"),
                    new Pair<ParserState,String>(ParserState.TEXT,"\" "+tag+">>\n")));
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
        ContentTable table = new ContentTableFactory().newTable("\nTABLE CAPTION\n",nheaderList,content);
        return table;
    }
    @Test
    public void testTable(){
        ContentTable table = this.sampleTable();
        assert(table.getNRows() == 3);
        assert(table.getNCols() == 6);
        assert(table.getCaption()=="\nTABLE CAPTION\n");
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
        List<Pair<ParserState,String>> multiple = Stream.of(new Pair<ParserState,String>(ParserState.TEXT,"\n\n\nStart"),
                new Pair<ParserState,String>(ParserState.TABLE,"Example")).collect(Collectors.toList());
        TemplateTokenGenerator ttg = new TemplateTokenGenerator(multiple);
        outManager.writeStages(ttg,table);
        System.out.println(out.toString());

    }

    private TemplateTokenGenerator ttgFromString(String templateString){
        return new SparserTokenGeneratorFactory().fromTemplateFile(
                new ByteArrayInputStream(templateString.getBytes()));
    }
    @Test
    public void testTemplateGeneration() {
        InputStream testString = new ByteArrayInputStream("Header {{tag1}} and {{tag2}} and {{tag3}} ".getBytes());
        TemplateTokenGenerator ttg = new SparserTokenGeneratorFactory().fromTemplateFile(testString);
        assert(ttg.tokens().collect(Collectors.toList()).size()==7);
        TemplateTokenGenerator counterTag = this.ttgFromString("{{++monkey}}{{=monkey}}{{render.table}}");
        List<Pair<ParserState,String>> counters = counterTag.tokens()
                .collect(Collectors.toList());
        assert(counters.get(0).getKey().equals(ParserState.INCREMENT_COUNTER));
        assert(counters.get(0).getValue().equalsIgnoreCase("monkey"));
        assert(counters.get(1).getKey().equals(ParserState.COUNTER));
        assert(counters.get(2).getKey().equals(ParserState.TABLE));
        TemplateTokenGenerator referenceTag = this.ttgFromString("<b>{{render.reference.one}}");
        List<Pair<ParserState,String>> refs = referenceTag.tokens()
                .collect(Collectors.toList());
        assert(refs.get(0).getKey()==ParserState.TEXT);
        assert(refs.get(0).getValue().equalsIgnoreCase("<b>"));
        assert(refs.get(1).getKey().equals(ParserState.REFERENCE));
        assert(refs.get(1).getValue().equalsIgnoreCase("one"));






    }
}
