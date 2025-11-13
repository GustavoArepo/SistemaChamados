using Microsoft.AspNetCore.Mvc;

namespace ChamadosApi.Models
{
    public class FCMTokenRequest
    {
        public int UsuarioId { get; set; }
        public string FCMToken { get; set; }
    }
}
