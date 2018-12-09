package com.bluefiddleguy.sparser.render;

import com.bluefiddleguy.sparser.content.ContentItem;
import com.bluefiddleguy.sparser.content.ContentTable;
import javafx.util.Pair;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Given {@link Pair<ParserState,String>} objects, and ContentObjects - {@link ContentTable} or
 * {@link ContentItem} objects ,  with keys, captions, and values - writes the relevant data
 * from the Templates to a {@link Writer}
 */
public class HTMLTemplateRenderer implements ContentRenderer {

    private Writer output;
    private TemplateConfiguration TemplateConfig;
    private Map<ParserState, StageHandler> handler;
    private Map<String, Integer> counters;
    static StringWriter logger = new StringWriter();

    public HTMLTemplateRenderer(Writer output, TemplateConfiguration TemplateConfig) {
        this.output = output;
        this.TemplateConfig = TemplateConfig;
        this.handler = new HashMap<ParserState, StageHandler>();
        this.counters = new HashMap<String, Integer>();
        this.loadHandlers();
    }

    private void loadHandlers() {
        this.handler.put(ParserState.TEXT, new TextHandler());
        this.handler.put(ParserState.TABLE, new TableHandler());
        this.handler.put(ParserState.CHILDREN, new ChildHandler());
        this.handler.put(ParserState.REFERENCE, new ReferenceHandler());
        CounterHandler counter = new CounterHandler();
        this.handler.put(ParserState.COUNTER, counter);
        this.handler.put(ParserState.CREATE_COUNTER, counter);
        this.handler.put(ParserState.INCREMENT_COUNTER, counter);


    }

    @Override
    public void writeStages(TemplateTokenGenerator Template, ContentItem info)  {
        try {
            this.output.write(info.getContent());
        }
        catch (IOException e){
            logger.write(e.getMessage());
        }
        Template.tokens().forEach(
                s -> {
                    this.writeStage(s, info);
                }
        );


    }

    @Override
    public void writeStage(Pair<ParserState, String> Template, ContentItem info)  {
        this.handler.get(Template.getKey()).writeStage(Template, info);

    }


    interface StageHandler {
        void writeStage(Pair<ParserState, String> stage, ContentItem info) ;
    }

    class TextHandler implements StageHandler {

        @Override
        public void writeStage(Pair<ParserState, String> stage, ContentItem info)  {
            try {
                HTMLTemplateRenderer.this.output.write(stage.getValue());
            }
            catch (IOException e){
                logger.write(e.getMessage());
            }
        }
    }

    class TableHandler implements StageHandler {

        /**
         * No options for table at present.  pick up table Template, write items.
         * Only exception is the ROWS tag, which will write the rows in order.
         *
         * @param stage
         * @param info
         * @
         */
        @Override
        public void writeStage(Pair<ParserState, String> stage, ContentItem info) {
            TemplateTokenGenerator Template = HTMLTemplateRenderer.this
                    .TemplateConfig.getTableTemplate(info.getTag());

            // A table Template has three special tags -- ROWS, HEADER_ROWS, and CAPTION.
            // everything else is rendered normally.

            ContentTable table = info.getTable();
            TemplateConfiguration config = HTMLTemplateRenderer.this.TemplateConfig;
                Template.tokens().forEach(
                        s -> {
                            try {

                                if (s.getKey().equals(ParserState.ROWS)) {
                                    this.writeRows(TemplateConfig.getRowTemplate(info.getTag()), info);
                                } else if (s.getKey().equals(ParserState.CAPTION)) {
                                    HTMLTemplateRenderer.this.output.write(table.getCaption());
                                } else if (s.getKey().equals(ParserState.HEADER_CELLS)) {
                                    this.writeHeaderCells(TemplateConfig.getHeaderCellTemplate(info.getTag()), info);
                                } else {
                                    HTMLTemplateRenderer.this.writeStage(s, info);
                                }
                            }
                            catch (IOException e) {
                                logger.write(e.getMessage());
                            }


                        }


                );


        }

        /**
         * Write the cells of the table at the row Numberto the writer.
         *
         * @param stage
         * @param info
         * @param rowNumber
         */
        public void writeCells(Pair<ParserState, String> stage, ContentItem info, final int rowNumber)  {
            ContentTable table = info.getTable();
            TemplateTokenGenerator cellTemplate = cellTemplate = HTMLTemplateRenderer.this
                    .TemplateConfig.getCellTemplate(info.getTag());

            for (int col = 0; col < table.getNCols(); col++) {
                final int colidx = col;
                    cellTemplate.tokens().forEach(
                            s -> {

                                if (s.getKey().equals(ParserState.CELL)) {
                                    try {
                                        HTMLTemplateRenderer.this.output.write(table.getCell(rowNumber, colidx));
                                    }
                                    catch (IOException e){
                                        logger.write(e.getMessage());
                                    }
                                } else {
                                    HTMLTemplateRenderer.this.writeStage(s, info);
                                }
                            }

                    );

            }
        }


        /**
         * Write the cells from the header Template to the output writer.
         *
         * @param headerCellTemplate
         * @param info
         */
        public void writeHeaderCells(TemplateTokenGenerator headerCellTemplate, ContentItem info)
                 {
            ContentTable table = info.getTable();
            // walk through header cells and write them in.
            for (int col = 0; col < table.getNCols(); col++) {
                final int colidx = col;

                    headerCellTemplate.tokens().forEach(s -> {
                        if (s.getKey().equals(ParserState.CELL)) {
                            try {
                                HTMLTemplateRenderer.this.output.write(table.getHeader(colidx));
                            }
                            catch (IOException e){
                                logger.write(e.getMessage());
                            }
                        } else {
                            HTMLTemplateRenderer.this.writeStage(s, info);
                        }
                    });

            }
        }

        /**
         * @param rowTemplate
         * @param info
         */
        public void writeRows(TemplateTokenGenerator rowTemplate, ContentItem info)
                 {
            ContentTable table = info.getTable();
            for (int i = 0; i < table.getNRows(); i++) {
                // write the content normally for every type except CELLS
                final int idx = i;
                rowTemplate.tokens().forEach(
                        s -> {
                            if (s.getKey().equals(ParserState.CELLS)) {
                                this.writeCells(s, info, idx);
                            } else {
                                HTMLTemplateRenderer.this.writeStage(s, info);
                            }
                        }
                );

            }
        }
    }

    class ChildHandler implements StageHandler {

        @Override
        public void writeStage(Pair<ParserState, String> stage, ContentItem info)  {
            for (ContentItem child : info.getChildren()) {
                TemplateTokenGenerator Template = HTMLTemplateRenderer.this
                        .TemplateConfig.getContentTemplate(child.getTag());
                HTMLTemplateRenderer.this.writeStages(Template, info);
            }

        }
    }

    class ReferenceHandler implements StageHandler {

        @Override
        public void writeStage(Pair<ParserState, String> stage, ContentItem info)  {
            TemplateTokenGenerator referenceTemplate = HTMLTemplateRenderer.this
                    .TemplateConfig.getContentTemplate(info.getTag());
            HTMLTemplateRenderer.this.writeStages(referenceTemplate, info);
        }
    }

    class CounterHandler implements StageHandler {

        @Override
        public void writeStage(Pair<ParserState, String> stage, ContentItem info)  {
            int counter = HTMLTemplateRenderer.this.counters.getOrDefault(stage.getValue(), 0);

            if (stage.getKey().equals(ParserState.INCREMENT_COUNTER)) {
                HTMLTemplateRenderer.this.counters.put(stage.getValue(), counter + 1);
            }
            try {
                HTMLTemplateRenderer.this.output.write(Integer.toString(counter));
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }


}
