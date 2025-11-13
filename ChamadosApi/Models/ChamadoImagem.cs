using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace ChamadosApi.Models
{
    public class ChamadoImagem
    {
        [Key]
        public int Id { get; set; }

        [Required]
        public int ChamadoId { get; set; }

        [Required]
        [MaxLength(255)]
        public string NomeArquivo { get; set; }

        [Required]
        [MaxLength(500)]
        public string CaminhoArquivo { get; set; }

        public DateTime DataUpload { get; set; } = DateTime.Now;

        [ForeignKey("ChamadoId")]
        public virtual Chamado Chamado { get; set; }
    }

    public class ImagemRequest
    {
        [Required]
        public int ChamadoId { get; set; }

        [Required]
        public IFormFile Arquivo { get; set; }
    }

    public class ImagemResponse
    {
        public bool Success { get; set; }
        public string Message { get; set; }
        public ChamadoImagem Imagem { get; set; }
    }

    public class ImagensResponse
    {
        public bool Success { get; set; }
        public string Message { get; set; }
        public List<ChamadoImagem> Imagens { get; set; }
    }
}