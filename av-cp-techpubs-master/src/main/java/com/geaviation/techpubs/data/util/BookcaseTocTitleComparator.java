package com.geaviation.techpubs.data.util;

import com.geaviation.techpubs.models.BookcaseTocDAO;
import java.util.Comparator;
import org.apache.commons.lang3.StringUtils;

public class BookcaseTocTitleComparator implements Comparator<BookcaseTocDAO> {
  public int compare(BookcaseTocDAO bookcaseTocDAO1, BookcaseTocDAO bookcaseTocDAO2) {
    return StringUtils.compareIgnoreCase(bookcaseTocDAO1.getTitle(), bookcaseTocDAO2.getTitle());
  }
}
