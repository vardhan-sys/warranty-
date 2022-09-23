ALTER TABLE engine_model_program
DROP CONSTRAINT engine_model_program_model_fk,
ADD CONSTRAINT engine_model_program_model_fk
    FOREIGN KEY (engine_model)
    REFERENCES engine_model (model)
    ON UPDATE CASCADE;

ALTER TABLE engine_model_program
DROP CONSTRAINT engine_model_program_bookcase_key_fk,
ADD CONSTRAINT engine_model_program_bookcase_key_fk
    FOREIGN KEY (bookcase_key)
    REFERENCES engine_program (bookcase_key)
    ON UPDATE CASCADE;