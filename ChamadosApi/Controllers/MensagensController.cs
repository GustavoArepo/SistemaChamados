using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using ChamadosApi.Data;
using ChamadosApi.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using ChamadosApi.Models.ChamadosApi.Models;


namespace ChamadosApi.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class MensagensController : ControllerBase
    {
        private readonly ApplicationDbContext _context;

        public MensagensController(ApplicationDbContext context)
        {
            _context = context;
        }

        // GET: api/mensagens/chamado/{chamadoId}
        [HttpGet("chamado/{chamadoId}")]
        public async Task<IActionResult> GetMensagensPorChamado(int chamadoId)
        {
            try
            {
                var mensagens = await _context.Mensagens
                    .Where(m => m.ChamadoId == chamadoId)
                    .OrderBy(m => m.DataEnvio)
                    .ToListAsync();

                return Ok(new MensagensResponse
                {
                    Success = true,
                    Mensagens = mensagens
                });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new MensagensResponse
                {
                    Success = false,
                    Message = "Erro interno: " + ex.Message
                });
            }
        }

        // POST: api/mensagens
        [HttpPost]
        public async Task<IActionResult> EnviarMensagem([FromBody] MensagemRequest request)
        {
            try
            {
                // Verificar se o chamado existe
                var chamado = await _context.Chamados
                    .FirstOrDefaultAsync(c => c.Id == request.ChamadoId);

                if (chamado == null)
                {
                    return NotFound(new MensagemResponse
                    {
                        Success = false,
                        Message = "Chamado não encontrado"
                    });
                }

                var mensagem = new Mensagem
                {
                    ChamadoId = request.ChamadoId,
                    UsuarioId = request.UsuarioId,
                    Texto = request.Texto,
                    EhAtendente = request.EhAtendente,
                    DataEnvio = DateTime.Now,
                    Lida = false
                };

                _context.Mensagens.Add(mensagem);
                await _context.SaveChangesAsync();

                return Ok(new MensagemResponse
                {
                    Success = true,
                    Message = "Mensagem enviada com sucesso",
                    Mensagem = mensagem
                });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new MensagemResponse
                {
                    Success = false,
                    Message = "Erro interno: " + ex.Message
                });
            }
        }

        // PUT: api/mensagens/{id}/ler
        [HttpPut("{id}/ler")]
        public async Task<IActionResult> MarcarComoLida(int id)
        {
            try
            {
                var mensagem = await _context.Mensagens
                    .FirstOrDefaultAsync(m => m.Id == id);

                if (mensagem == null)
                {
                    return NotFound(new { success = false, message = "Mensagem não encontrada" });
                }

                mensagem.Lida = true;
                await _context.SaveChangesAsync();

                return Ok(new { success = true, message = "Mensagem marcada como lida" });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { success = false, message = "Erro interno: " + ex.Message });
            }
        }

        // GET: api/mensagens/chamado/{chamadoId}/nao-lidas
        [HttpGet("chamado/{chamadoId}/nao-lidas")]
        public async Task<IActionResult> GetMensagensNaoLidas(int chamadoId, int usuarioId)
        {
            try
            {
                var mensagensNaoLidas = await _context.Mensagens
                    .Where(m => m.ChamadoId == chamadoId &&
                           m.UsuarioId != usuarioId && // Mensagens de outros usuários
                           !m.Lida)
                    .ToListAsync();

                return Ok(new MensagensResponse
                {
                    Success = true,
                    Mensagens = mensagensNaoLidas
                });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new MensagensResponse
                {
                    Success = false,
                    Message = "Erro interno: " + ex.Message
                });
            }
        }
    }
}