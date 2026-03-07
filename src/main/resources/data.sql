-- =========================
-- voluntarios (5)
-- =========================
INSERT INTO voluntarios
(nombre, apellido, especialidad, email, telefono, created_at)
VALUES
('Lucía', 'García', 'Cuidado de felinos', 'lucia.garcia@refugio.local', '+34600111222', NOW()),
('Carlos', 'Martín', 'Comportamiento canino', 'carlos.martin@refugio.local', '+34600222333', NOW()),
('Marta', 'López', 'Auxiliar veterinaria', 'marta.lopez@refugio.local', '+34600333444', NOW()),
('Javier', 'Ruiz', 'Rescate y transporte', 'javier.ruiz@refugio.local', '+34600444555', NOW()),
('Ana', 'Santos', 'Gestión de adopciones', 'ana.santos@refugio.local', '+34600555666', NOW());

-- animales (5)
INSERT INTO animales 
(nombre, especie, raza, sexo, chip_id, estado, created_at) VALUES 
('Luna', 'Perro', 'Labrador', 'Hembra', 'CHIP001', 'Disponible', NOW()),
('Simba', 'Gato', 'Común', 'Macho', 'CHIP002', 'Disponible', NOW()),
('Rex', 'Perro', 'Pastor Alemán', 'Macho', 'CHIP003', 'En Tratamiento', NOW()),
('Bella', 'Perro', 'Galgp', 'Hembra', 'CHIP004', 'Disponible', NOW()),
('Nala', 'Gato', 'Siamés', 'Hembra', 'CHIP005', 'Adoptado', NOW());

-- PERSONAS / ADOPTANTES (20)
INSERT INTO personas
(dni, nombre, apellido, email, telefono, direccion, fecha_nacimiento, created_at) VALUES
('12345678A', 'Diego', 'Romero', 'diego.romero@local', '600000001', 'C/ Sol 1, Madrid', '2001-03-10', NOW()),
('23456789B', 'Lucía', 'Martínez', 'lucia.martinez@local', '600000002', 'C/ Luna 2, Sevilla', '2003-07-22', NOW());

-- adopciones
INSERT INTO adopciones (id_persona, id_animal, created_at) VALUES
(1, 1, NOW()),
(2, 2, NOW());


