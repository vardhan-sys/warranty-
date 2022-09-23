-- Insert Developer Role_Permissions to SuperUser
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('superuser', 'feature-flags', 'view', '212409106', now(), '212409106', now());

INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('superuser', 'feature-flags', 'edit', '212409106', now(), '212409106', now());





