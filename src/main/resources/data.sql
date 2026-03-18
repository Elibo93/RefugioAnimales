-- =====================================================
-- PERSONAS / ADOPTANTES (10)
-- =====================================================
INSERT INTO personas
(dni, nombre, apellido, email, telefono, direccion, fecha_nacimiento, created_at) VALUES
('12345678A', 'Diego', 'Romero', 'diego.romero@local', '600000001', 'C/ Sol 1, Madrid', '2001-03-10', NOW()),
('23456789B', 'Lucía', 'Martínez', 'lucia.martinez@local', '600000002', 'C/ Luna 2, Sevilla', '2003-07-22', NOW()),
('34567890C', 'Mario', 'Gómez', 'mario.gomez@local', '600000003', 'C/ Norte 5, Valencia', '1998-11-15', NOW()),
('45678901D', 'Laura', 'Sánchez', 'laura.sanchez@local', '600000004', 'C/ Sur 8, Málaga', '2000-02-03', NOW()),
('56789012E', 'Pablo', 'Díaz', 'pablo.diaz@local', '600000005', 'Av. Mar 12, Cádiz', '1997-06-18', NOW()),
('67890123F', 'Sara', 'Moreno', 'sara.moreno@local', '600000006', 'C/ Prado 9, Madrid', '2002-09-25', NOW()),
('78901234G', 'Alberto', 'Ruiz', 'alberto.ruiz@local', '600000007', 'C/ Río 7, Zaragoza', '1999-04-11', NOW()),
('89012345H', 'Clara', 'Navarro', 'clara.navarro@local', '600000008', 'C/ Sierra 14, Granada', '2001-12-30', NOW()),
('90123456J', 'David', 'Torres', 'david.torres@local', '600000009', 'C/ Parque 3, Bilbao', '1996-01-20', NOW()),
('01234567K', 'Irene', 'Castro', 'irene.castro@local', '600000010', 'C/ Jardín 6, Madrid', '2004-05-09', NOW());


-- =====================================================
-- VOLUNTARIOS (7)
-- =====================================================
INSERT INTO voluntarios
(nombre, apellido, especialidad, email, telefono, created_at)
VALUES
('Lucía', 'García', 'Cuidado de felinos', 'lucia.garcia@refugio.local', '+34600111222', NOW()),
('Carlos', 'Martín', 'Comportamiento canino', 'carlos.martin@refugio.local', '+34600222333', NOW()),
('Marta', 'López', 'Auxiliar veterinaria', 'marta.lopez@refugio.local', '+34600333444', NOW()),
('Javier', 'Ruiz', 'Rescate y transporte', 'javier.ruiz@refugio.local', '+34600444555', NOW()),
('Ana', 'Santos', 'Gestión de adopciones', 'ana.santos@refugio.local', '+34600555666', NOW()),
('Pedro', 'Gil', 'Entrenamiento básico', 'pedro.gil@refugio.local', '+34600666777', NOW()),
('Elena', 'Vega', 'Cuidados veterinarios', 'elena.vega@refugio.local', '+34600777888', NOW());


-- =====================================================
-- ANIMALES (10)
-- =====================================================
INSERT INTO animales
(nombre, especie, raza, sexo, chip_id, estado, edad, tamano, descripcion, foto, fecha_ingreso) VALUES
('Luna', 'Perro', 'Labrador', 'Hembra', 'CHIP001', 'Disponible', 3, 'Mediano', 'Perrita juiciosa', 'http://example.com/foto1.jpg', NOW()),
('Simba', 'Gato', 'Común', 'Macho', 'CHIP002', 'Disponible', 2, 'Pequeño', 'Gato dormilon', 'http://example.com/foto2.jpg', NOW()),
('Rex', 'Perro', 'Pastor Alemán', 'Macho', 'CHIP003', 'En Tratamiento', 5, 'Grande', 'Buen guardian', 'http://example.com/foto3.jpg', NOW()),
('Bella', 'Perro', 'Galgo', 'Hembra', 'CHIP004', 'Disponible', 4, 'Grande', 'Corre muy rapido', 'http://example.com/foto4.jpg', NOW()),
('Nala', 'Gato', 'Siamés', 'Hembra', 'CHIP005', 'Adoptado', 1, 'Pequeño', 'Muy curiosa', 'http://example.com/foto5.jpg', NOW()),
('Thor', 'Perro', 'Husky', 'Macho', 'CHIP006', 'Disponible', 3, 'Grande', 'Le encanta la nieve', 'http://example.com/foto6.jpg', NOW()),
('Milo', 'Gato', 'Europeo', 'Macho', 'CHIP007', 'Disponible', 4, 'Mediano', 'Le gusta cazar ratones', 'http://example.com/foto7.jpg', NOW()),
('Kira', 'Perro', 'Border Collie', 'Hembra', 'CHIP008', 'Disponible', 2, 'Mediano', 'Muy inteligente', 'http://example.com/foto8.jpg', NOW()),
('Coco', 'Gato', 'Persa', 'Hembra', 'CHIP009', 'En Tratamiento', 5, 'Pequeño', 'Requiere cuidado de pelaje', 'http://example.com/foto9.jpg', NOW()),
('Rocky', 'Perro', 'Boxer', 'Macho', 'CHIP010', 'Disponible', 4, 'Grande', 'Muy fuerte y leal', 'http://example.com/foto10.jpg', NOW());


-- =====================================================
-- ADOPCIONES
-- =====================================================
INSERT INTO adopciones (id_persona, id_animal, created_at) VALUES
(1, 1, NOW()),
(2, 2, NOW()),
(3, 6, NOW()),
(4, 7, NOW()),
(5, 8, NOW());