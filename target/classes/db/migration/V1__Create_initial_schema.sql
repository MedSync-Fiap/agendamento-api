-- Create initial schema for MedSync Cadastro + Agendamento Service

-- Create roles table
CREATE TABLE tb_role (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(50) NOT NULL UNIQUE,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create permissions table
CREATE TABLE tb_permissao (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(100) NOT NULL UNIQUE,
    descricao VARCHAR(255),
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create role-permission relationship table
CREATE TABLE tb_role_permissao (
    role_id UUID NOT NULL,
    permissao_id UUID NOT NULL,
    PRIMARY KEY (role_id, permissao_id),
    FOREIGN KEY (role_id) REFERENCES tb_role(id) ON DELETE CASCADE,
    FOREIGN KEY (permissao_id) REFERENCES tb_permissao(id) ON DELETE CASCADE
);

-- Create users table
CREATE TABLE tb_usuario (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(255) NOT NULL,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    role_id UUID NOT NULL,
    ativo BOOLEAN DEFAULT true,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES tb_role(id)
);

-- Create user phone table
CREATE TABLE tb_usuario_telefone (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id UUID NOT NULL,
    numero VARCHAR(20) NOT NULL,
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('CELULAR', 'FIXO', 'WHATSAPP')),
    FOREIGN KEY (usuario_id) REFERENCES tb_usuario(id) ON DELETE CASCADE
);

-- Create specialties table
CREATE TABLE tb_especialidade (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(100) NOT NULL UNIQUE,
    descricao VARCHAR(255),
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create doctor-specialty relationship table
CREATE TABLE tb_especialidade_medico (
    medico_id UUID NOT NULL,
    especialidade_id UUID NOT NULL,
    PRIMARY KEY (medico_id, especialidade_id),
    FOREIGN KEY (medico_id) REFERENCES tb_usuario(id) ON DELETE CASCADE,
    FOREIGN KEY (especialidade_id) REFERENCES tb_especialidade(id) ON DELETE CASCADE
);

-- Create consultations table
CREATE TABLE tb_consulta (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    paciente_id UUID NOT NULL,
    medico_id UUID NOT NULL,
    criado_por_id UUID NOT NULL,
    data_hora TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'AGENDADA' CHECK (status IN ('AGENDADA', 'CONFIRMADA', 'CANCELADA', 'REALIZADA', 'FALTA')),
    observacoes TEXT,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (paciente_id) REFERENCES tb_usuario(id),
    FOREIGN KEY (medico_id) REFERENCES tb_usuario(id),
    FOREIGN KEY (criado_por_id) REFERENCES tb_usuario(id)
);

-- Create indexes for better performance
CREATE INDEX idx_usuario_cpf ON tb_usuario(cpf);
CREATE INDEX idx_usuario_email ON tb_usuario(email);
CREATE INDEX idx_usuario_role ON tb_usuario(role_id);
CREATE INDEX idx_consulta_paciente ON tb_consulta(paciente_id);
CREATE INDEX idx_consulta_medico ON tb_consulta(medico_id);
CREATE INDEX idx_consulta_data_hora ON tb_consulta(data_hora);
CREATE INDEX idx_consulta_status ON tb_consulta(status);
