CREATE OR REPLACE FUNCTION loadpageblksbytypebookcasekeyandversion(pageblktype CHARACTER VARYING, bookcasekey CHARACTER VARYING,
                                                        bookcaseversion CHARACTER VARYING)
    RETURNS TABLE
            (
                pageblk_key    CHARACTER VARYING,
                title          CHARACTER VARYING,
                revision_date  CHARACTER VARYING,
                bookcase_key   CHARACTER VARYING,
                bookcase_title CHARACTER VARYING,
                file_name      CHARACTER VARYING,
                book_key       CHARACTER VARYING,
                sb_type        CHARACTER VARYING,
                type           CHARACTER VARYING
            )
    LANGUAGE SQL
AS
$$
SELECT pageblk_key                     AS pageblk_key
     , pageblk.title                   AS title
     , pageblk_version.revision_date   AS revision_date
     , bookcase.bookcase_key           AS bookcase_key
     , bookcase_version.title          AS bookcase_title
     , pageblk_version.online_filename AS file_name
     , book.book_key                   AS book_key
     , (pageblk.metadata ->> 'type')   AS sb_type
     , pageblk.publication_type_code   AS type
FROM techlib.pageblk
         INNER JOIN techlib.pageblk_version on pageblk.id = pageblk_version.pageblk_id
         INNER JOIN techlib.book_section on book_section.id = pageblk.book_section_id
         INNER JOIN techlib.book on book.id = book_section.book_id
         INNER JOIN techlib.bookcase on bookcase.id = book.bookcase_id
         INNER JOIN techlib.bookcase_version on bookcase_version.bookcase_id = bookcase.id
WHERE pageblk.publication_type_code = pageblkType
  AND pageblk_version.bookcase_version = bookcaseVersion
  AND bookcase.bookcase_key = bookcaseKey
  AND bookcase_version.bookcase_version = bookcaseVersion
$$;