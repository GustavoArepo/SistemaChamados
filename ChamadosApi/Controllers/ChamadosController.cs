using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using ChamadosApi.Data;
using ChamadosApi.Models;

namespace ChamadosApi.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class ChamadosController : ControllerBase
    {
        private readonly ApplicationDbContext _context;

        public ChamadosController(ApplicationDbContext context)
        {
            _context = context;
        }
        //LISTAR CHAMADOS DO USER
        [HttpGet("usuario/{usuarioId}")]
        public async Task<IActionResult> GetChamadosPorUsuario(int usuarioId)
        {
            try
            {
                var chamados = await _context.Chamados
                    .Where(c => c.UsuarioId == usuarioId)
                    .OrderByDescending(c => c.DataAbertura)
                    .ToListAsync();

                return Ok(new { success = true, chamados });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { success = false, message = "Erro interno: " + ex.Message });
            }
        }
        //CRIAR CHAMADOS
        [HttpPost]
        public async Task<IActionResult> CriarChamado([FromBody] Chamado chamado)
        {
            try
            {
                chamado.DataAbertura = DateTime.Now;
                chamado.Status = "Aberto";

                _context.Chamados.Add(chamado);
                await _context.SaveChangesAsync();

                return Ok(new { success = true, message = "Chamado criado com sucesso", chamadoId = chamado.Id });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { success = false, message = "Erro interno: " + ex.Message });
            }
        }

        //BUSCA CHAMADOS
        [HttpGet("{id}")]
        public async Task<IActionResult> GetChamado(int id)
        {
            try
            {
                var chamado = await _context.Chamados
                    .FirstOrDefaultAsync(c => c.Id == id);

                if (chamado == null)
                {
                    return NotFound(new { success = false, message = "Chamado não encontrado" });
                }

                return Ok(new { success = true, chamado });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { success = false, message = "Erro interno: " + ex.Message });
            }
        }

        //ATUALIZAR CHAMADO
        [HttpPut("{id}")]
        public async Task<IActionResult> AtualizarChamado(int id, [FromBody] Chamado chamadoAtualizado)
        {
            try
            {
                var chamado = await _context.Chamados
                    .FirstOrDefaultAsync(c => c.Id == id);

                if (chamado == null)
                {
                    return NotFound(new { success = false, message = "Chamado não encontrado" });
                }

                // Atualizar campos
                chamado.Titulo = chamadoAtualizado.Titulo;
                chamado.Descricao = chamadoAtualizado.Descricao;
                chamado.Status = chamadoAtualizado.Status;

                // Se status for "Resolvido" e ainda não tem data de fechamento
                if (chamado.Status == "Resolvido" && chamado.DataFechamento == null)
                {
                    chamado.DataFechamento = DateTime.Now;
                }

                await _context.SaveChangesAsync();

                return Ok(new { success = true, message = "Chamado atualizado com sucesso" });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { success = false, message = "Erro interno: " + ex.Message });
            }
        }

        //DELETAR CHAMADO
        [HttpDelete("{id}")]
        public async Task<IActionResult> DeletarChamado(int id)
        {
            try
            {
                var chamado = await _context.Chamados
                    .FirstOrDefaultAsync(c => c.Id == id);

                if (chamado == null)
                {
                    return NotFound(new { success = false, message = "Chamado não encontrado" });
                }

                _context.Chamados.Remove(chamado);
                await _context.SaveChangesAsync();

                return Ok(new { success = true, message = "Chamado deletado com sucesso" });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { success = false, message = "Erro interno: " + ex.Message });
            }
        }

    }
}
