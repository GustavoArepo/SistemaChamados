using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using ChamadosApi.Data;
using ChamadosApi.Models;

namespace ChamadosApi.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class ImagensController : ControllerBase
    {
        private readonly ApplicationDbContext _context;
        private readonly IWebHostEnvironment _environment;

        public ImagensController(ApplicationDbContext context, IWebHostEnvironment environment)
        {
            _context = context;
            _environment = environment;
        }

        // POST: api/imagens/upload
        [HttpPost("upload")]
        public async Task<IActionResult> UploadImagem([FromForm] ImagemRequest request)
        {
            try
            {
                if (request.Arquivo == null || request.Arquivo.Length == 0)
                {
                    return BadRequest(new ImagemResponse
                    {
                        Success = false,
                        Message = "Nenhum arquivo enviado"
                    });
                }

                // Validar se é imagem
                var allowedExtensions = new[] { ".jpg", ".jpeg", ".png", ".gif", ".bmp" };
                var fileExtension = Path.GetExtension(request.Arquivo.FileName).ToLower();

                if (!allowedExtensions.Contains(fileExtension))
                {
                    return BadRequest(new ImagemResponse
                    {
                        Success = false,
                        Message = "Formato de arquivo não permitido"
                    });
                }

                // Criar pasta se não existir
                var uploadsFolder = Path.Combine(_environment.WebRootPath, "uploads");
                if (!Directory.Exists(uploadsFolder))
                {
                    Directory.CreateDirectory(uploadsFolder);
                }

                // Gerar nome único para o arquivo
                var fileName = Guid.NewGuid().ToString() + fileExtension;
                var filePath = Path.Combine(uploadsFolder, fileName);

                // Salvar arquivo
                using (var stream = new FileStream(filePath, FileMode.Create))
                {
                    await request.Arquivo.CopyToAsync(stream);
                }

                // Salvar no banco
                var imagem = new ChamadoImagem
                {
                    ChamadoId = request.ChamadoId,
                    NomeArquivo = request.Arquivo.FileName,
                    CaminhoArquivo = $"/uploads/{fileName}",
                    DataUpload = DateTime.Now
                };

                _context.ChamadoImagens.Add(imagem);
                await _context.SaveChangesAsync();

                return Ok(new ImagemResponse
                {
                    Success = true,
                    Message = "Imagem enviada com sucesso",
                    Imagem = imagem
                });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new ImagemResponse
                {
                    Success = false,
                    Message = "Erro interno: " + ex.Message
                });
            }
        }

        // GET: api/imagens/chamado/{chamadoId}
        [HttpGet("chamado/{chamadoId}")]
        public async Task<IActionResult> GetImagensPorChamado(int chamadoId)
        {
            try
            {
                var imagens = await _context.ChamadoImagens
                    .Where(i => i.ChamadoId == chamadoId)
                    .OrderByDescending(i => i.DataUpload)
                    .ToListAsync();

                return Ok(new ImagensResponse
                {
                    Success = true,
                    Imagens = imagens
                });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new ImagensResponse
                {
                    Success = false,
                    Message = "Erro interno: " + ex.Message
                });
            }
        }

        // DELETE: api/imagens/{id}
        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteImagem(int id)
        {
            try
            {
                var imagem = await _context.ChamadoImagens.FindAsync(id);
                if (imagem == null)
                {
                    return NotFound(new { success = false, message = "Imagem não encontrada" });
                }

                // Deletar arquivo físico
                var filePath = Path.Combine(_environment.WebRootPath, imagem.CaminhoArquivo.TrimStart('/'));
                if (System.IO.File.Exists(filePath))
                {
                    System.IO.File.Delete(filePath);
                }

                // Deletar do banco
                _context.ChamadoImagens.Remove(imagem);
                await _context.SaveChangesAsync();

                return Ok(new { success = true, message = "Imagem deletada com sucesso" });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { success = false, message = "Erro interno: " + ex.Message });
            }
        }
    }
}