import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ControleFinanceiro {
    private double totalCartao = 0;
    private double totalNotas = 0;
    private Connection conn;
    private List<Double> historicoCartao = new ArrayList<>(); 
    private List<Double> historicoNotas = new ArrayList<>(); 

    public ControleFinanceiro() {
        try {
            Class.forName("org.sqlite.JDBC");

            // Utiliza um caminho relativo ou o diretório home do usuário
            String dbPath = getDatabasePath();
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath); 

            criarTabelaSeNecessario();
            carregarTotais();
            System.out.println("Conexão com o banco de dados estabelecida.");
        } catch (ClassNotFoundException e) {
            System.err.println("Erro ao carregar o driver JDBC do SQLite: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Erro ao conectar ao banco de dados: " + e.getMessage());
        }
    }

    private String getDatabasePath() {
        // Aqui, você pode escolher entre caminhos relativos ou diretórios específicos
        // Exemplo de uso de diretório home do usuário
        String userHome = System.getProperty("user.home");
        
        // Caminho relativo ou em uma pasta específica
        String dbPath = userHome + "/controle_financeiro.db";  // Banco de dados na pasta home do usuário
        // Alternativamente, se você quiser usar um caminho relativo (dentro do projeto), use:
        // String dbPath = "./controle_financeiro.db";

        return dbPath;
    }

    private void criarTabelaSeNecessario() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS transacoes (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "total_cartao REAL, " +
                     "total_notas REAL)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
            System.out.println("Tabela 'transacoes' criada ou já existe.");
        } catch (SQLException e) {
            System.err.println("Erro ao criar a tabela: " + e.getMessage());
            throw e; 
        }
    }

    private void carregarTotais() throws SQLException {
        String sql = "SELECT total_cartao, total_notas FROM transacoes ORDER BY id DESC LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                totalCartao = rs.getDouble("total_cartao");
                totalNotas = rs.getDouble("total_notas");
                System.out.println("Totais carregados: Cartão = R$ " + totalCartao + ", Notas = R$ " + totalNotas);
            } else {
                System.out.println("Nenhuma transação encontrada, valores iniciais zerados.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao carregar os totais: " + e.getMessage());
            throw e; 
        }
    }

    public void adicionarCartao(double valor) {
        if (conn == null) {
            System.err.println("A conexão é nula. Não é possível adicionar cartão.");
            return; 
        }
        totalCartao += valor;
        historicoCartao.add(valor); // Adiciona o valor à lista de histórico
        salvarTotais();
    }

    public void adicionarNota(double valor) {
        if (conn == null) {
            System.err.println("A conexão é nula. Não é possível adicionar nota.");
            return; 
        }
        totalNotas += valor;
        historicoNotas.add(valor); // Adiciona o valor à lista de histórico
        salvarTotais();
    }

    public List<Double> getHistoricoCartao() {
        return historicoCartao; // Retorna o histórico de cartões
    }

    public List<Double> getHistoricoNotas() {
        return historicoNotas; // Retorna o histórico de notas
    }

    public double getTotalCartao() {
        return totalCartao;
    }

    public double getTotalNotas() {
        return totalNotas;
    }

    public double getDiferenca() {
        return totalCartao - totalNotas;
    }

    public void finalizar() {
        totalCartao = 0;
        totalNotas = 0;
        salvarTotais();
        fecharConexao();
    }

    private void salvarTotais() {
        if (conn == null) {
            System.err.println("A conexão é nula. Não é possível salvar os totais.");
            return; 
        }
        String sql = "INSERT INTO transacoes (total_cartao, total_notas) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, totalCartao);
            stmt.setDouble(2, totalNotas);
            stmt.executeUpdate();
            System.out.println("Totais salvos no banco de dados: Cartão = R$ " + totalCartao + ", Notas = R$ " + totalNotas);
        } catch (SQLException e) {
            System.err.println("Erro ao salvar no banco de dados: " + e.getMessage());
        }
    }

    private void fecharConexao() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Conexão com o banco de dados fechada.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar a conexão com o banco de dados: " + e.getMessage());
        }
    }
}
