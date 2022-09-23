DELETE from archival_company_eml;
DELETE from archival_company;

INSERT INTO techlib.archival_company (icao_code) VALUES('CALS');
INSERT INTO archival_company_eml (archival_company_id, archival_engine_model_lookup_id) VALUES((SELECT ID FROM ARCHIVAL_COMPANY WHERE icao_code = 'CALS'), (SELECT id FROM archival_engine_model_lookup WHERE model = 'CJ610-1'));
INSERT INTO archival_company_eml (archival_company_id, archival_engine_model_lookup_id) VALUES((SELECT ID FROM ARCHIVAL_COMPANY WHERE icao_code = 'CALS'), (SELECT id FROM archival_engine_model_lookup WHERE model = 'CJ610-4'));
INSERT INTO archival_company_eml (archival_company_id, archival_engine_model_lookup_id) VALUES((SELECT ID FROM ARCHIVAL_COMPANY WHERE icao_code = 'CALS'), (SELECT id FROM archival_engine_model_lookup WHERE model = 'CJ610-5'));
INSERT INTO archival_company_eml (archival_company_id, archival_engine_model_lookup_id) VALUES((SELECT ID FROM ARCHIVAL_COMPANY WHERE icao_code = 'CALS'), (SELECT id FROM archival_engine_model_lookup WHERE model = 'CJ610-6'));
INSERT INTO archival_company_eml (archival_company_id, archival_engine_model_lookup_id) VALUES((SELECT ID FROM ARCHIVAL_COMPANY WHERE icao_code = 'CALS'), (SELECT id FROM archival_engine_model_lookup WHERE model = 'CJ610-8'));
INSERT INTO archival_company_eml (archival_company_id, archival_engine_model_lookup_id) VALUES((SELECT ID FROM ARCHIVAL_COMPANY WHERE icao_code = 'CALS'), (SELECT id FROM archival_engine_model_lookup WHERE model = 'CJ610-8A'));
INSERT INTO archival_company_eml (archival_company_id, archival_engine_model_lookup_id) VALUES((SELECT ID FROM ARCHIVAL_COMPANY WHERE icao_code = 'CALS'), (SELECT id FROM archival_engine_model_lookup WHERE model = 'CJ610-9'));

INSERT INTO techlib.archival_company (icao_code) VALUES('CAUK');
INSERT INTO archival_company_eml (archival_company_id, archival_engine_model_lookup_id) VALUES((SELECT ID FROM ARCHIVAL_COMPANY WHERE icao_code = 'CAUK'), (SELECT id FROM archival_engine_model_lookup WHERE model = 'CF700-2C'));
INSERT INTO archival_company_eml (archival_company_id, archival_engine_model_lookup_id) VALUES((SELECT ID FROM ARCHIVAL_COMPANY WHERE icao_code = 'CAUK'), (SELECT id FROM archival_engine_model_lookup WHERE model = 'CF700-2D-2'));
INSERT INTO archival_company_eml (archival_company_id, archival_engine_model_lookup_id) VALUES((SELECT ID FROM ARCHIVAL_COMPANY WHERE icao_code = 'CAUK'), (SELECT id FROM archival_engine_model_lookup WHERE model = 'CF700-2D'));

INSERT INTO techlib.archival_company (icao_code) VALUES('GEAE');
INSERT INTO archival_company_eml (archival_company_id, archival_engine_model_lookup_id) VALUES((SELECT ID FROM ARCHIVAL_COMPANY WHERE icao_code = 'GEAE'), (SELECT id FROM archival_engine_model_lookup WHERE model = 'CF700-2C'));
INSERT INTO archival_company_eml (archival_company_id, archival_engine_model_lookup_id) VALUES((SELECT ID FROM ARCHIVAL_COMPANY WHERE icao_code = 'GEAE'), (SELECT id FROM archival_engine_model_lookup WHERE model = 'CF700-2D-2'));
INSERT INTO archival_company_eml (archival_company_id, archival_engine_model_lookup_id) VALUES((SELECT ID FROM ARCHIVAL_COMPANY WHERE icao_code = 'GEAE'), (SELECT id FROM archival_engine_model_lookup WHERE model = 'CF700-2D'));

INSERT INTO techlib.archival_company (icao_code) VALUES('MMCT');
INSERT INTO archival_company_eml (archival_company_id, archival_engine_model_lookup_id) VALUES((SELECT ID FROM ARCHIVAL_COMPANY WHERE icao_code = 'MMCT'), (SELECT id FROM archival_engine_model_lookup WHERE model = 'CF700-2C'));
INSERT INTO archival_company_eml (archival_company_id, archival_engine_model_lookup_id) VALUES((SELECT ID FROM ARCHIVAL_COMPANY WHERE icao_code = 'MMCT'), (SELECT id FROM archival_engine_model_lookup WHERE model = 'CF700-2D-2'));
INSERT INTO archival_company_eml (archival_company_id, archival_engine_model_lookup_id) VALUES((SELECT ID FROM ARCHIVAL_COMPANY WHERE icao_code = 'MMCT'), (SELECT id FROM archival_engine_model_lookup WHERE model = 'CF700-2D'));

INSERT INTO techlib.archival_company (icao_code) VALUES('MMCV');
INSERT INTO archival_company_eml (archival_company_id, archival_engine_model_lookup_id) VALUES((SELECT ID FROM ARCHIVAL_COMPANY WHERE icao_code = 'MMCV'), (SELECT id FROM archival_engine_model_lookup WHERE model = 'CF700-2C'));
INSERT INTO archival_company_eml (archival_company_id, archival_engine_model_lookup_id) VALUES((SELECT ID FROM ARCHIVAL_COMPANY WHERE icao_code = 'MMCV'), (SELECT id FROM archival_engine_model_lookup WHERE model = 'CF700-2D-2'));
INSERT INTO archival_company_eml (archival_company_id, archival_engine_model_lookup_id) VALUES((SELECT ID FROM ARCHIVAL_COMPANY WHERE icao_code = 'MMCV'), (SELECT id FROM archival_engine_model_lookup WHERE model = 'CF700-2D'));

INSERT INTO techlib.archival_company (icao_code) VALUES('ZXIM');
INSERT INTO archival_company_eml (archival_company_id, archival_engine_model_lookup_id) VALUES((SELECT ID FROM ARCHIVAL_COMPANY WHERE icao_code = 'ZXIM'), (SELECT id FROM archival_engine_model_lookup WHERE model = 'CJ610-1'));
INSERT INTO archival_company_eml (archival_company_id, archival_engine_model_lookup_id) VALUES((SELECT ID FROM ARCHIVAL_COMPANY WHERE icao_code = 'ZXIM'), (SELECT id FROM archival_engine_model_lookup WHERE model = 'CJ610-4'));
INSERT INTO archival_company_eml (archival_company_id, archival_engine_model_lookup_id) VALUES((SELECT ID FROM ARCHIVAL_COMPANY WHERE icao_code = 'ZXIM'), (SELECT id FROM archival_engine_model_lookup WHERE model = 'CJ610-5'));
INSERT INTO archival_company_eml (archival_company_id, archival_engine_model_lookup_id) VALUES((SELECT ID FROM ARCHIVAL_COMPANY WHERE icao_code = 'ZXIM'), (SELECT id FROM archival_engine_model_lookup WHERE model = 'CJ610-6'));
INSERT INTO archival_company_eml (archival_company_id, archival_engine_model_lookup_id) VALUES((SELECT ID FROM ARCHIVAL_COMPANY WHERE icao_code = 'ZXIM'), (SELECT id FROM archival_engine_model_lookup WHERE model = 'CJ610-8'));
INSERT INTO archival_company_eml (archival_company_id, archival_engine_model_lookup_id) VALUES((SELECT ID FROM ARCHIVAL_COMPANY WHERE icao_code = 'ZXIM'), (SELECT id FROM archival_engine_model_lookup WHERE model = 'CJ610-8A'));
INSERT INTO archival_company_eml (archival_company_id, archival_engine_model_lookup_id) VALUES((SELECT ID FROM ARCHIVAL_COMPANY WHERE icao_code = 'ZXIM'), (SELECT id FROM archival_engine_model_lookup WHERE model = 'CJ610-9'));
