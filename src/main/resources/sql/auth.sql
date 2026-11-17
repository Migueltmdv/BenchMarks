-- ============================================================
-- AUTORIZACIÓN: permisos, roles, jerarquía y permisos por rol.
-- Se ejecuta el primero porque users.sql necesita que existan los roles.
-- ============================================================

-- Vacía las tablas de autorización (CASCADE limpia también las de unión).
TRUNCATE permissions, roles, role_hierarchy, role_permissions CASCADE;

-- Permisos granulares. El id tiene el formato "recurso:acción" y es el que
-- AuthenticationService convierte en autoridad de Spring Security.
INSERT INTO permissions(id, resource, action) VALUES
('users:read', 'users', 'read'),
('users:create', 'users', 'create'),
('users:update', 'users', 'update'),
('users:delete', 'users', 'delete'),
('books:read', 'books', 'read'),
('books:create', 'books', 'create'),
('books:update', 'books', 'update'),
('books:delete', 'books', 'delete');

-- Roles disponibles. El nombre se usa como autoridad "ROLE_<nombre>".
INSERT INTO roles(rolename) VALUES
('ADMIN'),
('USER');

-- Jerarquía: ADMIN hereda todos los permisos de USER.
INSERT INTO role_hierarchy(role, includes) VALUES
('ADMIN', 'USER');

-- Permisos asignados a cada rol.
INSERT INTO role_permissions(role, permission) VALUES
('USER', 'users:read'),
('USER', 'books:read'),
('ADMIN', 'users:create'),
('ADMIN', 'users:update'),
('ADMIN', 'users:delete'),
('ADMIN', 'books:create'),
('ADMIN', 'books:update'),
('ADMIN', 'books:delete');