--Adds ability for developer role to view and edit publisher tab.
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('developer', 'publisher', 'view', '212409106', now(), '212409106', now());

INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('developer', 'publisher', 'edit', '212409106', now(), '212409106', now());