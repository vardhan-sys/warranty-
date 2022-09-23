package com.geaviation.techpubs.data.util;

import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

public class SecurityEscape {
    public static String cleanString(String input) {
        if (input != null && !input.isEmpty()){
            return Jsoup.clean(StringEscapeUtils.escapeHtml(StringEscapeUtils.escapeJavaScript(StringEscapeUtils.escapeSql(input)))
                    , "", Whitelist.basic(), new Document.OutputSettings().prettyPrint(false));
        }
        return input;
    }

    public static Map<String,String> cleanMap(Map<String, String> input ) {
        if (input != null) {
            for (Map.Entry<String, String> entry : input.entrySet()) {
                if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                    entry.setValue(cleanString(entry.getValue()));
                }
            }
        }
        return input;
    }

    public static void unescapeFileContent(File file) throws IOException {
        // Get all lines from file
        List<String> lines = Files.readAllLines(file.toPath());

        // Escape each line, and overwrite the escaped line back to file
        try (Writer fileWriter = new FileWriter(file, false)) {
            for (String line : lines) {
                fileWriter.write(StringEscapeUtils.unescapeJava(line) + "\n");
            }
        }
    }
} 