import java.awt.*; // Importa as classes para criar interfaces gráficas com Swing.
import javax.swing.*; // Importa classes de layout e componentes gráficos;

public class ControleFinanceiroGUI {
    private ControleFinanceiro controleFinanceiro; // Instância da classe ControleFinanceiro que gerencia os dados financeiros.

    // Método principal que inicia a aplicação.
    public static void main(String[] args) {
        // Executa a criação da GUI na thread de despacho de eventos do Swing.
        SwingUtilities.invokeLater(() -> new ControleFinanceiroGUI().createAndShowGUI());
    }

    // Método para criar e mostrar a interface gráfica.
    public void createAndShowGUI() {
        controleFinanceiro = new ControleFinanceiro(); // Inicializa a instância de ControleFinanceiro.

        JFrame frame = new JFrame("Controle Financeiro"); // Cria a janela principal.
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Define o comportamento de fechamento da janela.
        frame.setSize(400, 300); // Define o tamanho da janela.

        JPanel panel = new JPanel(new GridLayout(6, 2)); // Cria um painel com um layout em grade.

        // Campos de entrada para os valores do cartão e nota fiscal.
        JTextField campoCartao = new JTextField();
        JTextField campoNota = new JTextField();
        JTextArea resultado = new JTextArea(); // Área para mostrar os resultados.
        resultado.setEditable(false); // Torna a área de resultados não editável.

        // Botões para adicionar valores, finalizar o processo e exibir o histórico.
        JButton adicionarButton = new JButton("Adicionar");
        JButton finalizarButton = new JButton("Finalizar");
        JButton historicoButton = new JButton("Histórico"); // Novo botão para mostrar o histórico.

        // Ação para adicionar valores ao clicar no botão "Adicionar".
        adicionarButton.addActionListener(e -> adicionarValores(campoCartao, campoNota, resultado));
        // Ação para finalizar o processo ao clicar no botão "Finalizar".
        finalizarButton.addActionListener(e -> finalizarProcesso(resultado, campoCartao, campoNota, adicionarButton, finalizarButton));
        // Ação para mostrar o histórico ao clicar no botão "Histórico".
        historicoButton.addActionListener(e -> mostrarHistorico());

        // Adiciona componentes ao painel.
        panel.add(new JLabel("Valor do Cartão:")); // Rótulo para o campo do cartão.
        panel.add(campoCartao); // Campo de entrada para o cartão.
        panel.add(new JLabel("Valor da Nota Fiscal:")); // Rótulo para o campo da nota.
        panel.add(campoNota); // Campo de entrada para a nota.
        panel.add(adicionarButton); // Botão para adicionar.
        panel.add(finalizarButton); // Botão para finalizar.
        panel.add(historicoButton); // Botão para mostrar o histórico.
        panel.add(new JLabel("Resultados:")); // Rótulo para a área de resultados.
        panel.add(new JScrollPane(resultado)); // Área de resultados em um painel rolável.

        frame.add(panel); // Adiciona o painel à janela.
        frame.setVisible(true); // Torna a janela visível.
    }

    // Método para adicionar valores dos campos ao controle financeiro.
    private void adicionarValores(JTextField campoCartao, JTextField campoNota, JTextArea resultado) {
        try {
            // Verifica se o campo do cartão não está vazio e adiciona o valor.
            if (!campoCartao.getText().isEmpty()) {
                controleFinanceiro.adicionarCartao(Double.parseDouble(campoCartao.getText()));
            }

            // Verifica se o campo da nota não está vazio e adiciona o valor.
            if (!campoNota.getText().isEmpty()) {
                controleFinanceiro.adicionarNota(Double.parseDouble(campoNota.getText()));
            }

            // Atualiza a área de resultados após adicionar os valores.
            atualizarResultados(resultado);
            campoCartao.setText(""); // Limpa o campo do cartão.
            campoNota.setText(""); // Limpa o campo da nota.
        } catch (NumberFormatException ex) {
            // Mostra uma mensagem de erro se os valores inseridos não forem válidos.
            JOptionPane.showMessageDialog(null, "Por favor, insira valores válidos.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método para atualizar a área de resultados com os totais e a diferença.
    private void atualizarResultados(JTextArea resultado) {
        resultado.setText(String.format("Total de Cartões: R$ %.2f\nTotal de Notas: R$ %.2f",
                controleFinanceiro.getTotalCartao(), controleFinanceiro.getTotalNotas()));
    }

    // Método para mostrar o histórico de transações.
    private void mostrarHistorico() {
        StringBuilder historico = new StringBuilder("Histórico de Valores:\n");
        
        // Adiciona os valores individuais do histórico de cartões.
        historico.append("Valores do Cartão:\n");
        for (Double valor : controleFinanceiro.getHistoricoCartao()) {
            historico.append(String.format("R$ %.2f\n", valor));
        }

        // Adiciona os valores individuais do histórico de notas.
        historico.append("\nValores da Nota Fiscal:\n");
        for (Double valor : controleFinanceiro.getHistoricoNotas()) {
            historico.append(String.format("R$ %.2f\n", valor));
        }

        // Adiciona os totais e a diferença.
        historico.append("\nTotal de Cartões: R$ ").append(String.format("%.2f", controleFinanceiro.getTotalCartao()));
        historico.append("\nTotal de Notas: R$ ").append(String.format("%.2f", controleFinanceiro.getTotalNotas()));
        historico.append("\nDiferença: R$ ").append(String.format("%.2f", controleFinanceiro.getDiferenca()));

        JOptionPane.showMessageDialog(null, historico.toString(), "Histórico", JOptionPane.INFORMATION_MESSAGE);
    }

    // Método para finalizar o processo, desativando campos e botões.
    private void finalizarProcesso(JTextArea resultado, JTextField campoCartao, JTextField campoNota, JButton adicionarButton, JButton finalizarButton) {
        resultado.setText("Processo finalizado."); // Mostra mensagem de finalização.
        campoCartao.setEnabled(false); // Desativa o campo do cartão.
        campoNota.setEnabled(false); // Desativa o campo da nota.
        adicionarButton.setEnabled(false); // Desativa o botão "Adicionar".
        finalizarButton.setEnabled(false); // Desativa o botão "Finalizar".
        controleFinanceiro.finalizar(); // Chama o método para finalizar o controle financeiro.

        // Mostra a diferença total em uma nova janela.
        JOptionPane.showMessageDialog(null, String.format("Diferença total: R$ %.2f", controleFinanceiro.getDiferenca()), "Diferença Total", JOptionPane.INFORMATION_MESSAGE);
    }
}
