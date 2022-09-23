-- Add resource

INSERT INTO resource ("name", type, created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('enginemanuals', 'product', '212589907', now(), '212589907', now());
INSERT INTO resource ("name", type, created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('avsystems', 'product', '212589907', now(), '212589907', now());

-- Add Permission
INSERT INTO permission (resource, "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('enginemanuals', 'view', '212589907', now(), '212589907', now());
INSERT INTO permission (resource, "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('avsystems', 'view', '212589907', now(), '212589907', now());

-- Insert Role for avSystem
INSERT INTO "role" ("name", label, description, policy, created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('avsystems-superuser', 'Super User', 'Able to use all features of the Documents Admin Tool.', '{
  "technologyLevels": ["M", "0", "1", "2", "3", "4", "5"]
}', '212589907', now(), '212589907', now());

INSERT INTO "role" ("name", label, description, policy, created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('avsystems-uploader', 'Document Uploader', 'Able to generate reports for specified engine models and/or airframes.',
'{"technologyLevels": []}', '212589907', now(), '212589907', now());

-- Add enginemanuals resource to existing roles
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('superuser', 'enginemanuals', 'view', '212589907', now(), '212589907', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('restricted-repair-provisioner', 'enginemanuals', 'view', '212589907', now(), '212589907', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('provisioner', 'enginemanuals', 'view', '212589907', now(), '212589907', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('publisher', 'enginemanuals', 'view', '212589907', now(), '212589907', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('uploader', 'enginemanuals', 'view', '212589907', now(), '212589907', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('reporter', 'enginemanuals', 'view', '212589907', now(), '212589907', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('reviewer', 'enginemanuals', 'view', '212589907', now(), '212589907', now());

-- Insert superuser Role_Permissions for avsystems
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('avsystems-superuser', 'companies', 'view', '212589907', now(), '212589907', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('avsystems-superuser', 'companies', 'edit', '212589907', now(), '212589907', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('avsystems-superuser', 'admin-management', 'view', '212589907', now(), '212589907', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('avsystems-superuser', 'admin-management', 'edit', '212589907', now(), '212589907', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('avsystems-superuser', 'audit-trail', 'view', '212589907', now(), '212589907', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('avsystems-superuser', 'audit-trail', 'edit', '212589907', now(), '212589907', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('avsystems-superuser', 'uploader', 'view', '212589907', now(), '212589907', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('avsystems-superuser', 'uploader', 'edit', '212589907', now(), '212589907', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('avsystems-superuser', 'avsystems', 'view', '212589907', now(), '212589907', now());

-- Insert Uploader Role_Permissions for avSystem
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('avsystems-uploader', 'uploader', 'view', '212589907', now(), '212589907', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('avsystems-uploader', 'uploader', 'edit', '212589907', now(), '212589907', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('avsystems-uploader', 'avsystems', 'view', '212589907', now(), '212589907', now());

--update description for existing role
update role set description ='Able to use all features of the Documents Admin Tool.' where name ='superuser';
update role set description ='Able to provision access to select engine models and/or airframes, and enable all documents regardless of repair level.'
where name ='restricted-repair-provisioner';
update role set description ='Able to provision access to select engine models and/or airframes, and enable all documents up to repair level 2.'
where name ='provisioner';
update role set description ='Able to preview documents and change their status.'
where name ='publisher';
update role set description ='Able to generate reports for specified engine models and/or airframes.'
where name ='uploader';
update role set description ='Able to preview select documents prior to publishing.'
where name ='reviewer';
update role set description ='Able to generate reports for specified engine models and/or airframes.'
where name ='reporter';

--update label for super user for displaying in UI
update role set label ='Super User' where name ='superuser';

--provide superuser access for avsystems
INSERT INTO user_role (sso, "role", "attributes", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('212462850', 'avsystems-superuser', '{
  "engineModels": [
    "all"
  ],
  "airFrames": [
    "all"
  ],
  "docTypes": [
    "all"
  ]
}', '212462850', now(), '212462850', now());
