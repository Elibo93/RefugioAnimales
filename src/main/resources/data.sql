-- =====================================================
-- USUARIOS (10)
-- Roles: ROLE_ADMIN, ROLE_VOLUNTARIO, ROLE_ADOPTANTE
-- =====================================================
INSERT IGNORE INTO usuarios (nombre, apellido, email, contrasena, telefono, rol, created_at) VALUES
('Laura', 'García', 'laura.garcia@refugio.local', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '600000001', 'ROLE_VOLUNTARIO', NOW()),
('Carlos', 'Martín', 'carlos.martin@refugio.local', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '600000002', 'ROLE_VOLUNTARIO', NOW()),
('Marta', 'López', 'marta.lopez@refugio.local', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '600000003', 'ROLE_VOLUNTARIO', NOW()),
('Diego', 'Romero', 'diego.romero@local', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '600000004', 'ROLE_ADOPTANTE', NOW()),
('Lucía', 'Martínez', 'lucia.martinez@local', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '600000005', 'ROLE_ADOPTANTE', NOW()),
('Mario', 'Gómez', 'mario.gomez@local', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '600000006', 'ROLE_ADOPTANTE', NOW()),
('Sara', 'Nadal', 'sara.nadal@local', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '600000007', 'ROLE_ADOPTANTE', NOW()),
('Pablo', 'Díaz', 'pablo.diaz@local', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '600000008', 'ROLE_ADOPTANTE', NOW()),
('David', 'Torres', 'david.torres@local', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '600000009', 'ROLE_ADOPTANTE', NOW());

-- =====================================================
-- ADOPTANTES (6) - Vinculados a los usuarios 5 al 10
-- =====================================================
INSERT IGNORE INTO adoptantes (dni, direccion, fecha_nacimiento, estado_validacion, fecha_registro, usuario_id) VALUES
('12345678A', 'C/ Sol 1, Madrid', '2001-03-10', 'Aprobado', NOW(), 5),
('23456789B', 'C/ Luna 2, Sevilla', '2003-07-22', 'Aprobado', NOW(), 6),
('34567890C', 'C/ Norte 5, Valencia', '1998-11-15', 'Pendiente', NOW(), 7),
('45678901D', 'C/ Sur 8, Málaga', '2000-02-03', 'Aprobado', NOW(), 8),
('56789012E', 'Av. Mar 12, Cádiz', '1997-06-18', 'Aprobado', NOW(), 9),
('67890123F', 'C/ Parque 3, Bilbao', '1996-01-20', 'Pendiente', NOW(), 10);

-- =====================================================
-- VOLUNTARIOS (3) - Vinculados a los usuarios 2, 3, 4
-- =====================================================
INSERT IGNORE INTO voluntarios (disponibilidad, created_at, usuario_id) VALUES
('Tardes y fines de semana', NOW(), 2),
('Mañanas de Lunes a Viernes', NOW(), 3),
('Fines de semana completos', NOW(), 4);

-- =====================================================
-- ANIMALES (10)
-- =====================================================
INSERT IGNORE INTO animales (nombre, especie, especie_personalizada, raza, sexo, chip_id, estado, edad, tamano, descripcion, foto, fecha_ingreso) VALUES
('Luna', 'PERRO', NULL, 'Labrador', 'HEMBRA', 'CHIP001', 'DISPONIBLE', 3, 'MEDIANO', 'Perrita muy cariñosa', 'http://example.com/foto1.jpg', NOW()),
('Simba', 'GATO', NULL, 'Común Europeo', 'MACHO', 'CHIP002', 'DISPONIBLE', 2, 'PEQUEÑO', 'Gato dormilon', 'http://example.com/foto2.jpg', NOW()),
('Rex', 'PERRO', NULL, 'Pastor Alemán', 'MACHO', 'CHIP003', 'EN_TRATAMIENTO', 5, 'GRANDE', 'Buen guardian, pero necesita medicación', 'http://example.com/foto3.jpg', NOW()),
('Bella', 'PERRO', NULL, 'Galgo', 'HEMBRA', 'CHIP004', 'DISPONIBLE', 4, 'GRANDE', 'Corre muy rápido', 'http://example.com/foto4.jpg', NOW()),
('Nala', 'GATO', NULL, 'Siamés', 'HEMBRA', 'CHIP005', 'ADOPTADO', 1, 'PEQUEÑO', 'Muy curiosa e inteligente', 'http://example.com/foto5.jpg', NOW()),
('Thor', 'PERRO', NULL, 'Husky', 'MACHO', 'CHIP006', 'DISPONIBLE', 3, 'GRANDE', 'Le encanta la nieve y jugar', 'http://example.com/foto6.jpg', NOW()),
('Milo', 'GATO', NULL, 'Persa', 'MACHO', 'CHIP007', 'DISPONIBLE', 4, 'MEDIANO', 'Tranquilo y mimoso', 'http://example.com/foto7.jpg', NOW()),
('Kira', 'PERRO', NULL, 'Border Collie', 'HEMBRA', 'CHIP008', 'RESERVADO', 2, 'MEDIANO', 'Muy inteligente y activa', 'http://example.com/foto8.jpg', NOW()),
('Coco', 'OTRO', 'Conejo', 'Belier', 'HEMBRA', 'CHIP009', 'DISPONIBLE', 1, 'PEQUEÑO', 'Le encantan las zanahorias', 'http://example.com/foto9.jpg', NOW()),
('Rocky', 'PERRO', NULL, 'Boxer', 'MACHO', 'CHIP010', 'EN_TRATAMIENTO', 6, 'GRANDE', 'Fuerte pero con problemas articulares', 'http://example.com/foto10.jpg', NOW());

-- =====================================================
-- ADOPCIONES (1)
-- Animal 5 (Nala) está ADOPTADO. Lo adopta el Adoptante 1 (Diego)
-- =====================================================
INSERT IGNORE INTO adopciones (adoptante_id, animal_id, fecha_adopcion, estado, contrato) VALUES
(1, 5, NOW(), 'COMPLETADA', 'Contrato de adopción Nala firmado correctamente.');

-- =====================================================
-- DONACIONES (3)
-- Vinculadas a los usuarios que donan
-- =====================================================
INSERT IGNORE INTO donaciones (usuario_id, tipo, cantidad, fecha, descripcion) VALUES
(5, 'DINERO', 50.00, NOW(), 'Donación mensual de apoyo monetario'),
(2, 'COMIDA', 20.00, NOW(), 'Sacos de pienso canino y latas de gato'),
(6, 'MEDICINAS', 15.50, NOW(), 'Desparasitantes externos e internos');

-- =====================================================
-- HISTORIAL MÉDICO (3)
-- =====================================================
INSERT IGNORE INTO historial_medicos (id_animal, fecha, descripcion, tratamiento, veterinario) VALUES
(3, NOW(), 'Revisión por cojera pata trasera', 'Reposo y antiinflamatorios', 'Dr. Gómez'),
(10, NOW(), 'Problemas articulares detectados', 'Medicación diaria articular', 'Dra. Silva'),
(1, NOW(), 'Vacunación anual', 'Vacuna polivalente y rabia', 'Dr. Gómez');

-- =====================================================
-- SOLICITUDES DE ADOPCIÓN (2)
-- =====================================================
INSERT IGNORE INTO solicitudes_adopcion (animal_id, adoptante_id, fecha, estado, comentario) VALUES
(8, 2, NOW(), 'APROBADA', 'Parece una familia ideal para Kira, el Border Collie'),
(1, 3, NOW(), 'PENDIENTE', 'Esperando para entrevista personal con el adoptante para Luna');

-- =====================================================
-- TAREAS (3)
-- =====================================================
INSERT IGNORE INTO tareas (descripcion, fecha, estado) VALUES
('Pasear a los perros grandes (Thor y Rocky)', NOW(), 'PENDIENTE'),
('Limpieza profunda de jaulas de gatitos', NOW(), 'COMPLETADA'),
('Administración de medicamentos a Rex y Rocky', NOW(), 'EN_PROCESO');

-- =====================================================
-- VOLUNTARIOS_TAREAS (Join Table N:M)
-- Relaciona las Tareas con los Voluntarios asignados
-- =====================================================
INSERT IGNORE INTO voluntarios_tareas (tarea_id, voluntario_id) VALUES
(1, 1),
(1, 2),
(2, 3),
(3, 1);