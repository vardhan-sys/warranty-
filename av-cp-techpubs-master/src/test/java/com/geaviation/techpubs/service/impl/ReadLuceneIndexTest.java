package com.geaviation.techpubs.service.impl;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.util.List;

public class ReadLuceneIndexTest {

    private static String INDEX_PATH = "/data4/techpubs/gek112060/1.2/targetindex";

    public static void main(String[] args) throws Exception {

        // directory where your index is stored
        File path = new File(INDEX_PATH);

        Directory index = FSDirectory.open(path);
        IndexReader reader = IndexReader.open(index);
        int numDocs = reader.numDocs();
        System.out.println(numDocs);

//        org.dom4j.Document xml = DocumentHelper.createDocument();

        for (int i = 0; i < numDocs; i++) {
            if (!reader.isDeleted(i)) {
                Document document = reader.document(i);
//                if (!document.getFieldable("docnbr").stringValue().equals("sbs")) {
//                    System.out.println(document);
                    List<Fieldable> fieldables = document.getFields();
                    printFields(fieldables);
//                }
            }
        }
    }

    private static void printFields(List<Fieldable> fields) {
        for (Fieldable field : fields) {
            System.out.println("Field Name: " + field.name() + " | Field Value: " + field.stringValue());
        }

        System.out.println();
    }
}
