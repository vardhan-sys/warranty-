package com.geaviation.techpubs.data.util;

import com.geaviation.techpubs.models.BookcaseTocDAO;
import java.util.Comparator;

//comparator to sort by the order field descending then type with manual type coming before ICs or TR
public class BookcaseTocDaoNodeOrderComparator implements Comparator<BookcaseTocDAO> {

  public int compare(BookcaseTocDAO bookcaseTOCDAO1, BookcaseTocDAO bookcaseTOCDAO2) {
    if (bookcaseTOCDAO1.getOrder() < bookcaseTOCDAO2.getOrder())
      return -1;
    if (bookcaseTOCDAO1.getOrder() > bookcaseTOCDAO2.getOrder())
      return 1;
    //This happens when a manual pageblk has an ic or tr
    if (bookcaseTOCDAO1.getOrder() == bookcaseTOCDAO2.getOrder() && bookcaseTOCDAO1.getKey() != null
            && bookcaseTOCDAO1.getKey().equals(bookcaseTOCDAO2.getKey())) {
      String nodeType = bookcaseTOCDAO1.getType() != null ? bookcaseTOCDAO1.getType() : "";
      String object2NodeType = bookcaseTOCDAO2.getType() != null ? bookcaseTOCDAO2.getType() : "";

      //manual pageblks should be ordered before their IC or TR
      if (nodeType.equals(DataConstants.MANUAL_PAGEBLK_TYPE) && (object2NodeType.equals(DataConstants.IC_PAGEBLK_TYPE)
              || object2NodeType.equals(DataConstants.TR_PAGEBLK_TYPE)))
        return -1;
      //manual pageblks should be ordered before their IC or TR
      if ((nodeType.equals(DataConstants.IC_PAGEBLK_TYPE) || nodeType.equals(DataConstants.TR_PAGEBLK_TYPE))
          && object2NodeType.equals(DataConstants.MANUAL_PAGEBLK_TYPE))
        return 1;
    }
    return 0;
  }
}
