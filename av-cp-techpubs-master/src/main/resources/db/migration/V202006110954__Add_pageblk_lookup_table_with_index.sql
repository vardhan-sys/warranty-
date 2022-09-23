CREATE TABLE "pageblk_lookup"
(
	id                  uuid     NOT NULL DEFAULT public.gen_random_uuid(),
	bookcase_key        VARCHAR,
	bookcase_version    VARCHAR,
	book_key            VARCHAR,
	revision            VARCHAR,
	pageblk_key         VARCHAR,
	online_filename     VARCHAR,
	target              VARCHAR,
	CONSTRAINT target_lookup_pk PRIMARY KEY (id),
	CONSTRAINT target_lookup_unique UNIQUE (bookcase_key, bookcase_version, book_key, revision, pageblk_key, online_filename, target)
);
CREATE INDEX pageblk_lookup_target_idx ON pageblk_lookup (target);