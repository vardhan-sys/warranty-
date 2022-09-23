DELETE FROM techlib.role_permission WHERE "role" = 'avsystems-superuser' AND resource = 'avsystems' AND "action" = 'edit';

DELETE FROM techlib.role_permission WHERE "role" = 'avsystems-provisioner' AND resource = 'avsystems' AND "action" = 'edit';

DELETE FROM techlib."permission" WHERE resource = 'avsystems' AND "action" = 'edit';
