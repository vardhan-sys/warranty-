ALTER TABLE pageblk
ADD COLUMN IF NOT EXISTS approved_for_publish BOOLEAN NOT NULL DEFAULT TRUE;
