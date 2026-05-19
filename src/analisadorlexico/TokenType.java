package analisadorlexico;

public enum TokenType {
    // Palavras reservadas
    PROGRAM, VAR, BEGIN, END, IF, THEN, ELSE, WHILE, DO, READ, WRITE, 
    INTEGER, REAL, AND, OR, NOT,
    
    // Identificadores e literais
    IDENTIFIER, NUMBER_INT, NUMBER_REAL, STRING,
    
    // Operadores
    PLUS, MINUS, MULTIPLY, DIVIDE, ASSIGN, EQUALS, NOT_EQUALS, 
    LESS, GREATER, LESS_EQ, GREATER_EQ,
    
    // Delimitadores
    SEMICOLON, COMMA, DOT, COLON, LPAREN, RPAREN,
    
    // Comentários
    COMMENT,
    
    // Especiais
    ERROR, EOF
}