using Microsoft.EntityFrameworkCore;
using ChamadosApi.Models;
using ChamadosApi.Models.ChamadosApi.Models;

namespace ChamadosApi.Data
{
    public class ApplicationDbContext : DbContext
    {
        public ApplicationDbContext(DbContextOptions<ApplicationDbContext> options) : base(options)
        {
        }

        public DbSet<Usuario> Usuarios { get; set; }
        public DbSet<Chamado> Chamados { get; set; }
        public DbSet<Mensagem> Mensagens { get; set; }
        public DbSet<ChamadoImagem> ChamadoImagens { get; set; }
        public DbSet<Comentario> Comentarios { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            // Configurações adicionais se necessário
            modelBuilder.Entity<Usuario>()
                .HasIndex(u => u.Email)
                .IsUnique();

            modelBuilder.Entity<Mensagem>()
                .HasOne(m => m.Chamado)
                .WithMany()
                .HasForeignKey(m => m.ChamadoId)
                .OnDelete(DeleteBehavior.Cascade);

            modelBuilder.Entity<Mensagem>()
                .HasOne(m => m.Usuario)
                .WithMany()
                .HasForeignKey(m => m.UsuarioId)
                .OnDelete(DeleteBehavior.Restrict);

            modelBuilder.Entity<Comentario>()
                .HasOne(c => c.Chamado)
                .WithMany()
                .HasForeignKey(c => c.ChamadoId)
                .OnDelete(DeleteBehavior.Cascade);
        }

    }
}
