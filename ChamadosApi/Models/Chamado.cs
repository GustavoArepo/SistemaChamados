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
        
        // CAMPOS ADICIONADOS para compatibilidade com sistema Web/Desktop do colega
        public string NumeroChamado { get; set; } = string.Empty;
        public string Prioridade { get; set; } = "Média"; // Baixa, Média, Alta
        public string Categoria { get; set; } = "Geral"; // Hardware, Software, Rede, etc
        public string Responsavel { get; set; } = string.Empty;
    }
}
public class UsuarioAtualizacao
{
    public string Nome { get; set; }
    public string Email { get; set; }
    public string SenhaAtual { get; set; }
    public string NovaSenha { get; set; }
}

