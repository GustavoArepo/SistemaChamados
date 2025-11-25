# 📦 Resumo Rápido - Deploy Hostinger

## 🎯 Arquivos Criados para Integração

### Arquivos de Configuração
- ✅ `web.config` - Configuração do IIS para hospedagem
- ✅ `appsettings.Production.json` - Configurações de produção (NÃO commitado)
- ✅ `appsettings.Production.Example.json` - Template de configuração
- ✅ `.htaccess` - Proteção e otimização Apache

### Documentação
- ✅ `DEPLOY_HOSTINGER.md` - Guia completo de deploy passo a passo

## 🚀 Mudanças Implementadas

### 1. Suporte Multi-Banco de Dados
A API agora suporta tanto **SQL Server** quanto **MySQL**:

```csharp
// Program.cs - Seleção automática baseada em configuração
var databaseProvider = builder.Configuration.GetValue<string>("DatabaseProvider") ?? "SqlServer";
```

**Para usar MySQL** (recomendado na Hostinger):
```json
{
  "DatabaseProvider": "MySql",
  "ConnectionStrings": {
    "MySqlConnection": "Server=localhost;Database=seu_db;User=seu_user;Password=senha;Port=3306;"
  }
}
```

**Para usar SQL Server**:
```json
{
  "DatabaseProvider": "SqlServer",
  "ConnectionStrings": {
    "DefaultConnection": "Server=servidor;Database=db;User Id=user;Password=senha;"
  }
}
```

### 2. CORS Dinâmico
CORS agora se adapta automaticamente entre desenvolvimento e produção:

**Desenvolvimento** (sem configuração):
- `http://10.0.2.2` (Emulador Android)
- `http://localhost`
- `http://127.0.0.1`

**Produção** (configurado em `appsettings.Production.json`):
```json
"AppSettings": {
  "AllowedOrigins": [
    "https://seudominio.com",
    "https://www.seudominio.com"
  ]
}
```

### 3. Configuração IIS/Hostinger
`web.config` configurado com:
- ASP.NET Core Module V2
- Logs habilitados
- Headers de segurança
- Compressão ativada
- Upload até 50MB

## 📝 Checklist de Deploy

### Antes do Deploy
- [ ] Criar banco de dados MySQL na Hostinger
- [ ] Copiar `appsettings.Production.Example.json` para `appsettings.Production.json`
- [ ] Configurar credenciais reais no `appsettings.Production.json`
- [ ] Gerar chave JWT forte (64+ caracteres)
- [ ] Build da aplicação: `dotnet publish -c Release -o ./publish`

### Durante o Deploy
- [ ] Upload via FTP/SFTP para `/public_html/api`
- [ ] Executar migrations ou script SQL para criar tabelas
- [ ] Configurar SSL/HTTPS
- [ ] Ajustar permissões da pasta `wwwroot/uploads`

### Após o Deploy
- [ ] Testar endpoint: `https://seudominio.com/api/swagger`
- [ ] Verificar logs em caso de erro
- [ ] Atualizar URL no app Android

## 🔗 Links Úteis

- **Guia Completo**: Veja `DEPLOY_HOSTINGER.md` para instruções detalhadas
- **Suporte Hostinger**: https://support.hostinger.com
- **ASP.NET Core Docs**: https://docs.microsoft.com/aspnet/core

## ⚡ Deploy Rápido (Resumido)

```bash
# 1. Build
cd ChamadosApi
dotnet publish -c Release -o ./publish

# 2. Configurar appsettings.Production.json
cp appsettings.Production.Example.json appsettings.Production.json
nano appsettings.Production.json  # Editar com credenciais reais

# 3. Upload via FTP
# Conecte no FileZilla e envie tudo de ./publish para /public_html/api

# 4. Criar banco e tabelas
# Execute o script SQL fornecido no DEPLOY_HOSTINGER.md

# 5. Testar
curl https://seudominio.com/api/swagger
```

## 🆘 Problemas Comuns

| Erro | Solução |
|------|---------|
| 500 Internal Server Error | Verifique logs em `logs/stdout-*.log` |
| Erro de conexão DB | Teste credenciais no phpMyAdmin |
| Upload falha | `chmod 755 wwwroot/uploads` |
| CORS bloqueado | Adicione origem em `AllowedOrigins` |

## 🔐 Segurança

⚠️ **NUNCA commitar** o arquivo `appsettings.Production.json` com credenciais reais!

O `.gitignore` já está configurado para proteger:
- `appsettings.Production.json`
- Logs
- Pasta de uploads
- Arquivos de ambiente

---

**Desenvolvido para hospedagem na Hostinger** 🚀
