package com.geaviation.techpubs.data.html.filter;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLString;
import org.cyberneko.html.HTMLEntities;

public class HTMLWriterFilter extends org.cyberneko.html.filters.Writer {

    private boolean processedDoctype = false;

    /**
     * Constructs a writer filter that prints to standard out.
     */
    public HTMLWriterFilter() {
        super();
    }

    /**
     * Constructs a writer filter using the specified output stream and encoding.
     *
     * @param outputStream The output stream to write to.
     * @param encoding The encoding to be used for the output. The encoding name should be an
     * official IANA encoding name.
     * @throws UnsupportedEncodingException the unsupported encoding exceptions
     */
    public HTMLWriterFilter(OutputStream outputStream, String encoding)
        throws UnsupportedEncodingException {
        super(outputStream, encoding);
    }

    /**
     * Constructs a writer filter using the specified Java writer and encoding.
     *
     * @param writer The Java writer to write to.
     * @param encoding The encoding to be used for the output. The encoding name should be an
     * official IANA encoding name.
     */
    public HTMLWriterFilter(java.io.Writer writer, String encoding) {
        super(writer, encoding);
    }

    @Override
    public void doctypeDecl(String root, String publicId, String systemId, Augmentations augs) {

        if (!processedDoctype) {
            StringBuilder doctypeSB = new StringBuilder("<!DOCTYPE");
            if (root != null) {
                doctypeSB.append(" ").append(root);
            }
            if (publicId != null) {
                doctypeSB.append(" PUBLIC \"").append(publicId).append("\"");
            }
            if (systemId != null) {
                doctypeSB.append(" \"").append(systemId).append("\"");
            }
            doctypeSB.append(">");

            fPrinter.print(doctypeSB.toString());
            fPrinter.flush();

            super.doctypeDecl(root, publicId, systemId, augs);
            processedDoctype = true;
        }

    }

    @Override
    protected void printCharacters(XMLString text, boolean normalize) {
        if (normalize) {
            checkNormalize(text);
        } else {
            for (int i = 0; i < text.length; i++) {
                char c = text.ch[text.offset + i];
                fPrinter.print(c);
            }
        }
        fPrinter.flush();
    }

    private void checkNormalize(XMLString text) {
        for (int i = 0; i < text.length; i++) {
            char c = text.ch[text.offset + i];
            if (c != '\n') {
                String entity = HTMLEntities.get(c);
                if (c != '\'' && entity != null) {
                    printEntity(entity);
                } else {
                    fPrinter.print(c);
                }
            } else {
                fPrinter.println();
            }
        }
    }
}
