CREATE OR REPLACE FUNCTION getbookcaseenabledpgblksforicao(icao CHARACTER VARYING, bookcasekey CHARACTER VARYING,
                                                bookcaseversion CHARACTER VARYING, bookkey CHARACTER VARYING)
    RETURNS TABLE
            (
                online_filename           CHARACTER VARYING,
                offline_filename          CHARACTER VARYING,
                ded_filename              CHARACTER VARYING,
                is_an_enabled_smm_pageblk BOOLEAN
            )
    LANGUAGE SQL
AS
$$
SELECT online_filename,
       offline_filename,
       ded_filename,
       (b.book_type = 'smm' AND v.technology_level_id IS NOT NULL) AS is_an_enabled_smm_pageblk
FROM techlib.bookcase bc
         INNER JOIN techlib.book b ON bc.id = b.bookcase_id
         INNER JOIN techlib.book_section s ON b.id = s.book_id
         INNER JOIN techlib.pageblk p ON s.id = p.book_section_id
         INNER JOIN techlib.pageblk_version v ON p.id = v.pageblk_id
WHERE upper(bookcase_key) = upper(bookcaseKey)
  AND upper(book_key) = upper(bookKey)
  AND v.bookcase_version = bookcaseVersion
  AND (
        v.technology_level_id IS NULL
        OR
        (
                    p.id IN
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
                OR
                    v.technology_level_id IN
                    (SELECT techlv_id
                     FROM techlib.company_engine_techlv_enablement
                     WHERE upper(techlib.company_engine_techlv_enablement.icao_code) = upper(icao)
                       AND upper(bookcase_key) = upper(bookcaseKey)
                    )
            )
    );
$$;