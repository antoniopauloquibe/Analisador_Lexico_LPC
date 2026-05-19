package analisadorlexico;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;

public class MainFrame extends JFrame {
    
    private JTextArea codeArea;
    private JTable tokenTable;
    private DefaultTableModel tableModel;
    private JTextArea errorArea;
    private JLabel statusLabel;
    private SymbolTable currentSymbolTable;
    
    public MainFrame() {
        setTitle("Analisador Léxico - Mini Pascal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        currentSymbolTable = new SymbolTable();
        initComponents();
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
       
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        JButton analyzeBtn = createButton("Analisar", new Color(76, 175, 80));
        analyzeBtn.addActionListener(e -> analyzeCode());
        
        JButton clearBtn = createButton("Limpar", new Color(255, 100, 100));
        clearBtn.addActionListener(e -> clearAll());
        
        JButton symbolBtn = createButton("Tabela Símbolos", new Color(33, 150, 243));
        symbolBtn.addActionListener(e -> showSymbolTableDialog());
        
        buttonPanel.add(analyzeBtn);
        buttonPanel.add(clearBtn);
        buttonPanel.add(symbolBtn);
        
        
        JPanel codePanel = new JPanel(new BorderLayout());
        codePanel.setBorder(BorderFactory.createTitledBorder("Código Fonte Mini-Pascal"));
        
        codeArea = new JTextArea();
        codeArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        codeArea.setTabSize(4);
        JScrollPane codeScroll = new JScrollPane(codeArea);
        codeScroll.setPreferredSize(new Dimension(800, 300));
        codePanel.add(codeScroll, BorderLayout.CENTER);
        
       
        JPanel tokenPanel = new JPanel(new BorderLayout());
        tokenPanel.setBorder(BorderFactory.createTitledBorder("Tabela de Tokens"));
        
        String[] columns = {"Tipo", "Lexema", "Linha", "Coluna", "ID Tabela"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tokenTable = new JTable(tableModel);
        tokenTable.getTableHeader().setReorderingAllowed(false);
        tokenTable.setRowHeight(22);
        JScrollPane tableScroll = new JScrollPane(tokenTable);
        tableScroll.setPreferredSize(new Dimension(800, 200));
        tokenPanel.add(tableScroll, BorderLayout.CENTER);
        
       
        JPanel errorPanel = new JPanel(new BorderLayout());
        errorPanel.setBorder(BorderFactory.createTitledBorder("Erros e Sugestões IA"));
        
        errorArea = new JTextArea();
        errorArea.setEditable(false);
        errorArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        errorArea.setBackground(new Color(255, 245, 245));
        JScrollPane errorScroll = new JScrollPane(errorArea);
        errorScroll.setPreferredSize(new Dimension(800, 130));
        errorPanel.add(errorScroll, BorderLayout.CENTER);
        
        
        statusLabel = new JLabel("Pronto. Clique em 'Analisar' para iniciar.");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
       
        JSplitPane topSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, codePanel, tokenPanel);
        topSplit.setResizeWeight(0.55);
        
        JSplitPane mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topSplit, errorPanel);
        mainSplit.setResizeWeight(0.7);
        
        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        mainPanel.add(mainSplit, BorderLayout.CENTER);
        mainPanel.add(statusLabel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        
        codeArea.setText("");
    }
    
    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return button;
    }
    
    private void analyzeCode() {
        String source = codeArea.getText();
        if (source.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum código para analisar.", 
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
       
        tableModel.setRowCount(0);
        errorArea.setText("");
        currentSymbolTable.clear();
        
        Lexer lexer = new Lexer(source);
        java.util.List<Token> tokens = lexer.tokenize();
        
        StringBuilder errors = new StringBuilder();
        int errorCount = 0;
        int validTokenCount = 0;
        
        
        for (Token token : tokens) {
            if (token.getType() == TokenType.IDENTIFIER) {
                int id = currentSymbolTable.addSymbol(token.getValue());
                token.setSymbolId(id);
            }
        }
        
       
        for (Token token : tokens) {
            if (token.getType() == TokenType.ERROR) {
                errorCount++;
                errors.append(formatError(token, errorCount, source));
            } else if (token.getType() != TokenType.EOF) {
                validTokenCount++;
                String symbolId = token.getSymbolId() == -1 ? "---" : String.valueOf(token.getSymbolId());
                tableModel.addRow(new Object[]{
                    token.getType(),
                    token.getValue(),
                    token.getLine(),
                    token.getColumn(),
                    symbolId
                });
            }
        }
        
        
        errors.append("\n");
        errors.append("╔══════════════════════════════════════════════════════════════╗\n");
        errors.append(String.format("║RESUMO DA ANÁLISE                                    ║\n"));
        errors.append("╠══════════════════════════════════════════════════════════════╣\n");
        errors.append(String.format("║Tokens válidos: %-39d ║\n", validTokenCount));
        errors.append(String.format("║Erros léxicos: %-41d ║\n", errorCount));
        errors.append(String.format("║Identificadores únicos: %-34d ║\n", currentSymbolTable.getSize()));
        errors.append("╚══════════════════════════════════════════════════════════════╝\n");
        
        if (errorCount == 0) {
            statusLabel.setText(String.format("Sucesso! %d tokens gerados, %d símbolos identificados.", 
                    validTokenCount, currentSymbolTable.getSize()));
            errorArea.setBackground(new Color(240, 255, 240));
        } else {
            statusLabel.setText(String.format("%d erro(s) encontrado(s). Verifique a área de erros.", errorCount));
            errorArea.setBackground(new Color(255, 240, 240));
        }
        
        errorArea.setText(errors.toString());
        errorArea.setCaretPosition(0);
    }
    
    private String formatError(Token token, int errorNumber, String source) {
        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════════════════════════════════╗\n");
        sb.append(String.format("║  ERRO #%d                                                                  ║\n", errorNumber));
        sb.append("╠══════════════════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║  Linha: %-4d  |  Coluna: %-4d                                    ║\n", 
                token.getLine(), token.getColumn()));
        sb.append("╠══════════════════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║  Caractere inválido: '%s'                                          ║\n", token.getValue()));
        sb.append("╠══════════════════════════════════════════════════════════════════╣\n");
        
        String suggestion = LLMSuggester.suggestFix(
            token.getValue(), 
            token.getLine(), 
            token.getColumn(),
            getLineContent(source, token.getLine())
        );
        
        String[] lines = suggestion.split("\n");
        for (String line : lines) {
            sb.append(String.format("║ %-68s ║\n", line));
        }
        
        sb.append("╚══════════════════════════════════════════════════════════════════╝\n\n");
        return sb.toString();
    }
    
    private String getLineContent(String source, int lineNumber) {
        String[] lines = source.split("\n");
        if (lineNumber - 1 >= 0 && lineNumber - 1 < lines.length) {
            return lines[lineNumber - 1].trim();
        }
        return "";
    }
    
    private void showSymbolTableDialog() {
        if (currentSymbolTable.getSize() == 0) {
            JOptionPane.showMessageDialog(this, 
                    "Nenhum símbolo na tabela. Execute uma análise primeiro.", 
                    "Tabela Vazia", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog(this, "Tabela de Símbolos", true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        
        String[] columns = {"ID", "Lexema", "Tipo"};
        DefaultTableModel symModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        for (Map.Entry<String, Integer> entry : currentSymbolTable.getAllSymbols().entrySet()) {
            symModel.addRow(new Object[]{
                entry.getValue(), 
                entry.getKey(), 
                "Identificador"
            });
        }
        
        JTable symTable = new JTable(symModel);
        symTable.setRowHeight(22);
        symTable.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scroll = new JScrollPane(symTable);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(scroll, BorderLayout.CENTER);
        
        JLabel infoLabel = new JLabel(String.format("Total de símbolos: %d", currentSymbolTable.getSize()));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.add(infoLabel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void clearAll() {
        codeArea.setText("");
        tableModel.setRowCount(0);
        errorArea.setText("");
        currentSymbolTable.clear();
        statusLabel.setText("Limpo. Pronto para nova análise.");
        errorArea.setBackground(null);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}