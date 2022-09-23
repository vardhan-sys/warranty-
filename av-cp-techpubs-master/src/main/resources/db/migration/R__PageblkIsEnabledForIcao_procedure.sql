CREATE OR REPLACE FUNCTION pageblkisenabledforicao(icao CHARACTER VARYING, filename CHARACTER VARYING,
                                                   bookcasekey CHARACTER VARYING, bookkey CHARACTER VARYING,
                                                   bookcaseversion CHARACTER VARYING) RETURNS BOOLEAN
    LANGUAGE SQL
AS
$$
SELECT exists
           (SELECT *
            FROM techlib.bookcase bc
                     INNER JOIN techlib.book b ON bc.id = b.bookcase_id
                     INNER JOIN techlib.book_section s ON b.id = s.book_id
                     INNER JOIN techlib.pageblk p ON s.id = p.book_section_id
                     INNER JOIN techlib.pageblk_version v ON p.id = v.pageblk_id
            WHERE upper(bookcase_key) = upper(bookcaseKey)
              AND upper(book_key) = upper(bookKey)
              AND upper(online_filename) = upper(filename)
              AND v.bookcase_version = bookcaseVersion
              AND (
                    (p.id IN
                     (
                         SELECT DISTINCT p.id
                         FROM techlib.pageblk p
                                  INNER JOIN techlib.company_engine_pageblk_enablement e
                                             ON p.pageblk_key = e.pageblk_key AND p.book_section_id = e.section_id
                         WHERE upper(e.icao_code) = upper(icao)
                           AND engine_model IN
                               (SELECT engine_model
                                FROM techlib.engine_model_program
                                WHERE upper(techlib.engine_model_program.bookcase_key) = upper(bookcasekey))
                     )
                        )
                    OR
                    v.technology_level_id IN
                    (SELECT techlv_id
                     FROM techlib.company_engine_techlv_enablement
                     WHERE upper(techlib.company_engine_techlv_enablement.icao_code) = upper(icao)
                       AND upper(bookcase_key) = upper(bookcaseKey)
                    )
                )
           )
           AS enabled;
$$;