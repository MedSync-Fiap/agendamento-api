-- Insert sample users with proper password hashing for testing
-- All passwords are hashed using bcrypt with salt

-- Insert sample admin user (password: admin123)
INSERT INTO tb_usuario (id, nome, cpf, email, data_nascimento, senha_hash, role_id, ativo, criado_em, atualizado_em) VALUES 
    ('850e8400-e29b-41d4-a716-446655440001', 'Admin Sistema', '12345678901', 'admin@medsync.com', '1980-01-01', crypt('admin123', gen_salt('bf')), '550e8400-e29b-41d4-a716-446655440001', true, NOW(), NOW());

-- Insert sample doctor (password: medico123)
INSERT INTO tb_usuario (id, nome, cpf, email, data_nascimento, senha_hash, role_id, ativo, criado_em, atualizado_em) VALUES 
    ('850e8400-e29b-41d4-a716-446655440002', 'Dr. João Silva', '12345678902', 'joao.silva@medsync.com', '1975-05-15', crypt('medico123', gen_salt('bf')), '550e8400-e29b-41d4-a716-446655440002', true, NOW(), NOW());

-- Insert sample nurse (password: enfermeiro123)
INSERT INTO tb_usuario (id, nome, cpf, email, data_nascimento, senha_hash, role_id, ativo, criado_em, atualizado_em) VALUES 
    ('850e8400-e29b-41d4-a716-446655440003', 'Enfermeira Maria Santos', '12345678903', 'maria.santos@medsync.com', '1985-08-20', crypt('enfermeiro123', gen_salt('bf')), '550e8400-e29b-41d4-a716-446655440003', true, NOW(), NOW());

-- Insert sample patient (password: paciente123)
INSERT INTO tb_usuario (id, nome, cpf, email, data_nascimento, senha_hash, role_id, ativo, criado_em, atualizado_em) VALUES 
    ('850e8400-e29b-41d4-a716-446655440004', 'Paciente Ana Costa', '12345678904', 'ana.costa@medsync.com', '1990-12-10', crypt('paciente123', gen_salt('bf')), '550e8400-e29b-41d4-a716-446655440004', true, NOW(), NOW());

-- Insert additional sample doctor (password: medico123)
INSERT INTO tb_usuario (id, nome, cpf, email, data_nascimento, senha_hash, role_id, ativo, criado_em, atualizado_em) VALUES 
    ('850e8400-e29b-41d4-a716-446655440005', 'Dr. Maria Oliveira', '12345678905', 'maria.oliveira@medsync.com', '1980-03-20', crypt('medico123', gen_salt('bf')), '550e8400-e29b-41d4-a716-446655440002', true, NOW(), NOW());

-- Insert additional sample nurse (password: enfermeiro123)
INSERT INTO tb_usuario (id, nome, cpf, email, data_nascimento, senha_hash, role_id, ativo, criado_em, atualizado_em) VALUES 
    ('850e8400-e29b-41d4-a716-446655440006', 'Enfermeiro Carlos Santos', '12345678906', 'carlos.santos@medsync.com', '1988-07-15', crypt('enfermeiro123', gen_salt('bf')), '550e8400-e29b-41d4-a716-446655440003', true, NOW(), NOW());

-- Insert additional sample patient (password: paciente123)
INSERT INTO tb_usuario (id, nome, cpf, email, data_nascimento, senha_hash, role_id, ativo, criado_em, atualizado_em) VALUES 
    ('850e8400-e29b-41d4-a716-446655440007', 'Paciente Pedro Silva', '12345678907', 'pedro.silva@medsync.com', '1995-11-30', crypt('paciente123', gen_salt('bf')), '550e8400-e29b-41d4-a716-446655440004', true, NOW(), NOW());

-- Insert another sample patient (password: paciente123)
INSERT INTO tb_usuario (id, nome, cpf, email, data_nascimento, senha_hash, role_id, ativo, criado_em, atualizado_em) VALUES 
    ('850e8400-e29b-41d4-a716-446655440008', 'Paciente Julia Costa', '12345678908', 'julia.costa@medsync.com', '1992-04-12', crypt('paciente123', gen_salt('bf')), '550e8400-e29b-41d4-a716-446655440004', true, NOW(), NOW());

-- Assign doctors to specialties
INSERT INTO tb_especialidade_medico (medico_id, especialidade_id) VALUES 
    ('850e8400-e29b-41d4-a716-446655440002', '750e8400-e29b-41d4-a716-446655440001'), -- Dr. João Silva - Cardiologia
    ('850e8400-e29b-41d4-a716-446655440005', '750e8400-e29b-41d4-a716-446655440001'); -- Dr. Maria Oliveira - Cardiologia

-- Insert phone numbers for all users
INSERT INTO tb_usuario_telefone (id, usuario_id, numero, tipo) VALUES 
    ('950e8400-e29b-41d4-a716-446655440001', '850e8400-e29b-41d4-a716-446655440001', '11999999999', 'CELULAR'),
    ('950e8400-e29b-41d4-a716-446655440002', '850e8400-e29b-41d4-a716-446655440002', '11999999998', 'CELULAR'),
    ('950e8400-e29b-41d4-a716-446655440003', '850e8400-e29b-41d4-a716-446655440003', '11999999997', 'CELULAR'),
    ('950e8400-e29b-41d4-a716-446655440004', '850e8400-e29b-41d4-a716-446655440004', '11999999996', 'CELULAR'),
    ('950e8400-e29b-41d4-a716-446655440005', '850e8400-e29b-41d4-a716-446655440005', '11999999995', 'CELULAR'),
    ('950e8400-e29b-41d4-a716-446655440006', '850e8400-e29b-41d4-a716-446655440006', '11999999994', 'CELULAR'),
    ('950e8400-e29b-41d4-a716-446655440007', '850e8400-e29b-41d4-a716-446655440007', '11999999993', 'CELULAR'),
    ('950e8400-e29b-41d4-a716-446655440008', '850e8400-e29b-41d4-a716-446655440008', '11999999992', 'CELULAR');
