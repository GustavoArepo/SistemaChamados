-- ==========================================
-- Script de Criação do Banco de Dados MySQL
-- Sistema de Chamados - Hostinger
-- Banco: u718816089_HelpDeskDB
-- ==========================================

-- Usar o banco de dados
USE u718816089_HelpDeskDB;

-- ==========================================
-- TABELA: Usuarios
-- ==========================================
CREATE TABLE IF NOT EXISTS Usuarios (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    Nome VARCHAR(100) NOT NULL,
    Email VARCHAR(100) NOT NULL UNIQUE,
    Senha VARCHAR(255) NOT NULL,
    DataCadastro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_email (Email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==========================================
-- TABELA: Chamados
-- ==========================================
CREATE TABLE IF NOT EXISTS Chamados (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    Titulo VARCHAR(200) NOT NULL,
    Descricao TEXT NOT NULL,
    UsuarioId INT NOT NULL,
    Status VARCHAR(50) NOT NULL DEFAULT 'Aberto',
    DataAbertura DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    DataFechamento DATETIME NULL,
    INDEX idx_usuario (UsuarioId),
    INDEX idx_status (Status),
    INDEX idx_data_abertura (DataAbertura),
    CONSTRAINT fk_chamados_usuario
        FOREIGN KEY (UsuarioId)
        REFERENCES Usuarios(Id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==========================================
-- TABELA: Mensagens
-- ==========================================
CREATE TABLE IF NOT EXISTS Mensagens (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    ChamadoId INT NOT NULL,
    UsuarioId INT NOT NULL,
    Texto TEXT NOT NULL,
    DataEnvio DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    EhAtendente BOOLEAN NOT NULL DEFAULT FALSE,
    Lida BOOLEAN NOT NULL DEFAULT FALSE,
    INDEX idx_chamado (ChamadoId),
    INDEX idx_usuario (UsuarioId),
    INDEX idx_lida (Lida),
    INDEX idx_data_envio (DataEnvio),
    CONSTRAINT fk_mensagens_chamado
        FOREIGN KEY (ChamadoId)
        REFERENCES Chamados(Id)
        ON DELETE CASCADE,
    CONSTRAINT fk_mensagens_usuario
        FOREIGN KEY (UsuarioId)
        REFERENCES Usuarios(Id)
        ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==========================================
-- TABELA: ChamadoImagens
-- ==========================================
CREATE TABLE IF NOT EXISTS ChamadoImagens (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    ChamadoId INT NOT NULL,
    NomeArquivo VARCHAR(255) NOT NULL,
    CaminhoArquivo VARCHAR(500) NOT NULL,
    DataUpload DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_chamado (ChamadoId),
    INDEX idx_data_upload (DataUpload),
    CONSTRAINT fk_imagens_chamado
        FOREIGN KEY (ChamadoId)
        REFERENCES Chamados(Id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==========================================
-- TABELA: Comentarios
-- ==========================================
CREATE TABLE IF NOT EXISTS Comentarios (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    ChamadoId INT NOT NULL,
    Texto TEXT NOT NULL,
    DataComentario DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_chamado (ChamadoId),
    INDEX idx_data_comentario (DataComentario),
    CONSTRAINT fk_comentarios_chamado
        FOREIGN KEY (ChamadoId)
        REFERENCES Chamados(Id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==========================================
-- DADOS INICIAIS: Usuário Admin de Teste
-- ==========================================
INSERT INTO Usuarios (Nome, Email, Senha, DataCadastro)
VALUES ('Administrador', 'admin@helpdesk.com', '12345678', NOW())
ON DUPLICATE KEY UPDATE Nome = Nome; -- Evita erro se já existir

-- ==========================================
-- DADOS DE TESTE (OPCIONAL)
-- ==========================================

-- Criar usuário de teste adicional
INSERT INTO Usuarios (Nome, Email, Senha, DataCadastro)
VALUES ('Usuário Teste', 'teste@helpdesk.com', 'senha123', NOW())
ON DUPLICATE KEY UPDATE Nome = Nome;

-- Criar chamado de exemplo
INSERT INTO Chamados (Titulo, Descricao, UsuarioId, Status, DataAbertura)
SELECT
    'Chamado de Teste - Sistema Funcionando',
    'Este é um chamado de teste criado automaticamente para verificar se o sistema está funcionando corretamente.',
    1,
    'Aberto',
    NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM Chamados WHERE Titulo LIKE 'Chamado de Teste%');

-- Criar mensagem de exemplo
INSERT INTO Mensagens (ChamadoId, UsuarioId, Texto, DataEnvio, EhAtendente, Lida)
SELECT
    1,
    1,
    'Bem-vindo ao sistema de chamados! Esta é uma mensagem de teste.',
    NOW(),
    FALSE,
    FALSE
FROM DUAL
WHERE EXISTS (SELECT 1 FROM Chamados WHERE Id = 1)
  AND NOT EXISTS (SELECT 1 FROM Mensagens WHERE ChamadoId = 1);

-- ==========================================
-- VERIFICAÇÃO E RELATÓRIO
-- ==========================================

-- Mostrar tabelas criadas
SHOW TABLES;

-- Contar registros em cada tabela
SELECT 'Usuarios' AS Tabela, COUNT(*) AS Total FROM Usuarios
UNION ALL
SELECT 'Chamados', COUNT(*) FROM Chamados
UNION ALL
SELECT 'Mensagens', COUNT(*) FROM Mensagens
UNION ALL
SELECT 'ChamadoImagens', COUNT(*) FROM ChamadoImagens
UNION ALL
SELECT 'Comentarios', COUNT(*) FROM Comentarios;

-- ==========================================
-- SCRIPT CONCLUÍDO COM SUCESSO
-- ==========================================

SELECT
    '✅ BANCO DE DADOS CONFIGURADO COM SUCESSO!' AS Status,
    'u718816089_HelpDeskDB' AS BancoDados,
    NOW() AS DataHora;
