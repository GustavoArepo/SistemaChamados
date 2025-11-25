# 🚀 Guia de Deploy na Hostinger - Chamados API

Este guia detalha o processo completo de hospedagem da API REST na **Hostinger**.

---

## 📋 Índice

- [Pré-requisitos](#pré-requisitos)
- [Configuração Inicial](#configuração-inicial)
- [Preparação da Aplicação](#preparação-da-aplicação)
- [Configuração do Banco de Dados](#configuração-do-banco-de-dados)
- [Upload e Deploy](#upload-e-deploy)
- [Configurações Pós-Deploy](#configurações-pós-deploy)
- [Troubleshooting](#troubleshooting)
- [Manutenção](#manutenção)

---

## 🎯 Pré-requisitos

### Na Hostinger

- ✅ Plano de hospedagem compatível com ASP.NET Core
  - **Recomendado**: Business ou Premium Cloud Hosting
  - Ou VPS com Windows Server
- ✅ Acesso ao Painel de Controle (hPanel)
- ✅ Banco de dados MySQL ou SQL Server disponível
- ✅ Domínio configurado

### Na Máquina Local

- ✅ .NET 8.0 SDK instalado
- ✅ Git instalado
- ✅ Cliente FTP/SFTP (FileZilla) ou acesso SSH

---

## ⚙️ Configuração Inicial

### 1. Verificar Plano de Hospedagem

Acesse o painel da Hostinger e verifique:

1. **Tipo de Hospedagem**: Certifique-se que suporta ASP.NET Core 8.0
2. **Banco de Dados Disponível**:
   - MySQL 8.0+ (mais comum na Hostinger)
   - Ou SQL Server (em planos VPS)

### 2. Criar Banco de Dados

#### Opção A: MySQL (Recomendado para Shared Hosting)

1. No hPanel, vá em **Databases** → **MySQL Databases**
2. Clique em **Create Database**
3. Anote as credenciais:
   ```
   Server: localhost (ou IP fornecido)
   Database: u123456789_chamados
   User: u123456789_admin
   Password: [senha gerada]
   Port: 3306
   ```

#### Opção B: SQL Server (VPS)

1. Acesse via Remote Desktop (RDP)
2. Abra SQL Server Management Studio
3. Crie o banco de dados:
   ```sql
   CREATE DATABASE SistemaChamados;
   GO
   ```

---

## 🔧 Preparação da Aplicação

### 1. Configurar `appsettings.Production.json`

Edite o arquivo `appsettings.Production.json`:

```json
{
  "ConnectionStrings": {
    "MySqlConnection": "Server=localhost;Database=u123456789_chamados;User=u123456789_admin;Password=SUA_SENHA_AQUI;Port=3306;SslMode=Required;"
  },
  "DatabaseProvider": "MySql",
  "Jwt": {
    "Key": "GERE_UMA_CHAVE_FORTE_64_CARACTERES_MINIMO_USE_GERADOR_ONLINE",
    "Issuer": "ChamadosApi",
    "Audience": "ChamadosApp",
    "ExpirationMinutes": 1440
  },
  "AppSettings": {
    "BaseUrl": "https://seudominio.com",
    "AllowedOrigins": [
      "https://seudominio.com",
      "https://www.seudominio.com"
    ],
    "UploadPath": "wwwroot/uploads",
    "MaxUploadSizeMB": 10
  },
  "Logging": {
    "LogLevel": {
      "Default": "Warning",
      "Microsoft.AspNetCore": "Warning"
    }
  },
  "AllowedHosts": "*"
}
```

**⚠️ IMPORTANTE**:
- Substitua todas as credenciais pelas reais
- Gere uma chave JWT forte em: https://generate-random.org/api-key-generator
- Configure seu domínio real em `AllowedOrigins`

### 2. Build da Aplicação

Execute no terminal dentro da pasta `ChamadosApi`:

```bash
# Restaurar pacotes
dotnet restore

# Build em modo Release
dotnet publish -c Release -o ./publish

# Verificar se gerou corretamente
ls -la ./publish
```

Você verá arquivos como:
- `ChamadosApi.dll`
- `appsettings.json`
- `web.config`
- Pasta `wwwroot/`

### 3. Criar Pasta de Uploads

```bash
mkdir -p ./publish/wwwroot/uploads
chmod 755 ./publish/wwwroot/uploads
```

---

## 🗄️ Configuração do Banco de Dados

### Opção A: Migrações Automáticas (MySQL)

Se usar MySQL, precisa ajustar as migrations:

```bash
# Adicionar migration específica para MySQL
dotnet ef migrations add InitialMySql --context ApplicationDbContext

# Gerar script SQL
dotnet ef migrations script -o migration.sql
```

Execute o script `migration.sql` no phpMyAdmin da Hostinger:

1. Acesse **phpMyAdmin** no hPanel
2. Selecione seu banco de dados
3. Vá em **SQL**
4. Cole o conteúdo de `migration.sql`
5. Clique em **Go**

### Opção B: Criação Manual das Tabelas

Crie as tabelas manualmente usando este script:

```sql
-- Tabela Usuarios
CREATE TABLE Usuarios (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    Nome VARCHAR(100) NOT NULL,
    Email VARCHAR(100) NOT NULL UNIQUE,
    Senha VARCHAR(255) NOT NULL,
    DataCadastro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tabela Chamados
CREATE TABLE Chamados (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    Titulo VARCHAR(200) NOT NULL,
    Descricao TEXT NOT NULL,
    UsuarioId INT NOT NULL,
    Status VARCHAR(50) NOT NULL DEFAULT 'Aberto',
    DataAbertura DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    DataFechamento DATETIME NULL,
    FOREIGN KEY (UsuarioId) REFERENCES Usuarios(Id) ON DELETE CASCADE
);

-- Tabela Mensagens
CREATE TABLE Mensagens (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    ChamadoId INT NOT NULL,
    UsuarioId INT NOT NULL,
    Texto TEXT NOT NULL,
    DataEnvio DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    EhAtendente BOOLEAN NOT NULL DEFAULT FALSE,
    Lida BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (ChamadoId) REFERENCES Chamados(Id) ON DELETE CASCADE,
    FOREIGN KEY (UsuarioId) REFERENCES Usuarios(Id) ON DELETE RESTRICT
);

-- Tabela ChamadoImagens
CREATE TABLE ChamadoImagens (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    ChamadoId INT NOT NULL,
    NomeArquivo VARCHAR(255) NOT NULL,
    CaminhoArquivo VARCHAR(500) NOT NULL,
    DataUpload DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ChamadoId) REFERENCES Chamados(Id) ON DELETE CASCADE
);

-- Tabela Comentarios
CREATE TABLE Comentarios (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    ChamadoId INT NOT NULL,
    Texto TEXT NOT NULL,
    DataComentario DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ChamadoId) REFERENCES Chamados(Id) ON DELETE CASCADE
);

-- Criar usuário de teste
INSERT INTO Usuarios (Nome, Email, Senha, DataCadastro)
VALUES ('Admin', 'admin@email.com', '12345678', NOW());
```

---

## 📤 Upload e Deploy

### Método 1: FTP/SFTP (Recomendado para Shared Hosting)

1. **Obter Credenciais FTP**:
   - No hPanel, vá em **Files** → **FTP Accounts**
   - Anote: Host, Username, Password, Port

2. **Conectar via FileZilla**:
   ```
   Host: ftp.seudominio.com
   Username: u123456789
   Password: sua_senha
   Port: 21 (FTP) ou 22 (SFTP)
   ```

3. **Upload dos Arquivos**:
   - Navegue até `/public_html` ou `/httpdocs`
   - Crie pasta `api` (ou use a raiz)
   - Faça upload de TODOS os arquivos da pasta `publish/`

4. **Verificar Estrutura**:
   ```
   /public_html/api/
   ├── ChamadosApi.dll
   ├── appsettings.Production.json
   ├── web.config
   ├── wwwroot/
   │   └── uploads/
   └── ... (outros arquivos)
   ```

### Método 2: Git Deploy (VPS ou Cloud)

Se tiver acesso SSH:

```bash
# Conectar via SSH
ssh usuario@seu-servidor.hostinger.com

# Clonar repositório
git clone https://github.com/seu-usuario/SistemaChamados.git
cd SistemaChamados/ChamadosApi

# Build e publish
dotnet publish -c Release -o /var/www/api

# Configurar permissões
chmod -R 755 /var/www/api
chown -R www-data:www-data /var/www/api
```

---

## 🔒 Configurações Pós-Deploy

### 1. Configurar IIS (se aplicável)

No caso de VPS Windows:

1. Abra **IIS Manager**
2. Adicione novo **Application Pool**:
   - Nome: `ChamadosApiPool`
   - .NET CLR Version: `No Managed Code`
   - Managed Pipeline Mode: `Integrated`

3. Adicione novo **Site**:
   - Site Name: `ChamadosApi`
   - Application Pool: `ChamadosApiPool`
   - Physical Path: `C:\inetpub\wwwroot\api`
   - Binding: `http` ou `https` na porta 80/443

4. Instale **ASP.NET Core Hosting Bundle**:
   - Download: https://dotnet.microsoft.com/download/dotnet/8.0
   - Versão: `.NET 8.0 Hosting Bundle`

### 2. Configurar SSL/HTTPS

1. No hPanel, vá em **Security** → **SSL/TLS**
2. Ative **AutoSSL** ou instale certificado Let's Encrypt
3. Force HTTPS editando `.htaccess`:

```apache
# .htaccess na raiz do domínio
RewriteEngine On
RewriteCond %{HTTPS} off
RewriteRule ^(.*)$ https://%{HTTP_HOST}%{REQUEST_URI} [L,R=301]
```

### 3. Testar a API

Acesse via navegador:

```
https://seudominio.com/api/swagger
```

Ou teste endpoint básico:

```bash
curl https://seudominio.com/api/chamados/usuario/1
```

### 4. Configurar Logs

Crie pasta de logs com permissões:

```bash
mkdir -p /var/www/api/logs
chmod 755 /var/www/api/logs
```

Verifique logs em caso de erro:
```bash
tail -f /var/www/api/logs/stdout-*.log
```

---

## 🔍 Troubleshooting

### Erro 500 - Internal Server Error

**Causa**: Problemas de configuração ou falta de dependências

**Solução**:
1. Verifique se o `web.config` está presente
2. Confirme que o ASP.NET Core Runtime está instalado
3. Verifique logs em `logs/stdout-*.log`
4. Teste connection string do banco

### Erro de Conexão com Banco de Dados

**Causa**: Credenciais incorretas ou servidor inacessível

**Solução**:
1. Teste conexão via phpMyAdmin/SQL Management Studio
2. Verifique firewall (porta 3306 para MySQL, 1433 para SQL Server)
3. Confirme que o usuário tem permissões no banco
4. Verifique se `appsettings.Production.json` tem as credenciais corretas

### Upload de Imagens Falha

**Causa**: Permissões de pasta incorretas

**Solução**:
```bash
chmod -R 755 wwwroot/uploads
chown -R www-data:www-data wwwroot/uploads
```

### CORS Bloqueando Requests

**Causa**: Origem não permitida

**Solução**:
Adicione sua origem em `appsettings.Production.json`:
```json
"AppSettings": {
  "AllowedOrigins": [
    "https://seuapp.com",
    "https://www.seuapp.com"
  ]
}
```

### Swagger não Aparece em Produção

**Causa**: Swagger desabilitado por padrão em produção

**Solução**:
Edite `Program.cs` e remova a condição:
```csharp
// De:
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
}

// Para:
app.UseSwagger();
app.UseSwaggerUI();
```

⚠️ **Não recomendado em produção por segurança!**

---

## 🔧 Manutenção

### Atualizar Aplicação

1. **Build nova versão**:
   ```bash
   dotnet publish -c Release -o ./publish
   ```

2. **Backup da versão atual**:
   ```bash
   # Via SSH
   mv /var/www/api /var/www/api_backup_$(date +%Y%m%d)
   ```

3. **Upload da nova versão**:
   - Via FTP: substitua todos os arquivos
   - Via SSH: `cp -r publish/* /var/www/api/`

4. **Reiniciar aplicação**:
   ```bash
   # Windows/IIS
   iisreset

   # Linux/Kestrel com systemd
   sudo systemctl restart kestrel-chamadosapi.service
   ```

### Backup do Banco de Dados

**MySQL**:
```bash
mysqldump -u usuario -p u123456789_chamados > backup_$(date +%Y%m%d).sql
```

**SQL Server**:
```sql
BACKUP DATABASE SistemaChamados
TO DISK = 'C:\Backups\SistemaChamados.bak'
WITH FORMAT, COMPRESSION;
```

### Monitoramento

Configure alertas para:
- Uso de CPU/Memória
- Espaço em disco
- Logs de erro
- Tempo de resposta da API

Ferramentas recomendadas:
- **UptimeRobot**: monitoramento de uptime
- **Seq**: visualização de logs
- **Application Insights**: telemetria (Azure)

---

## 📞 Suporte

### Hostinger
- **Chat**: Disponível 24/7 no hPanel
- **Tickets**: https://support.hostinger.com
- **Knowledge Base**: https://support.hostinger.com/pt-BR/

### Documentação Oficial
- **ASP.NET Core**: https://docs.microsoft.com/aspnet/core
- **Entity Framework**: https://docs.microsoft.com/ef/core
- **Pomelo MySQL**: https://github.com/PomeloFoundation/Pomelo.EntityFrameworkCore.MySql

---

## ✅ Checklist Final

Antes de considerar o deploy completo:

- [ ] Banco de dados criado e tabelas migradas
- [ ] `appsettings.Production.json` configurado corretamente
- [ ] Chave JWT forte gerada e configurada
- [ ] Arquivos publicados e enviados para servidor
- [ ] `web.config` presente na pasta raiz
- [ ] Pasta `wwwroot/uploads` com permissões corretas
- [ ] SSL/HTTPS configurado e funcionando
- [ ] CORS configurado com origens corretas
- [ ] Testes de endpoints básicos realizados
- [ ] Swagger acessível (se habilitado)
- [ ] Logs sendo gerados corretamente
- [ ] Backup inicial do banco de dados realizado

---

**Deploy realizado com sucesso! 🎉**

Para conectar o app Android, atualize a URL base em `ApiClient.java`:

```java
private static final String BASE_URL = "https://seudominio.com/api/";
```
