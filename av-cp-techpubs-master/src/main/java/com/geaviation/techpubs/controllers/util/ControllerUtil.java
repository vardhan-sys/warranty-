package com.geaviation.techpubs.controllers.util;

import org.apache.commons.io.FilenameUtils;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class ControllerUtil {

    /**
     * getResourceContentType service returns the content type of the resource selected by the user.
     * This response returns the resource content type.
     *
     * @param res the resource name
     * @return String the resource content type
     */
    public static String getResourceContentType(String res) {
        String contentType = URLConnection.guessContentTypeFromName(res);
        if (contentType != null) {
            return contentType;
        }
        String ext = FilenameUtils.getExtension(res).toLowerCase();

        Map<String, String> dataFormat = new HashMap<>();
        dataFormat.put("svg", "image/svg+xml");
        dataFormat.put("css", "text/css");
        dataFormat.put("js", "text/javascript");
        dataFormat.put("wrl", "model/vrml");
        dataFormat.put("cgm", "image/cgm");
        dataFormat.put("csv", "text/csv");
        dataFormat.put("ppt", "application/vnd.ms-powerpoint");
        dataFormat.put("doc", "application/msword");
        dataFormat.put("xls", "application/vnd.ms-excel");
        dataFormat.put("wmv", "video/x-ms-wmv");
        dataFormat.put("avi", "video/avi");
        dataFormat.put("mp2", Constants.VIDEO_MPEG);
        dataFormat.put("mp3", Constants.VIDEO_MPEG);
        dataFormat.put("mp4", Constants.VIDEO_MP4);
        dataFormat.put("m4v", Constants.VIDEO_MP4);
        dataFormat.put("m4a", Constants.VIDEO_MP4);
        dataFormat.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        dataFormat.put("pptx",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        dataFormat
                .put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        dataFormat.put("wasm", "application/wasm");
        String dataReturnFormat = dataFormat.get(ext);

        if (null != dataReturnFormat) {
            return dataReturnFormat;
        }

        return "application/octet-stream";
    }

    public static MediaType getSpringResourceContentType(String res) {
        return MediaType.valueOf(getResourceContentType(res));
    }

    /**
     * Get basic cache control
     *
     * @return Cache Control
     */
    public static CacheControl getSpringCacheControl() {
        return CacheControl.maxAge(3600, TimeUnit.SECONDS);
    }

    /**
     * Get the remaining path from a request
     *
     * @param request Http request object containing path
     * @return End of path url represented by "**" in path value
     */
    public static String getRemainingPath(HttpServletRequest request) {
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);

        return new AntPathMatcher().extractPathWithinPattern(bestMatchPattern, path);
    }
}
