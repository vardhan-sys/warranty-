package com.geaviation.techpubs.models.techlib.dto;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Data transfer object for hierarchy of bookcaseKey -> List of page blks owned by bookcase
 */
public class SMMDocsDto {
    Map<String, List<PageBlkDto>> bookcaseSMMDocs = new LinkedHashMap<>();

    public void addBookcaseSMMDocs(String bookcase, List<PageBlkDto> pageBlkDtos) {
        bookcaseSMMDocs.put(bookcase, pageBlkDtos);
    }

    public Map<String, List<PageBlkDto>> getBookcaseSMMDocs() {
        return bookcaseSMMDocs;
    }
}
