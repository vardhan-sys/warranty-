ALTER TABLE engine_document
	ADD COLUMN file_name	VARCHAR NOT NULL,
	ADD COLUMN created_date     TIMESTAMP DEFAULT now(),
	ADD COLUMN last_updated_date 	TIMESTAMP DEFAULT now();