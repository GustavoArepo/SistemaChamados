using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using ChamadosApi.Data;
using ChamadosApi.Models;

namespace ChamadosApi.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class AuthController : ControllerBase
    {
        
        private readonly ApplicationDbContext _context;

        public AuthController(ApplicationDbContext context)
        {
            _context = context;
        }

        [HttpPost("login")]
        public async Task<IActionResult> Login([FromBody] UsuarioLogin loginRequest)
        {
            try
            {
                // Validar email
                if (string.IsNullOrEmpty(loginRequest.Email) || !loginRequest.Email.Contains("@"))
                {
                    return BadRequest(new { success = false, message = "Email inválido" });
                }

                // Validar senha
                if (string.IsNullOrEmpty(loginRequest.Senha) || loginRequest.Senha.Length < 8)
                {
                    return BadRequest(new { success = false, message = "Senha deve ter no mínimo 8 caracteres" });
                }

                // Buscar usuário no banco
                var usuario = await _context.Usuarios
                    .FirstOrDefaultAsync(u => u.Email == loginRequest.Email && u.Senha == loginRequest.Senha);

                if (usuario == null)
                {
                    return Unauthorized(new { success = false, message = "Email ou senha incorretos" });
                }

                // Retornar dados do usuário (sem a senha por segurança)
                var userResponse = new
                {
                    usuario.Id,
                    usuario.Nome,
                    usuario.Email,
                    usuario.DataCadastro
                };

                return Ok(new { success = true, message = "Login realizado com sucesso", user = userResponse });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { success = false, message = "Erro interno do servidor: " + ex.Message });
            }
        }

        [HttpPost("register")]
        public async Task<IActionResult> Register([FromBody] UsuarioRegistro registroRequest)
        {
            try
            {
                // Validar campos
                if (string.IsNullOrEmpty(registroRequest.Nome))
                {
                    return BadRequest(new { success = false, message = "Nome é obrigatório" });
                }

                if (string.IsNullOrEmpty(registroRequest.Email) || !registroRequest.Email.Contains("@"))
                {
                    return BadRequest(new { success = false, message = "Email inválido" });
                }

                if (string.IsNullOrEmpty(registroRequest.Senha) || registroRequest.Senha.Length < 8)
                {
                    return BadRequest(new { success = false, message = "Senha deve ter no mínimo 8 caracteres" });
                }

                // Verificar se email já existe
                var usuarioExistente = await _context.Usuarios
                    .FirstOrDefaultAsync(u => u.Email == registroRequest.Email);

                if (usuarioExistente != null)
                {
                    return BadRequest(new { success = false, message = "Email já cadastrado" });
                }

                // Criar novo usuário
                var novoUsuario = new Usuario
                {
                    Nome = registroRequest.Nome,
                    Email = registroRequest.Email,
                    Senha = registroRequest.Senha, // Em produção, usar hash!
                    DataCadastro = DateTime.Now
                };

                _context.Usuarios.Add(novoUsuario);
                await _context.SaveChangesAsync();

                return Ok(new { success = true, message = "Usuário cadastrado com sucesso" });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { success = false, message = "Erro interno do servidor: " + ex.Message });
            }
        }

    }

}
