
-- Insertar usuarios de prueba (password: password123)
-- Los IDs se generan secuencialmente del 1 al 20

-- Voluntarios (1, 2, 3, 10, 11, 20)
INSERT IGNORE INTO usuarios (email, username, contrasena, rol, created_at) VALUES ('laura@mail.com', 'laura_g', '$2a$10$8.UnVuG9HHgffUDAlk8q6uy57vnIFC3F46U.p.9.3N.p9.3N.p9.3', 'ROLE_VOLUNTARIO', NOW());
INSERT IGNORE INTO usuarios (email, username, contrasena, rol, created_at) VALUES ('carlos@mail.com', 'carlos_m', '$2a$10$8.UnVuG9HHgffUDAlk8q6uy57vnIFC3F46U.p.9.3N.p9.3N.p9.3', 'ROLE_VOLUNTARIO', NOW());
INSERT IGNORE INTO usuarios (email, username, contrasena, rol, created_at) VALUES ('marta@mail.com', 'marta_l', '$2a$10$8.UnVuG9HHgffUDAlk8q6uy57vnIFC3F46U.p.9.3N.p9.3N.p9.3', 'ROLE_VOLUNTARIO', NOW());

-- Adoptantes (4, 5, 6, 7, 8, 9)
INSERT IGNORE INTO usuarios (email, username, contrasena, rol, created_at) VALUES ('diego@mail.com', 'diego_r', '$2a$10$8.UnVuG9HHgffUDAlk8q6uy57vnIFC3F46U.p.9.3N.p9.3N.p9.3', 'ROLE_ADOPTANTE', NOW());
INSERT IGNORE INTO usuarios (email, username, contrasena, rol, created_at) VALUES ('lucia@mail.com', 'lucia_m', '$2a$10$8.UnVuG9HHgffUDAlk8q6uy57vnIFC3F46U.p.9.3N.p9.3N.p9.3', 'ROLE_ADOPTANTE', NOW());
INSERT IGNORE INTO usuarios (email, username, contrasena, rol, created_at) VALUES ('mario@mail.com', 'mario_g', '$2a$10$8.UnVuG9HHgffUDAlk8q6uy57vnIFC3F46U.p.9.3N.p9.3N.p9.3', 'ROLE_ADOPTANTE', NOW());
INSERT IGNORE INTO usuarios (email, username, contrasena, rol, created_at) VALUES ('sara@mail.com', 'sara_n', '$2a$10$8.UnVuG9HHgffUDAlk8q6uy57vnIFC3F46U.p.9.3N.p9.3N.p9.3', 'ROLE_ADOPTANTE', NOW());
INSERT IGNORE INTO usuarios (email, username, contrasena, rol, created_at) VALUES ('pablo@mail.com', 'pablo_d', '$2a$10$8.UnVuG9HHgffUDAlk8q6uy57vnIFC3F46U.p.9.3N.p9.3N.p9.3', 'ROLE_ADOPTANTE', NOW());
INSERT IGNORE INTO usuarios (email, username, contrasena, rol, created_at) VALUES ('david@mail.com', 'david_t', '$2a$10$8.UnVuG9HHgffUDAlk8q6uy57vnIFC3F46U.p.9.3N.p9.3N.p9.3', 'ROLE_ADOPTANTE', NOW());

-- Voluntarios (10, 11)
INSERT IGNORE INTO usuarios (email, username, contrasena, rol, created_at) VALUES ('elena@mail.com', 'elena_r', '$2a$10$8.UnVuG9HHgffUDAlk8q6uy57vnIFC3F46U.p.9.3N.p9.3N.p9.3', 'ROLE_VOLUNTARIO', NOW());
INSERT IGNORE INTO usuarios (email, username, contrasena, rol, created_at) VALUES ('javier@mail.com', 'javier_s', '$2a$10$8.UnVuG9HHgffUDAlk8q6uy57vnIFC3F46U.p.9.3N.p9.3N.p9.3', 'ROLE_VOLUNTARIO', NOW());

-- Adoptantes (12, 13, 14, 15)
INSERT IGNORE INTO usuarios (email, username, contrasena, rol, created_at) VALUES ('ana@mail.com', 'ana_b', '$2a$10$8.UnVuG9HHgffUDAlk8q6uy57vnIFC3F46U.p.9.3N.p9.3N.p9.3', 'ROLE_ADOPTANTE', NOW());
INSERT IGNORE INTO usuarios (email, username, contrasena, rol, created_at) VALUES ('sergio@mail.com', 'sergio_r', '$2a$10$8.UnVuG9HHgffUDAlk8q6uy57vnIFC3F46U.p.9.3N.p9.3N.p9.3', 'ROLE_ADOPTANTE', NOW());
INSERT IGNORE INTO usuarios (email, username, contrasena, rol, created_at) VALUES ('clara@mail.com', 'clara_l', '$2a$10$8.UnVuG9HHgffUDAlk8q6uy57vnIFC3F46U.p.9.3N.p9.3N.p9.3', 'ROLE_ADOPTANTE', NOW());
INSERT IGNORE INTO usuarios (email, username, contrasena, rol, created_at) VALUES ('roberto@mail.com', 'roberto_c', '$2a$10$8.UnVuG9HHgffUDAlk8q6uy57vnIFC3F46U.p.9.3N.p9.3N.p9.3', 'ROLE_ADOPTANTE', NOW());

-- Simpatizantes / Público (17, 18, 19) -> Sin perfil legal
INSERT IGNORE INTO usuarios (email, username, contrasena, rol, created_at) VALUES ('sofia@mail.com', 'sofia_v', '$2a$10$8.UnVuG9HHgffUDAlk8q6uy57vnIFC3F46U.p.9.3N.p9.3N.p9.3', 'ROLE_PUBLICO', NOW());
INSERT IGNORE INTO usuarios (email, username, contrasena, rol, created_at) VALUES ('miguel@mail.com', 'miguel_a', '$2a$10$8.UnVuG9HHgffUDAlk8q6uy57vnIFC3F46U.p.9.3N.p9.3N.p9.3', 'ROLE_PUBLICO', NOW());
INSERT IGNORE INTO usuarios (email, username, contrasena, rol, created_at) VALUES ('isabel@mail.com', 'isabel_p', '$2a$10$8.UnVuG9HHgffUDAlk8q6uy57vnIFC3F46U.p.9.3N.p9.3N.p9.3', 'ROLE_PUBLICO', NOW());

-- Voluntario (19)
INSERT IGNORE INTO usuarios (email, username, contrasena, rol, created_at) VALUES ('antonio@mail.com', 'antonio_b', '$2a$10$8.UnVuG9HHgffUDAlk8q6uy57vnIFC3F46U.p.9.3N.p9.3N.p9.3', 'ROLE_VOLUNTARIO', NOW());
