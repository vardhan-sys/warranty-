ALTER TABLE bookcase
ADD COLUMN send_email boolean DEFAULT false,
ADD COLUMN last_email_sent_date TIMESTAMP DEFAULT ('1970-01-01 00:00:00+00');