namespace ChamadosApi.Models
{
    public class Chamado
    {
        public int Id { get; set; }
        public string Titulo { get; set; }
        public string Descricao { get; set; }
        public int UsuarioId { get; set; }
        public string Status { get; set; } = "Aberto";
        public DateTime DataAbertura { get; set; } = DateTime.Now;
        public DateTime? DataFechamento { get; set; }
        public string NumeroChamado { get; set; } = string.Empty;
        public string Prioridade { get; set; } = "Média";
        public string Categoria { get; set; } = "Geral";
        public string Responsavel { get; set; } = string.Empty;
    }

    public class UsuarioAtualizacao
    {
        public string Nome { get; set; }
        public string Email { get; set; }
        public string SenhaAtual { get; set; }
        public string NovaSenha { get; set; }
    }
}

