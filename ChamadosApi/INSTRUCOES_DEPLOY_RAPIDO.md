# 🚀 Instruções de Deploy Rápido - Hostinger

## 📋 Informações da Hospedagem

- **Servidor**: `br-asc-web791.main-hosting.eu`
- **Banco de Dados**: `u718816089_HelpDeskDB`
- **Usuário**: `u718816089_HelpDeskDB`
- **Porta MySQL**: `3306`

## ⚡ Passo a Passo Rápido

### 1️⃣ Criar as Tabelas no Banco de Dados

1. Acesse o **phpMyAdmin** no painel da Hostinger (hPanel)
2. Selecione o banco `u718816089_HelpDeskDB`
3. Vá na aba **SQL**
4. Copie e cole TODO o conteúdo do arquivo `setup_mysql_hostinger.sql`
5. Clique em **Executar/Go**
6. Verifique se apareceu a mensagem de sucesso

### 2️⃣ Build da Aplicação

Execute no terminal (pasta `ChamadosApi`):

```bash
# Restaurar pacotes NuGet
dotnet restore

# Build em modo Release
dotnet publish -c Release -o ./publish

# Verificar se gerou corretamente
ls -la ./publish
```

Você deve ver:
- ✅ `ChamadosApi.dll`
- ✅ `appsettings.Production.json`
- ✅ `web.config`
- ✅ Pasta `wwwroot/`

### 3️⃣ Upload via FTP

**Credenciais FTP** (obter no hPanel):

1. No hPanel, vá em **Files** → **FTP Accounts**
2. Copie as credenciais FTP do domínio

**Upload com FileZilla**:

1. Conecte usando as credenciais FTP
2. Navegue até `/public_html` (ou `/httpdocs`)
3. Crie uma pasta chamada `api` (ou use a raiz)
4. Faça upload de TODOS os arquivos de `./publish/` para `/public_html/api/`

**Estrutura final no servidor**:
```
/public_html/api/
├── ChamadosApi.dll
├── appsettings.Production.json
├── web.config
├── .htaccess
├── wwwroot/
│   └── uploads/
└── ... (outros arquivos DLL e dependências)
```

### 4️⃣ Configurar Permissões

Via FTP ou SSH, configure as permissões:

```bash
# Pasta de uploads precisa ser gravável
chmod 755 wwwroot/uploads

# Se precisar de logs
mkdir logs
chmod 755 logs
```

### 5️⃣ Testar a API

Acesse no navegador:

```
https://br-asc-web791.main-hosting.eu/api/swagger
```

Ou teste um endpoint:

```bash
curl https://br-asc-web791.main-hosting.eu/api/usuarios
```

## 🧪 Credenciais de Teste

Após executar o script SQL, você terá:

- **Email**: `admin@helpdesk.com`
- **Senha**: `12345678`
- **Usuário**: Administrador

Teste o login via Swagger ou Postman:

```json
POST /api/auth/login
{
  "email": "admin@helpdesk.com",
  "senha": "12345678"
}
```

## ✅ Checklist Completo

- [ ] **Banco criado** - Executou `setup_mysql_hostinger.sql` no phpMyAdmin
- [ ] **Tabelas criadas** - Verificou que Usuarios, Chamados, etc. existem
- [ ] **Build feito** - `dotnet publish` executado com sucesso
- [ ] **Upload completo** - Todos os arquivos enviados via FTP
- [ ] **Permissões OK** - Pasta uploads com chmod 755
- [ ] **Teste básico** - Swagger acessível ou endpoint respondendo

## 🐛 Troubleshooting Rápido

### Erro 500 - Internal Server Error

**Verifique**:
1. Se o arquivo `web.config` está presente
2. Se o ASP.NET Core Runtime 8.0 está instalado no servidor
3. Logs em `logs/stdout-*.log` (se a pasta existir)

**Solução Hostinger**:
- Abra ticket no suporte pedindo instalação do **.NET 8.0 Hosting Bundle**

### Erro de Conexão com MySQL

**Teste a conexão**:
1. Vá no phpMyAdmin
2. Tente fazer login com `u718816089_HelpDeskDB` / `VnVq8:ed:j6H`
3. Se conectar = credenciais OK

**Se não conectar**:
- Verifique se o IP do servidor está liberado
- Confirme que o usuário tem permissões no banco

### CORS Bloqueado

Já configurado em `appsettings.Production.json`:

```json
"AllowedOrigins": [
  "https://br-asc-web791.main-hosting.eu",
  "http://br-asc-web791.main-hosting.eu",
  "http://10.0.2.2",
  "http://localhost"
]
```

Se precisar adicionar mais domínios, edite este array.

### Swagger Não Aparece

Por padrão, Swagger está desabilitado em produção por segurança.

**Para habilitar** (NÃO recomendado em produção final):

Edite `Program.cs` e remova a condição:

```csharp
// Era:
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
}

// Mude para:
app.UseSwagger();
app.UseSwaggerUI();
```

Depois refaça o build e upload.

## 📱 Configurar App Android

Após deploy bem-sucedido, atualize o app Android:

**Arquivo**: `app/src/main/java/.../ApiClient.java`

```java
// Era:
private static final String BASE_URL = "http://10.0.2.2:5257/api/";

// Mude para:
private static final String BASE_URL = "https://br-asc-web791.main-hosting.eu/api/";
```

## 🔐 Segurança - Próximos Passos

⚠️ **IMPORTANTE**: Após confirmar que tudo funciona:

1. **Mudar senha do usuário admin**
2. **Implementar hash de senhas** (BCrypt)
3. **Implementar autenticação JWT** real
4. **Desabilitar Swagger** em produção
5. **Configurar SSL/HTTPS** (Let's Encrypt)

## 📞 Suporte

- **Hostinger**: Chat 24/7 no hPanel
- **Documentação Completa**: Ver `DEPLOY_HOSTINGER.md`
- **Problemas**: Abra issue no GitHub

---

**Configuração Atual**:
- ✅ MySQL configurado
- ✅ Credenciais inseridas
- ✅ JWT Key gerada
- ✅ CORS configurado
- ✅ Script SQL pronto

**Pronto para deploy!** 🎉
