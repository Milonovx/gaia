UPDATE mascotas SET tipo_edad = 'ANIOS' WHERE tipo_edad IS NULL OR tipo_edad = '';
UPDATE mascotas SET tipo_edad = 'ANIOS' WHERE UPPER(tipo_edad) IN ('AÑOS', 'ANOS');
UPDATE mascotas SET tipo_edad = 'MESES' WHERE UPPER(tipo_edad) = 'MESES';
UPDATE mascotas SET tamano = 'PEQUENO' WHERE UPPER(tamano) IN ('PEQUEÑO', 'PEQUENO');
UPDATE mascotas SET tamano = 'MEDIANO' WHERE UPPER(tamano) = 'MEDIANO';
UPDATE mascotas SET tamano = 'GRANDE' WHERE UPPER(tamano) = 'GRANDE';
UPDATE mascotas SET genero = 'MACHO' WHERE UPPER(genero) = 'MACHO';
UPDATE mascotas SET genero = 'HEMBRA' WHERE UPPER(genero) = 'HEMBRA';

UPDATE solicitudes_ingreso_mascota SET tipo_edad = 'ANIOS' WHERE tipo_edad IS NULL OR tipo_edad = '';
UPDATE solicitudes_ingreso_mascota SET tipo_edad = 'ANIOS' WHERE UPPER(tipo_edad) IN ('AÑOS', 'ANOS');
UPDATE solicitudes_ingreso_mascota SET tipo_edad = 'MESES' WHERE UPPER(tipo_edad) = 'MESES';
UPDATE solicitudes_ingreso_mascota SET tamano = 'PEQUENO' WHERE UPPER(tamano) IN ('PEQUEÑO', 'PEQUENO');
UPDATE solicitudes_ingreso_mascota SET tamano = 'MEDIANO' WHERE UPPER(tamano) = 'MEDIANO';
UPDATE solicitudes_ingreso_mascota SET tamano = 'GRANDE' WHERE UPPER(tamano) = 'GRANDE';
UPDATE solicitudes_ingreso_mascota SET genero = 'MACHO' WHERE UPPER(genero) = 'MACHO';
UPDATE solicitudes_ingreso_mascota SET genero = 'HEMBRA' WHERE UPPER(genero) = 'HEMBRA';
