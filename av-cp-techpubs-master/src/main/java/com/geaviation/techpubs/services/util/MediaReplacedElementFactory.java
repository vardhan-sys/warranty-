package com.geaviation.techpubs.services.util;

import com.lowagie.text.Image;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import javax.imageio.ImageIO;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Element;
import org.xhtmlrenderer.extend.FSImage;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.extend.ReplacedElementFactory;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.pdf.ITextFSImage;
import org.xhtmlrenderer.pdf.ITextImageElement;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.FormSubmissionListener;

public class MediaReplacedElementFactory implements ReplacedElementFactory {

    private static final Logger log = LogManager.getLogger(MediaReplacedElementFactory.class);
    private final ReplacedElementFactory superFactory;
    private String strSSO;
    private String strPortalId;
    private static final String IMAGE_UNAVAILABLE = "Image Not Available";

    public MediaReplacedElementFactory(ReplacedElementFactory superFactory, String strSSO,
        String strPortalId) {
        this.superFactory = superFactory;
        this.strSSO = strSSO;
        this.strPortalId = strPortalId;
    }

    public String getStrSSO() {
        return strSSO;
    }

    public void setStrSSO(String strSSO) {
        this.strSSO = strSSO;
    }

    public String getStrPortalId() {
        return strPortalId;
    }

    public void setStrPortalId(String strPortalId) {
        this.strPortalId = strPortalId;
    }

    @Override
    public ReplacedElement createReplacedElement(LayoutContext layoutContext, BlockBox blockBox,
        UserAgentCallback userAgentCallback, int cssWidth, int cssHeight) {
        Element element = blockBox.getElement();
        if (element == null) {
            return null;
        }
        String nodeName = element.getNodeName();
        if ("img".equals(nodeName)) {
            if (!element.hasAttribute("src")) {
                throw new NullPointerException(
                    "An element with class `media` is missing a `data-src` attribute indicating the media file.");
            }
            String input = null;
            FSImage fsImage = null;
            try {
                input = element.getAttribute("src");
                final byte[] bytes = imageReader(input, strSSO, strPortalId);
                final Image image = Image.getInstance(bytes);
                fsImage = new ITextFSImage(image);
            } catch (Exception e) {
                fsImage = null;
                log.error(e);
            }
            if (fsImage != null) {
                if ((cssWidth != -1) || (cssHeight != -1)) {
                    fsImage.scale(cssWidth, cssHeight);
                }
                return new ITextImageElement(fsImage);
            }
        }
        return null;
    }

    @Override
    public void remove(Element element) {
        this.superFactory.remove(element);
    }

    @Override
    public void reset() {
        this.superFactory.reset();
    }

    @Override
    public void setFormSubmissionListener(FormSubmissionListener listener) {
        this.superFactory.setFormSubmissionListener(listener);
    }

    private BufferedImage convertSVGToPNG(InputStream svgInputStream) {
        File tempPNG = new File("svg.png");
        BufferedImage png = null;
        try (
            // Step-2: Define OutputStream to PNG Image and attach to TranscoderOutput
            OutputStream pngOstream = new FileOutputStream("svg.png");
        ) {
            // Step -1: We read the input SVG document into Transcoder Input
            TranscoderInput inputSvgImage = new TranscoderInput(svgInputStream);
            TranscoderOutput outputPngImage = new TranscoderOutput(pngOstream);
            // Step-3: Create PNGTranscoder and define hints if required
            PNGTranscoder transcoder = new PNGTranscoder();
            // Step-4: Convert and Write output
            transcoder.transcode(inputSvgImage, outputPngImage);
            // Read image
            png = ImageIO.read(tempPNG);
        } catch (IOException | TranscoderException ex) {
            log.error(ex);
        } finally {
            if (tempPNG.delete()) {
                log.info("Successfully deleted temp PNG file");
            } else {
                log.error("Failed to delete temp PNG file");
            }
        }
        return png;
    }

    byte[] imageReader(String url, String sso, String portalId) {
        byte[] imageInByte = null;
        BufferedImage originalImage = null;
        try {
            URL imageURL = new URL(url);
            URLConnection urlCon = imageURL.openConnection();
            urlCon.addRequestProperty("sm_ssoid", sso);
            urlCon.addRequestProperty("portal_id", portalId);
            InputStream response = urlCon.getInputStream();

            // If it's a vector image
            if (url.contains(".svg")) {
                originalImage = convertSVGToPNG(response);
            } else {
                originalImage = ImageIO.read(response);
            }

        } catch (Exception ex) {
            log.error(
                "There was a problem in reading embedded graphic, Replacing with Image Unavailable.");
            log.error(ex);
            if (null == originalImage) {
                Font font = new Font("Arial", Font.PLAIN, 11);
                originalImage = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = originalImage.createGraphics();

                g2d.setColor(Color.BLACK);
                g2d.setFont(font);
                g2d.drawString(IMAGE_UNAVAILABLE, 215, 245);
                g2d.dispose();
            }
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(originalImage, "png", baos);
            baos.flush();
            imageInByte = baos.toByteArray();
            baos.close();
        } catch (IOException e) {
            log.error(e);
        }
        return imageInByte;
    }

}