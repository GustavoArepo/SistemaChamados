namespace ChamadosApi.Models
{
    public class UsuarioPerfil
    {
        public int Id { get; set; }
        public string Nome { get; set; }
        public string Email { get; set; }
        public DateTime DataCadastro { get; set; }
        public int TotalChamados { get; set; }
        public int ChamadosAbertos { get; set; }
        public int ChamadosResolvidos { get; set; }

    }

    public class UsuarioAtualizacao
    {
        public string Nome { get; set; }
        public string Email { get; set; }
        public string SenhaAtual { get; set; }
        public string NovaSenha { get; set; }
    }
}
