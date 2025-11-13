using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace ChamadosApi.Migrations
{
    public partial class AddMensagensTable : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            // ✅ MANTENHA APENAS O Up()
            migrationBuilder.CreateTable(
                name: "Mensagens",
                columns: table => new
                {
                    Id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    ChamadoId = table.Column<int>(type: "int", nullable: false),
                    UsuarioId = table.Column<int>(type: "int", nullable: false),
                    Texto = table.Column<string>(type: "nvarchar(500)", maxLength: 500, nullable: false),
                    DataEnvio = table.Column<DateTime>(type: "datetime2", nullable: false),
                    EhAtendente = table.Column<bool>(type: "bit", nullable: false),
                    Lida = table.Column<bool>(type: "bit", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Mensagens", x => x.Id);
                    table.ForeignKey(
                        name: "FK_Mensagens_Chamados_ChamadoId",
                        column: x => x.ChamadoId,
                        principalTable: "Chamados",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateIndex(
                name: "IX_Mensagens_ChamadoId",
                table: "Mensagens",
                column: "ChamadoId");
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            // ✅ MANTENHA APENAS O Down()
            migrationBuilder.DropTable(
                name: "Mensagens");
        }

        // ❌ DELETE COMPLETAMENTE ESTE MÉTODO:
        // protected override void BuildTargetModel(ModelBuilder modelBuilder)
        // {
        //     ... todo o conteúdo ...
        // }
    }
}