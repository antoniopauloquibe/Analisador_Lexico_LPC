package analisadorlexico;

public class Token {
    private TokenType type;
    private String value;
    private int line;
    private int column;
    private int symbolId;

    public Token(TokenType type, String value, int line, int column) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.column = column;
        this.symbolId = -1;
    }

    public TokenType getType() { return type; }
    public String getValue() { return value; }
    public int getLine() { return line; }
    public int getColumn() { return column; }
    public int getSymbolId() { return symbolId; }
    public void setSymbolId(int symbolId) { this.symbolId = symbolId; }
    public boolean isIdentifier() { return type == TokenType.IDENTIFIER; }

    @Override
    public String toString() {
        return String.format("%-15s | %-15s | %4d | %4d | %s", 
            type, value, line, column, symbolId == -1 ? "---" : symbolId);
    }
}