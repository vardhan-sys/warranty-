ALTER TABLE pageblk
ADD COLUMN IF NOT EXISTS email_notification BOOLEAN NOT NULL DEFAULT FALSE;
