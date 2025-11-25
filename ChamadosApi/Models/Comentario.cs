namespace ChamadosApi.Models
{
    public class Comentario
    {
        public int Id { get; set; }
        public string Mensagem { get; set; }
        public string Autor { get; set; }  // Nome de quem comentou
        public bool EhAdministrador { get; set; } // TRUE=Admin, FALSE=Usuário
        public DateTime DataCriacao { get; set; } = DateTime.Now;
        
        // Relacionamento com Chamado
        public int ChamadoId { get; set; }
        public Chamado Chamado { get; set; }
    }
}
