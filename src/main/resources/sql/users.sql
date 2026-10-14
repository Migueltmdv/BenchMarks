TRUNCATE users, user_roles CASCADE;

INSERT INTO users(user_id, name, last_name, nickname, birthday, location, password) VALUES
('user', 'Regular', 'User', 'user', '1995-01-01', 'Santiago', '{noop}1234'),
('admin', 'Admin', 'User', 'admin', '1990-01-01', 'A Coruna', '{noop}1234');

INSERT INTO user_roles(user_id, role) VALUES
('user', 'USER'),
('admin', 'ADMIN');
