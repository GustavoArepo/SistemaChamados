using Microsoft.EntityFrameworkCore;
using ChamadosApi.Data;

var builder = WebApplication.CreateBuilder(args);


// Add services to the container.
builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();

// Configure Swagger
builder.Services.AddSwaggerGen(c =>
{
    c.SwaggerDoc("v1", new Microsoft.OpenApi.Models.OpenApiInfo
    {
        Title = "Chamados API",
        Version = "v1",
        Description = "API para sistema de chamados - Android + ASP.NET"
    });
});

// Add DbContext - Suporte para SQL Server ou MySQL
var databaseProvider = builder.Configuration.GetValue<string>("DatabaseProvider") ?? "SqlServer";

builder.Services.AddDbContext<ApplicationDbContext>(options =>
{
    if (databaseProvider.Equals("MySql", StringComparison.OrdinalIgnoreCase))
    {
        var connectionString = builder.Configuration.GetConnectionString("MySqlConnection");
        options.UseMySql(connectionString, ServerVersion.AutoDetect(connectionString));
    }
    else
    {
        options.UseSqlServer(builder.Configuration.GetConnectionString("DefaultConnection"));
    }
});

// Add CORS - Configuração dinâmica para desenvolvimento e produção
builder.Services.AddCors(options =>
{
    options.AddPolicy("AllowAll",
        policy =>
        {
            var allowedOrigins = builder.Configuration.GetSection("AppSettings:AllowedOrigins").Get<string[]>();

            if (allowedOrigins != null && allowedOrigins.Length > 0)
            {
                // Produção: usa origens específicas
                policy.WithOrigins(allowedOrigins)
                      .AllowAnyMethod()
                      .AllowAnyHeader()
                      .AllowCredentials();
            }
            else
            {
                // Desenvolvimento: permite origens locais
                policy.WithOrigins(
                        "http://10.0.2.2",        // Emulador Android
                        "http://localhost",       // Localhost
                        "http://127.0.0.1"        // Localhost alternativo
                    )
                    .AllowAnyMethod()
                    .AllowAnyHeader()
                    .AllowCredentials();
            }
        });
});

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI(c =>
    {
        c.SwaggerEndpoint("/swagger/v1/swagger.json", "Chamados API v1");
        c.RoutePrefix = "swagger";
    });
}

// ⚠️ IMPORTANTE: UseCors deve vir ANTES de UseAuthorization
app.UseCors("AllowAll");

app.UseHttpsRedirection();

app.UseAuthorization();

app.MapControllers();

app.UseRouting();

app.UseStaticFiles();

app.Run();