INSERT INTO techlib."permission" (resource, "action", created_by, last_updated_by)
VALUES ('avsystems', 'edit', '212726591', '212726591');

INSERT INTO techlib.role_permission ("role", resource, "action", created_by, last_updated_by)
VALUES ('avsystems-provisioner', 'avsystems', 'edit', '212726591', '212726591');

INSERT INTO techlib.role_permission ("role", resource, "action", created_by, last_updated_by)
VALUES ('avsystems-superuser', 'avsystems', 'edit', '212726591', '212726591');