-- ============================================================
-- USUARIOS DE PRUEBA
-- Contraseña: 1234 para ambos.
-- El prefijo {noop} indica al DelegatingPasswordEncoder que no hay hash:
-- es válido solo para datos de ejemplo. En producción se usaría {bcrypt}.
-- ============================================================

-- CASCADE limpia también user_favorite_benches y las FKs que apunten a users.
TRUNCATE users, user_roles CASCADE;

-- user_id es la PK (String) y el nombre de usuario del login.
INSERT INTO users(user_id, name, last_name, nickname, birthday, location, password) VALUES
('user', 'Regular', 'User', 'user', '1995-01-01', 'Santiago', '{noop}1234'),
('admin', 'Admin', 'User', 'admin', '1990-01-01', 'A Coruna', '{noop}1234');

-- Asignación de roles (tabla de unión user_roles).
INSERT INTO user_roles(user_id, role) VALUES
('user', 'USER'),
('admin', 'ADMIN');
