using System;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace ChamadosApi.Models
{
    using global::ChamadosApi.Models.ChamadosApi.Models;
    using System;
    using System.ComponentModel.DataAnnotations;
    using System.ComponentModel.DataAnnotations.Schema;

    namespace ChamadosApi.Models
    {
        public class Mensagem
        {
            [Key]
            [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
            public int Id { get; set; }

            [Required]
            [Column("ChamadoId")] // ✅ FORÇAR NOME DA COLUNA
            public int ChamadoId { get; set; }

            [Required]
            [Column("UsuarioId")]
            public int UsuarioId { get; set; }

            [Required]
            [MaxLength(500)]
            [Column("Texto")]
            public string Texto { get; set; }

            [Required]
            [Column("DataEnvio")]
            public DateTime DataEnvio { get; set; } = DateTime.Now;

            [Column("EhAtendente")]
            public bool EhAtendente { get; set; }

            [Column("Lida")]
            public bool Lida { get; set; } = false;

            // Navegação
            [ForeignKey("ChamadoId")]
            public virtual Chamado Chamado { get; set; }

            [ForeignKey("UsuarioId")]
            public virtual Usuario Usuario { get; set; }
        }

        // Manter os outros modelos (MensagemRequest, etc.)
    }

    // Model para enviar mensagem
    public class MensagemRequest
    {
        [Required]
        public int ChamadoId { get; set; }

        [Required]
        public int UsuarioId { get; set; }

        [Required]
        [MaxLength(500)]
        public string Texto { get; set; }

        public bool EhAtendente { get; set; }
    }

    // Model para resposta
    public class MensagemResponse
    {
        public bool Success { get; set; }
        public string Message { get; set; }
        public Mensagem Mensagem { get; set; }
    }

    public class MensagensResponse
    {
        public bool Success { get; set; }
        public string Message { get; set; }
        public List<Mensagem> Mensagens { get; set; }
    }
}