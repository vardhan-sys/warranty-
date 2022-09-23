INSERT INTO techlib.archival_company (icao_code) VALUES('JUS');

INSERT INTO archival_company_eml (archival_company_id, archival_engine_model_lookup_id) VALUES((SELECT ID FROM ARCHIVAL_COMPANY WHERE icao_code = 'JUS'), (SELECT id FROM archival_engine_model_lookup WHERE model = 'CF700-2C'));
INSERT INTO archival_company_eml (archival_company_id, archival_engine_model_lookup_id) VALUES((SELECT ID FROM ARCHIVAL_COMPANY WHERE icao_code = 'JUS'), (SELECT id FROM archival_engine_model_lookup WHERE model = 'CF700-2D'));
INSERT INTO archival_company_eml (archival_company_id, archival_engine_model_lookup_id) VALUES((SELECT ID FROM ARCHIVAL_COMPANY WHERE icao_code = 'JUS'), (SELECT id FROM archival_engine_model_lookup WHERE model = 'CF700-2D-2'));
