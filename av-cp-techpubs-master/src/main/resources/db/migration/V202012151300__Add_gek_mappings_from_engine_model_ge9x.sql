INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('GE9X-105', 'GE9X', '212601653', now(), '212601653', now());
INSERT INTO engine_program (bookcase_key, "program", offline_filename, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek131810', 'GE9X-105', 'GE9X', '212601653', now(), '212601653', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
  	VALUES('GE9X-105', 'gek131810', '212601653', now(), '212601653', now());