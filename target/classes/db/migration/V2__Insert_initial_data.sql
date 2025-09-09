-- Insert initial data for MedSync system

-- Insert roles
INSERT INTO tb_role (id, nome) VALUES 
    ('550e8400-e29b-41d4-a716-446655440001', 'ADMIN'),
    ('550e8400-e29b-41d4-a716-446655440002', 'MEDICO'),
    ('550e8400-e29b-41d4-a716-446655440003', 'ENFERMEIRO'),
    ('550e8400-e29b-41d4-a716-446655440004', 'PACIENTE');

-- Insert permissions
INSERT INTO tb_permissao (id, nome, descricao) VALUES 
    ('650e8400-e29b-41d4-a716-446655440001', 'CRIAR_CONSULTA', 'Permite criar novas consultas'),
    ('650e8400-e29b-41d4-a716-446655440002', 'EDITAR_CONSULTA', 'Permite editar consultas existentes'),
    ('650e8400-e29b-41d4-a716-446655440003', 'CANCELAR_CONSULTA', 'Permite cancelar consultas'),
    ('650e8400-e29b-41d4-a716-446655440004', 'VISUALIZAR_HISTORICO', 'Permite visualizar histórico de consultas'),
    ('650e8400-e29b-41d4-a716-446655440005', 'GERENCIAR_USUARIOS', 'Permite gerenciar usuários do sistema'),
    ('650e8400-e29b-41d4-a716-446655440006', 'GERENCIAR_ESPECIALIDADES', 'Permite gerenciar especialidades médicas'),
    ('650e8400-e29b-41d4-a716-446655440007', 'VISUALIZAR_CONSULTAS', 'Permite visualizar consultas'),
    ('650e8400-e29b-41d4-a716-446655440008', 'CONFIRMAR_CONSULTA', 'Permite confirmar consultas');

-- Assign permissions to roles
-- ADMIN - all permissions
INSERT INTO tb_role_permissao (role_id, permissao_id) VALUES 
    ('550e8400-e29b-41d4-a716-446655440001', '650e8400-e29b-41d4-a716-446655440001'),
    ('550e8400-e29b-41d4-a716-446655440001', '650e8400-e29b-41d4-a716-446655440002'),
    ('550e8400-e29b-41d4-a716-446655440001', '650e8400-e29b-41d4-a716-446655440003'),
    ('550e8400-e29b-41d4-a716-446655440001', '650e8400-e29b-41d4-a716-446655440004'),
    ('550e8400-e29b-41d4-a716-446655440001', '650e8400-e29b-41d4-a716-446655440005'),
    ('550e8400-e29b-41d4-a716-446655440001', '650e8400-e29b-41d4-a716-446655440006'),
    ('550e8400-e29b-41d4-a716-446655440001', '650e8400-e29b-41d4-a716-446655440007'),
    ('550e8400-e29b-41d4-a716-446655440001', '650e8400-e29b-41d4-a716-446655440008');

-- MEDICO - consultation related permissions
INSERT INTO tb_role_permissao (role_id, permissao_id) VALUES 
    ('550e8400-e29b-41d4-a716-446655440002', '650e8400-e29b-41d4-a716-446655440002'),
    ('550e8400-e29b-41d4-a716-446655440002', '650e8400-e29b-41d4-a716-446655440004'),
    ('550e8400-e29b-41d4-a716-446655440002', '650e8400-e29b-41d4-a716-446655440007'),
    ('550e8400-e29b-41d4-a716-446655440002', '650e8400-e29b-41d4-a716-446655440008');

-- ENFERMEIRO - can create and manage consultations
INSERT INTO tb_role_permissao (role_id, permissao_id) VALUES 
    ('550e8400-e29b-41d4-a716-446655440003', '650e8400-e29b-41d4-a716-446655440001'),
    ('550e8400-e29b-41d4-a716-446655440003', '650e8400-e29b-41d4-a716-446655440002'),
    ('550e8400-e29b-41d4-a716-446655440003', '650e8400-e29b-41d4-a716-446655440003'),
    ('550e8400-e29b-41d4-a716-446655440003', '650e8400-e29b-41d4-a716-446655440004'),
    ('550e8400-e29b-41d4-a716-446655440003', '650e8400-e29b-41d4-a716-446655440007');

-- PACIENTE - can only view their own consultations
INSERT INTO tb_role_permissao (role_id, permissao_id) VALUES 
    ('550e8400-e29b-41d4-a716-446655440004', '650e8400-e29b-41d4-a716-446655440004'),
    ('550e8400-e29b-41d4-a716-446655440004', '650e8400-e29b-41d4-a716-446655440007');

-- Insert medical specialties
INSERT INTO tb_especialidade (id, nome, descricao) VALUES 
    ('750e8400-e29b-41d4-a716-446655440001', 'Cardiologia', 'Especialidade médica que trata do coração e sistema cardiovascular'),
    ('750e8400-e29b-41d4-a716-446655440002', 'Dermatologia', 'Especialidade médica que trata da pele, cabelos e unhas'),
    ('750e8400-e29b-41d4-a716-446655440003', 'Endocrinologia', 'Especialidade médica que trata de hormônios e metabolismo'),
    ('750e8400-e29b-41d4-a716-446655440004', 'Ginecologia', 'Especialidade médica que trata da saúde da mulher'),
    ('750e8400-e29b-41d4-a716-446655440005', 'Neurologia', 'Especialidade médica que trata do sistema nervoso'),
    ('750e8400-e29b-41d4-a716-446655440006', 'Ortopedia', 'Especialidade médica que trata de ossos, músculos e articulações'),
    ('750e8400-e29b-41d4-a716-446655440007', 'Pediatria', 'Especialidade médica que trata de crianças e adolescentes'),
    ('750e8400-e29b-41d4-a716-446655440008', 'Psiquiatria', 'Especialidade médica que trata de transtornos mentais');
