CREATE TABLE "cortona_lookup"
(
	id                uuid  NOT NULL DEFAULT public.gen_random_uuid(),
	html_filename     VARCHAR,
	cortona_filename  VARCHAR,
	bookcase          VARCHAR,
	bookcase_version  VARCHAR,
	book              VARCHAR,
	CONSTRAINT cortona_lookup_pk PRIMARY KEY (id),
	CONSTRAINT cortona_lookup_unique UNIQUE (html_filename, cortona_filename, bookcase, bookcase_version, book)
);

CREATE INDEX cortona_lookup_idx ON cortona_lookup (html_filename);