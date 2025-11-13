using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using ChamadosApi.Data;
using ChamadosApi.Models;


namespace ChamadosApi.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class UsuarioController : ControllerBase
    {
        private readonly ApplicationDbContext _context;

        public UsuarioController(ApplicationDbContext context)
        {

            _context = context;

        }

        [HttpGet("perfil/{usuarioId}")]
        public async Task<IActionResult> GetPerfil(int usuarioId)
        {
            try
            {
                var usuario = await _context.Usuarios
                   .FirstOrDefaultAsync(u => u.Id == usuarioId);



            if (usuario == null)
                {
                    return NotFound(new { success = false, message = "Usuário não encontrado" });
                }

                // Estatísticas dos chamados
                var totalChamados = await _context.Chamados
                    .CountAsync(c => c.UsuarioId == usuarioId);

                var chamadosAbertos = await _context.Chamados
                    .CountAsync(c => c.UsuarioId == usuarioId && c.Status == "Aberto");

                var chamadosResolvidos = await _context.Chamados
                    .CountAsync(c => c.UsuarioId == usuarioId && c.Status == "Resolvido");

                var perfil = new UsuarioPerfil
                {
                    Id = usuario.Id,
                    Nome = usuario.Nome,
                    Email = usuario.Email,
                    DataCadastro = usuario.DataCadastro,
                    TotalChamados = totalChamados,
                    ChamadosAbertos = chamadosAbertos,
                    ChamadosResolvidos = chamadosResolvidos
                };

                return Ok(new { success = true, perfil });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { success = false, message = "Erro interno:" + ex.Message });

            }
        }

        [HttpPut("perfil/{usuarioId}")]
        public async Task<IActionResult> AtualizarPerfil(int usuarioId, [FromBody] UsuarioAtualizacao atualizacao)
        {
            try
            {
                var usuario = await _context.Usuarios
                 .FirstOrDefaultAsync(u => u.Id == usuarioId);

                if (usuario == null)
                {
                    return NotFound(new { success = false, message = "Usuário não encontrado" });
                }
                // Atualizar nome
                if (!string.IsNullOrEmpty(atualizacao.Nome))
                {
                    usuario.Nome = atualizacao.Nome;
                }
                // Atualizar email (verificar se já existe)
                if (!string.IsNullOrEmpty(atualizacao.Email) && atualizacao.Email != usuario.Email)
                {
                    var emailExistente = await _context.Usuarios
                        .AnyAsync(u => u.Email == atualizacao.Email && u.Id != usuarioId);

                    if (emailExistente)
                    {
                        return BadRequest(new
                        {
                            success = false,
                            message = "Email já está em uso"
                        });
                    }

                    usuario.Email = atualizacao.Email;

                }

                // Atualizar senha (se fornecida)
                if (!string.IsNullOrEmpty(atualizacao.NovaSenha))
                {
                    if (string.IsNullOrEmpty(atualizacao.SenhaAtual) || atualizacao.SenhaAtual != usuario.Senha)
                    {
                        return BadRequest(new { success = false, message = "Senha atual incorreta" });
                    }

                    if (atualizacao.NovaSenha.Length < 8)
                    {
                        return BadRequest(new { success = false, message = "Nova senha deve ter no mínimo 8 caracteres" });
                    }

                    usuario.Senha = atualizacao.NovaSenha;
                }

                await _context.SaveChangesAsync();

                return Ok(new { success = true, message = "Perfil atualizado com sucesso" });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { success = false, message = "Erro interno: " + ex.Message });
            }
        }

       // [HttpPost("fcm-token")]
        //public async Task<IActionResult> SalvarFCMToken([FromBody] FCMTokenRequest request)
        //{
            // Implementar lógica para salvar token no banco
        //}


    }
    }
