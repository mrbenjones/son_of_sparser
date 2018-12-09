package com.bluefiddleguy.sparser.render;

import com.bluefiddleguy.sparser.content.MarkupTag;
import javafx.util.Pair;
import org.yaml.snakeyaml.error.Mark;

import java.util.stream.Stream;

public interface TemplateConfiguration {
    TemplateTokenGenerator getContentTemplate(MarkupTag tag);
    TemplateTokenGenerator getTableTemplate(MarkupTag tag);
    TemplateTokenGenerator getRowTemplate(MarkupTag tag);
    TemplateTokenGenerator getHeaderCellTemplate(MarkupTag tag);
    TemplateTokenGenerator getCellTemplate(MarkupTag tag);
}
