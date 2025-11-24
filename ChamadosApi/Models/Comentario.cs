namespace ChamadosApi.Models
{
    public class Comentario
    {
        public int Id { get; set; }
        public string Mensagem { get; set; } = string.Empty;
        public string Autor { get; set; } = string.Empty;
        public bool EhAdministrador { get; set; } = false;
        public DateTime DataCriacao { get; set; } = DateTime.Now;
        public int ChamadoId { get; set; }
        public Chamado Chamado { get; set; }
    }
}
