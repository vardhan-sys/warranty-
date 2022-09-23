ALTER TABLE bookcase
DROP COLUMN IF EXISTS title;

ALTER TABLE bookcase_version
ADD COLUMN IF NOT EXISTS title varchar NULL;

ALTER TABLE book_section
DROP CONSTRAINT IF EXISTS book_section_section_key_book_id_title_key,
DROP CONSTRAINT IF EXISTS book_section_section_key_book_id_title_parent_section_id_key,
ADD CONSTRAINT book_section_section_key_book_id_title_parent_section_id_key
UNIQUE (section_key, book_id, title, parent_section_id);