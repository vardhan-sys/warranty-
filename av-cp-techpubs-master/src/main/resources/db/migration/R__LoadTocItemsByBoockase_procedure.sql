-- Stored Procedures --
drop FUNCTION loadtocitemsbybookcase(org_id CHARACTER VARYING, bc_key CHARACTER VARYING,
                                                                bc_version CHARACTER VARYING);

CREATE OR REPLACE FUNCTION loadtocitemsbybookcase(org_id CHARACTER VARYING, bc_key CHARACTER VARYING,
                                                  bc_version CHARACTER VARYING)
    RETURNS TABLE
            (
                id            UUID,
                node_order    SMALLINT,
                tree_depth    INTEGER,
                title         CHARACTER VARYING,
                toc_title     CHARACTER VARYING,
                parent_id     UUID,
                node_type     CHARACTER VARYING,
                revision_date CHARACTER VARYING,
                node_key      CHARACTER VARYING,
                filename      CHARACTER VARYING,
                approve_publish_flag boolean,
                publication_type_code CHARACTER VARYING,
                revision CHARACTER VARYING
            )
    LANGUAGE SQL
AS
$$
WITH
   --load the bookcase that matches the bookcase_key parameter into temp table
    bookcases AS (
        SELECT bookcase.id                       AS bookcase_id,
               bookcase_version.bookcase_version AS bookcase_version
        FROM techlib.bookcase_version
                 INNER JOIN techlib.bookcase
                            ON bookcase.id = bookcase_version.bookcase_id
        WHERE bookcase.bookcase_key = bc_key
          AND bookcase_version.bookcase_version = bc_version
--					and bookcase_version.bookcase_version_status_code = 'online'
        LIMIT 1
    )
   , books AS
    (SELECT book.id                    AS id,
            book_version.book_order    as node_order,
            1::int                     AS tree_depth,
            book_version.title         AS title,
            book_version.title         AS toc_title, --should this logic be in the service or here in DB?,
            NULL::uuid                 AS parent_id,
            book.book_type             AS node_type,
            book_version.revision_date AS revision_date,
            book.book_key              AS node_key,
            NULL::varchar              AS filename,
            false::boolean             AS approve_publish_flag,
            NULL::varchar              AS publication_type_code,
            NULL::varchar              AS revision
     FROM techlib.book
              INNER JOIN techlib.book_version ON book.id = book_version.book_id
     WHERE (SELECT bookcase_id FROM bookcases) = book.bookcase_id
       AND book_version.bookcase_version = (SELECT bookcase_version FROM bookcases))
   --load all sections into a temp table
   , sections AS
    (SELECT book_section.id                         as id,
            book_section_version.book_section_order as node_order,
            (CASE
                 WHEN book_section.tree_depth = 0
                     THEN book_section.tree_depth --when a section's tree depth is 0, it is a dummy section (it only exists to join a pageblk to a book); Keep it as 0 to indicate a dummy section to consumers
                 ELSE book_section.tree_depth + 1
                END)                                as tree_depth,
            book_section.title                      AS title,
            book_section.title                      AS toc_title,
            (CASE
                 WHEN book_section.parent_section_id IS NULL THEN book_section.book_id
                 ELSE book_section.parent_section_id
                END)                                AS parent_id,
            NULL::varchar                           AS node_type,
            NULL::varchar                           AS revision_date,
            book_section.section_key                AS node_key,
            NULL::varchar                           AS filename,
            false::boolean                          AS approve_publish_flag,
            NULL::varchar                           AS publication_type_code,
            NULL::varchar                           AS revision
     FROM techlib.book_section
              INNER JOIN techlib.book_section_version ON book_section.id = book_section_version.book_section_id
     WHERE book_section.book_id IN (SELECT id FROM books)
       AND book_section_version.bookcase_version = (SELECT bookcase_version FROM bookcases))

   --load all pageblks into a temp table
   , pageblks AS
    (select pageblk_version.id                    AS id,
            pageblk_version.pageblk_order   as node_order,
            book_section.tree_depth + 2     as tree_depth,
            pageblk.title                   AS title,
            pageblk.toc_title               AS toc_title,
            pageblk.book_section_id         AS parent_id,
            (CASE
                 WHEN publication_type.code = 'sb' AND ((pageblk.metadata ->> 'type')::VARCHAR = 'alert' OR
                                                        (pageblk.metadata ->> 'type')::VARCHAR = 'alert-cover')
                     THEN 'sbalert'
                 ELSE publication_type.code
                END)                        AS node_type,
            pageblk_version.revision_date   AS revision_date,
            pageblk.pageblk_key             AS node_key,
            pageblk_version.online_filename AS filename,
            pageblk.approved_for_publish    AS approve_publish_flag,
            publication_type.code           AS publication_type_code,
            pageblk_version.revision		AS revision
     FROM techlib.pageblk
              INNER JOIN techlib.pageblk_version ON pageblk.id = pageblk_version.pageblk_id
              INNER JOIN techlib.book_section ON pageblk.book_section_id = book_section.id
              LEFT JOIN techlib.publication_type ON publication_type.code = techlib.pageblk.publication_type_code
     WHERE pageblk.book_section_id IN (SELECT id FROM sections)
       AND pageblk_version.bookcase_version = (SELECT bookcase_version FROM bookcases))

SELECT *
FROM books
UNION ALL
SELECT *
FROM sections
UNION ALL
SELECT *
FROM pageblks
$$;