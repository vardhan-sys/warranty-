-- Insert Resources
INSERT INTO resource ("name", type, created_by, creation_date, last_updated_by, last_updated_date)
	VALUES('feature-flags', 'tab', '212409106', now(), '212409106', now());

-- Insert Permissions
INSERT INTO permission (resource, "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('feature-flags', 'view', '212409106', now(), '212409106', now());

INSERT INTO permission (resource, "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('feature-flags', 'edit', '212409106', now(), '212409106', now());

-- Insert Role
INSERT INTO role (name, label, description, policy, created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('developer', 'Developer', 'Has access to the Feature Flag tab in order to refresh the feature flags in the service.', '{"technologyLevels": []}', '212409106', now(), '212409106', now());

-- Insert Developer Role_Permissions
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('developer', 'feature-flags', 'view', '212409106', now(), '212409106', now());

INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('developer', 'feature-flags', 'edit', '212409106', now(), '212409106', now());

INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('developer', 'admin-management', 'view', '212409106', now(), '212409106', now());

INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('developer', 'admin-management', 'edit', '212409106', now(), '212409106', now());




