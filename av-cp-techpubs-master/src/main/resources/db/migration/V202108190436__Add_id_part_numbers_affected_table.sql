
ALTER TABLE part_numbers_affected DROP CONSTRAINT part_numbers_affected_pk;

ALTER TABLE part_numbers_affected
ADD COLUMN id     UUID       DEFAULT public.gen_random_uuid() NOT NULL CONSTRAINT part_numbers_affected_pkey PRIMARY KEY;