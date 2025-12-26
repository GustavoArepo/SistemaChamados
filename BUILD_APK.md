# Build do APK - Sistema de Chamados

Este documento descreve como gerar o APK do aplicativo para instalação em dispositivos Android.

## Opção 1: Build Automático via GitHub Actions (Recomendado)

### APK Debug (Mais Simples)

1. Faça push para qualquer branch do repositório
2. Acesse a aba **Actions** no GitHub
3. Clique no workflow **Build APK Debug**
4. Após o build completar, baixe o artifact `app-debug-xxx`
5. Extraia o ZIP e instale o APK no dispositivo

### APK Release (Assinado)

Para builds de release, configure os seguintes **Secrets** no GitHub:

1. Vá em **Settings > Secrets and variables > Actions**
2. Adicione os seguintes secrets:

| Secret | Valor |
|--------|-------|
| `KEYSTORE_BASE64` | Keystore em base64 (veja abaixo) |
| `KEYSTORE_PASSWORD` | `chamados123` |
| `KEY_PASSWORD` | `chamados123` |
| `KEY_ALIAS` | `appchamados` |

**Para gerar o KEYSTORE_BASE64:**
```bash
base64 -w 0 app/keystore/release-key.jks
```

## Opção 2: Build Local

### Pré-requisitos

- **Android Studio** ou **Android SDK** instalado
- **JDK 17** ou superior
- Variável `ANDROID_HOME` configurada

### Passos

1. Clone o repositório:
```bash
git clone https://github.com/seu-usuario/SistemaChamados.git
cd SistemaChamados
```

2. Crie o arquivo `keystore.properties` na raiz do projeto:
```properties
storePassword=chamados123
keyPassword=chamados123
keyAlias=appchamados
storeFile=keystore/release-key.jks
```

3. Gere o APK:

**Debug (sem assinatura de release):**
```bash
./gradlew assembleDebug
```
APK gerado em: `app/build/outputs/apk/debug/app-debug.apk`

**Release (assinado):**
```bash
./gradlew assembleRelease
```
APK gerado em: `app/build/outputs/apk/release/app-release.apk`

## Instalação no Dispositivo

### Via ADB
```bash
adb install app/build/outputs/apk/release/app-release.apk
```

### Via Transferência de Arquivo
1. Transfira o APK para o dispositivo (USB, email, drive, etc.)
2. No dispositivo, habilite **Fontes desconhecidas** em Configurações > Segurança
3. Abra o arquivo APK e confirme a instalação

## Configuração da API

O app se conecta a uma API backend. Configure o endereço da API em:
- `app/src/main/java/com/example/appchamados/network/ApiClient.java`

Para testes locais com emulador, use:
- `http://10.0.2.2:5000` (aponta para localhost da máquina host)

## Keystore de Produção

A keystore incluída é apenas para **testes de desenvolvimento**. Para publicação na Play Store:

1. Gere uma nova keystore:
```bash
keytool -genkey -v -keystore minha-keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias meuapp
```

2. **IMPORTANTE**: Guarde a keystore e senhas em local seguro. Se perdê-las, não poderá atualizar o app na Play Store.

## Solução de Problemas

### Erro: SDK location not found
Crie o arquivo `local.properties` na raiz:
```properties
sdk.dir=/caminho/para/android/sdk
```

### Erro: Java version incompatível
Certifique-se de usar JDK 17:
```bash
java -version
```

### Erro de permissão no gradlew
```bash
chmod +x gradlew
```
