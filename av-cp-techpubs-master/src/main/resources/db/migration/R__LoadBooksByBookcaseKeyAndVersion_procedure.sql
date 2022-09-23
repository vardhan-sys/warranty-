CREATE OR REPLACE FUNCTION loadbooksbybookcasekeyandversion(bookcasekey CHARACTER VARYING, bookcaseversion CHARACTER VARYING)
    RETURNS TABLE
            (
                book_key           CHARACTER VARYING,
                book_title         CHARACTER VARYING,
                book_revision_date CHARACTER VARYING,
                book_revision_num  CHARACTER VARYING,
                bookcase_key       CHARACTER VARYING,
                bookcase_title     CHARACTER VARYING
            )
    LANGUAGE SQL
AS
$$
SELECT book.book_key              AS book_key,
       book_version.title         AS book_title,
       book_version.revision_date AS book_revision_date,
       book_version.revision      AS book_revision_num,
       bookcase.bookcase_key      AS bookcase_key,
       bookcase_version.title     AS bookcase_title
FROM techlib.book_version
         INNER JOIN techlib.book ON book_version.book_id = book.id
         INNER JOIN techlib.bookcase ON bookcase.id = book.bookcase_id
         INNER JOIN techlib.bookcase_version on bookcase_version.bookcase_id = bookcase.id
WHERE bookcase.bookcase_key = bookcaseKey
  AND book_version.bookcase_version = bookcaseVersion
  AND bookcase_version.bookcase_version = bookcaseVersion
$$;