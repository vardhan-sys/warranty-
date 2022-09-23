--Remove ablility for superusers to toggle feature flags.
delete from role_permission
where role='superuser' and resource='feature-flags'