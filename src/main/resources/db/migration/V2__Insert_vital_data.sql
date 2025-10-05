-- Insert roles
INSERT INTO tb_role (id, nome, descricao, tipo) VALUES 
    ('550e8400-e29b-41d4-a716-446655440001', 'ADMIN', 'Administrador do sistema com acesso total', 'ADMIN'),
    ('550e8400-e29b-41d4-a716-446655440002', 'MEDICO', 'Médico com acesso a consultas e histórico', 'MEDICO'),
    ('550e8400-e29b-41d4-a716-446655440003', 'ENFERMEIRO', 'Enfermeiro com acesso limitado a consultas', 'ENFERMEIRO'),
    ('550e8400-e29b-41d4-a716-446655440004', 'PACIENTE', 'Paciente com acesso apenas às próprias consultas', 'PACIENTE');

-- Insert permissions
INSERT INTO tb_permissao (id, nome, descricao) VALUES 
    ('650e8400-e29b-41d4-a716-446655440001', 'CRIAR_CONSULTA', 'Permite criar novas consultas'),
    ('650e8400-e29b-41d4-a716-446655440002', 'EDITAR_CONSULTA', 'Permite editar consultas existentes'),
    ('650e8400-e29b-41d4-a716-446655440003', 'VISUALIZAR_CONSULTAS', 'Permite visualizar consultas'),
    ('650e8400-e29b-41d4-a716-446655440004', 'EXCLUIR_CONSULTA', 'Permite excluir consultas'),
    ('650e8400-e29b-41d4-a716-446655440005', 'VISUALIZAR_HISTORICO', 'Permite visualizar histórico de pacientes'),
    ('650e8400-e29b-41d4-a716-446655440006', 'EDITAR_HISTORICO', 'Permite editar histórico de pacientes'),
    ('650e8400-e29b-41d4-a716-446655440007', 'VISUALIZAR_USUARIOS', 'Permite visualizar lista de usuários'),
    ('650e8400-e29b-41d4-a716-446655440008', 'CRIAR_USUARIO', 'Permite criar novos usuários'),
    ('650e8400-e29b-41d4-a716-446655440009', 'EDITAR_USUARIO', 'Permite editar usuários existentes'),
    ('650e8400-e29b-41d4-a716-446655440010', 'EXCLUIR_USUARIO', 'Permite excluir usuários');

-- Assign permissions to ADMIN role (all permissions)
INSERT INTO tb_role_permissao (role_id, permissao_id) VALUES 
    ('550e8400-e29b-41d4-a716-446655440001', '650e8400-e29b-41d4-a716-446655440001'),
    ('550e8400-e29b-41d4-a716-446655440001', '650e8400-e29b-41d4-a716-446655440002'),
    ('550e8400-e29b-41d4-a716-446655440001', '650e8400-e29b-41d4-a716-446655440003'),
    ('550e8400-e29b-41d4-a716-446655440001', '650e8400-e29b-41d4-a716-446655440004'),
    ('550e8400-e29b-41d4-a716-446655440001', '650e8400-e29b-41d4-a716-446655440005'),
    ('550e8400-e29b-41d4-a716-446655440001', '650e8400-e29b-41d4-a716-446655440006'),
    ('550e8400-e29b-41d4-a716-446655440001', '650e8400-e29b-41d4-a716-446655440007'),
    ('550e8400-e29b-41d4-a716-446655440001', '650e8400-e29b-41d4-a716-446655440008'),
    ('550e8400-e29b-41d4-a716-446655440001', '650e8400-e29b-41d4-a716-446655440009'),
    ('550e8400-e29b-41d4-a716-446655440001', '650e8400-e29b-41d4-a716-446655440010');

-- Assign permissions to MEDICO role
INSERT INTO tb_role_permissao (role_id, permissao_id) VALUES 
    ('550e8400-e29b-41d4-a716-446655440002', '650e8400-e29b-41d4-a716-446655440001'),
    ('550e8400-e29b-41d4-a716-446655440002', '650e8400-e29b-41d4-a716-446655440002'),
    ('550e8400-e29b-41d4-a716-446655440002', '650e8400-e29b-41d4-a716-446655440003'),
    ('550e8400-e29b-41d4-a716-446655440002', '650e8400-e29b-41d4-a716-446655440005'),
    ('550e8400-e29b-41d4-a716-446655440002', '650e8400-e29b-41d4-a716-446655440006');

-- Assign permissions to ENFERMEIRO role
INSERT INTO tb_role_permissao (role_id, permissao_id) VALUES 
    ('550e8400-e29b-41d4-a716-446655440003', '650e8400-e29b-41d4-a716-446655440001'),
    ('550e8400-e29b-41d4-a716-446655440003', '650e8400-e29b-41d4-a716-446655440003'),
    ('550e8400-e29b-41d4-a716-446655440003', '650e8400-e29b-41d4-a716-446655440005');

-- Assign permissions to PACIENTE role
INSERT INTO tb_role_permissao (role_id, permissao_id) VALUES 
    ('550e8400-e29b-41d4-a716-446655440004', '650e8400-e29b-41d4-a716-446655440003');

-- Insert specialties
INSERT INTO tb_especialidade (id, nome, descricao) VALUES 
    ('750e8400-e29b-41d4-a716-446655440001', 'Cardiologia', 'Especialidade médica que trata do coração e sistema cardiovascular'),
    ('750e8400-e29b-41d4-a716-446655440002', 'Dermatologia', 'Especialidade médica que trata da pele, cabelos e unhas'),
    ('750e8400-e29b-41d4-a716-446655440003', 'Pediatria', 'Especialidade médica que trata de crianças e adolescentes'),
    ('750e8400-e29b-41d4-a716-446655440004', 'Ginecologia', 'Especialidade médica que trata da saúde da mulher'),
    ('750e8400-e29b-41d4-a716-446655440005', 'Ortopedia', 'Especialidade médica que trata do sistema musculoesquelético'),
    ('750e8400-e29b-41d4-a716-446655440006', 'Neurologia', 'Especialidade médica que trata do sistema nervoso'),
    ('750e8400-e29b-41d4-a716-446655440007', 'Psiquiatria', 'Especialidade médica que trata de transtornos mentais'),
    ('750e8400-e29b-41d4-a716-446655440008', 'Oftalmologia', 'Especialidade médica que trata dos olhos e visão'),
    ('750e8400-e29b-41d4-a716-446655440009', 'Clínica Médica', 'Especialidade médica que trata de doenças internas em adultos'),
    ('750e8400-e29b-41d4-a716-446655440010', 'Cirurgia Geral', 'Especialidade médica que realiza procedimentos cirúrgicos gerais'),
    ('750e8400-e29b-41d4-a716-446655440011', 'Urologia', 'Especialidade médica que trata do sistema urinário e reprodutor masculino'),
    ('750e8400-e29b-41d4-a716-446655440012', 'Endocrinologia', 'Especialidade médica que trata de distúrbios hormonais e metabólicos'),
    ('750e8400-e29b-41d4-a716-446655440013', 'Gastroenterologia', 'Especialidade médica que trata do sistema digestivo'),
    ('750e8400-e29b-41d4-a716-446655440014', 'Pneumologia', 'Especialidade médica que trata do sistema respiratório'),
    ('750e8400-e29b-41d4-a716-446655440015', 'Reumatologia', 'Especialidade médica que trata de doenças reumáticas e autoimunes'),
    ('750e8400-e29b-41d4-a716-446655440016', 'Hematologia', 'Especialidade médica que trata de doenças do sangue'),
    ('750e8400-e29b-41d4-a716-446655440017', 'Infectologia', 'Especialidade médica que trata de doenças infecciosas'),
    ('750e8400-e29b-41d4-a716-446655440018', 'Nefrologia', 'Especialidade médica que trata dos rins e sistema urinário'),
    ('750e8400-e29b-41d4-a716-446655440019', 'Oncologia', 'Especialidade médica que trata de câncer e tumores'),
    ('750e8400-e29b-41d4-a716-446655440020', 'Geriatria', 'Especialidade médica que trata de idosos e envelhecimento'),
    ('750e8400-e29b-41d4-a716-446655440021', 'Medicina do Trabalho', 'Especialidade médica que trata da saúde no ambiente de trabalho'),
    ('750e8400-e29b-41d4-a716-446655440022', 'Medicina Esportiva', 'Especialidade médica que trata da saúde de atletas e exercícios'),
    ('750e8400-e29b-41d4-a716-446655440023', 'Medicina Preventiva', 'Especialidade médica focada na prevenção de doenças'),
    ('750e8400-e29b-41d4-a716-446655440024', 'Anestesiologia', 'Especialidade médica responsável pela anestesia em procedimentos'),
    ('750e8400-e29b-41d4-a716-446655440025', 'Radiologia', 'Especialidade médica que utiliza imagens para diagnóstico'),
    ('750e8400-e29b-41d4-a716-446655440026', 'Patologia', 'Especialidade médica que estuda doenças através de exames'),
    ('750e8400-e29b-41d4-a716-446655440027', 'Medicina Legal', 'Especialidade médica que aplica conhecimentos médicos na justiça'),
    ('750e8400-e29b-41d4-a716-446655440028', 'Medicina de Família', 'Especialidade médica que oferece cuidados primários à família');
