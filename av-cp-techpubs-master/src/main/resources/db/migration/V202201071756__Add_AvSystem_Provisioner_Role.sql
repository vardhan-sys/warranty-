
-- Insert Role for avSystem
INSERT INTO role("name", label, description, policy, created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('avsystems-provisioner', 'Provisioner', 'Able to provision access to select engine models and/or airframes, and enable all documents up to repair level 2.',
'{"technologyLevels": ["M","0","1","5"]}', '212589907', now(), '212589907', now());


-- Insert Uploader Role_Permissions for avSystem
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('avsystems-provisioner', 'companies', 'view', '212589907', now(), '212589907', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('avsystems-provisioner', 'companies', 'edit', '212589907', now(), '212589907', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('avsystems-provisioner', 'audit-trail', 'view', '212589907', now(), '212589907', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('avsystems-provisioner', 'audit-trail', 'edit', '212589907', now(), '212589907', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('avsystems-provisioner', 'avsystems', 'view', '212589907', now(), '212589907', now());