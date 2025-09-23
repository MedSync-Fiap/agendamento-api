-- Insert sample users with dynamic password hashing

-- Insert sample admin user
INSERT INTO tb_usuario (id, nome, cpf, email, data_nascimento, senha_hash, role_id, ativo, criado_em, atualizado_em) VALUES 
    ('850e8400-e29b-41d4-a716-446655440001', 'Admin Sistema', '12345678901', 'admin@medsync.com', '1980-01-01', crypt('123456', gen_salt('bf')), '550e8400-e29b-41d4-a716-446655440001', true, NOW(), NOW());

-- Insert sample doctor
INSERT INTO tb_usuario (id, nome, cpf, email, data_nascimento, senha_hash, role_id, ativo, criado_em, atualizado_em) VALUES 
    ('850e8400-e29b-41d4-a716-446655440002', 'Dr. João Silva', '12345678902', 'joao.silva@medsync.com', '1975-05-15', crypt('123456', gen_salt('bf')), '550e8400-e29b-41d4-a716-446655440002', true, NOW(), NOW());

-- Insert sample nurse
INSERT INTO tb_usuario (id, nome, cpf, email, data_nascimento, senha_hash, role_id, ativo, criado_em, atualizado_em) VALUES 
    ('850e8400-e29b-41d4-a716-446655440003', 'Enfermeira Maria Santos', '12345678903', 'maria.santos@medsync.com', '1985-08-20', crypt('123456', gen_salt('bf')), '550e8400-e29b-41d4-a716-446655440003', true, NOW(), NOW());

-- Insert sample patient
INSERT INTO tb_usuario (id, nome, cpf, email, data_nascimento, senha_hash, role_id, ativo, criado_em, atualizado_em) VALUES 
    ('850e8400-e29b-41d4-a716-446655440004', 'Paciente Ana Costa', '12345678904', 'ana.costa@medsync.com', '1990-12-10', crypt('123456', gen_salt('bf')), '550e8400-e29b-41d4-a716-446655440004', true, NOW(), NOW());

-- Assign doctor to cardiology specialty
INSERT INTO tb_especialidade_medico (medico_id, especialidade_id) VALUES 
    ('850e8400-e29b-41d4-a716-446655440002', '750e8400-e29b-41d4-a716-446655440001');

-- Insert sample phone numbers
INSERT INTO tb_usuario_telefone (id, usuario_id, numero, tipo) VALUES 
    ('950e8400-e29b-41d4-a716-446655440001', '850e8400-e29b-41d4-a716-446655440001', '11999999999', 'CELULAR'),
    ('950e8400-e29b-41d4-a716-446655440002', '850e8400-e29b-41d4-a716-446655440002', '11999999998', 'CELULAR'),
    ('950e8400-e29b-41d4-a716-446655440003', '850e8400-e29b-41d4-a716-446655440003', '11999999997', 'CELULAR'),
    ('950e8400-e29b-41d4-a716-446655440004', '850e8400-e29b-41d4-a716-446655440004', '11999999996', 'CELULAR');
