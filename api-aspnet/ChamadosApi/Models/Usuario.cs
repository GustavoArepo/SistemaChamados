namespace ChamadosApi.Models
{
    public class Usuario
    {
        public int Id { get; set; }
        public string Nome { get; set; }
        public string Email { get; set; }
        public string Senha { get; set; }

        public DateTime DataCadastro { get; set; }

    }

    public class UsuarioLogin
    {
        public string Email { get; set;}
        public string Senha { get; set;}

    }

    public class UsuarioRegistro 
    { 
    
        public string Nome { get; set;}
        public string Email { get; set;}
        public string Senha { get; set;}
    
    }
}
