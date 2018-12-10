package com.bluefiddleguy.sparser.render;

import javafx.util.Pair;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

public class SparserConfigurationFactory {

    /**
     * Utility class to make the input map for building a {@link SparserTokenGeneratorFactory}
     * from a JSON file.
     * @param jsonFile
     * @return
     */
    private Map<Pair<String, String>, String> mapFromJson(InputStream jsonFile) {
        String content = "";
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(jsonFile))) {
            content = buffer.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {

        }
        Map<Pair<String, String>, String> configMap = new HashMap<Pair<String, String>, String>();

        try {
            JSONObject configObject = new JSONObject(content);
            // the initial keys are the tag types.
            Iterator tags = configObject.keys();
            while (tags.hasNext()) {
                String tag = tags.next().toString();
                JSONObject stageObject = (JSONObject) (configObject.get(tag));
                Iterator stages = stageObject.keys();
                while (stages.hasNext()) {
                    String stage = stages.next().toString();
                    Pair key = new Pair(tag, stage);
                    // the value of each of these keys are maps from parser state to a file.
                    configMap.put(key,
                            stageObject.get(stage).toString());
                }
            }
            return configMap;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<Pair<String, String>, String>();
        }
    }

    /**
     * Use the json reader to make the map specifying the configuration, and then run the constructor.
     * @param jsonStream
     * @return
     */
    public TemplateConfiguration fromJson(InputStream jsonStream){
        return new FileBasedTemplateConfiguration(this.mapFromJson(jsonStream));
    }

}
