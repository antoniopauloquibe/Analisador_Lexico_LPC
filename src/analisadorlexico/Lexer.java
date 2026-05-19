package analisadorlexico;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private String source;
    private int position;
    private int line;
    private int column;
    private char currentChar;

    public Lexer(String source) {
        this.source = source;
        this.position = 0;
        this.line = 1;
        this.column = 1;
        if (source != null && source.length() > 0) {
            this.currentChar = source.charAt(0);
        } else {
            this.currentChar = '\0';
        }
    }

    private void advance() {
        if (currentChar == '\n') {
            line++;
            column = 0;
        }
        position++;
        column++;
        if (position < source.length()) {
            currentChar = source.charAt(position);
        } else {
            currentChar = '\0';
        }
    }

    private void skipWhitespace() {
        while (currentChar != '\0' && Character.isWhitespace(currentChar)) {
            advance();
        }
    }

    private Token readIdentifier() {
        StringBuilder result = new StringBuilder();
        int startLine = line;
        int startCol = column;
        
        while (currentChar != '\0' && (Character.isLetterOrDigit(currentChar) || currentChar == '_')) {
            result.append(currentChar);
            advance();
        }
        
        String word = result.toString().toLowerCase();
        
        
        switch (word) {
            case "program": return new Token(TokenType.PROGRAM, word, startLine, startCol);
            case "var": return new Token(TokenType.VAR, word, startLine, startCol);
            case "begin": return new Token(TokenType.BEGIN, word, startLine, startCol);
            case "end": return new Token(TokenType.END, word, startLine, startCol);
            case "if": return new Token(TokenType.IF, word, startLine, startCol);
            case "then": return new Token(TokenType.THEN, word, startLine, startCol);
            case "else": return new Token(TokenType.ELSE, word, startLine, startCol);
            case "while": return new Token(TokenType.WHILE, word, startLine, startCol);
            case "do": return new Token(TokenType.DO, word, startLine, startCol);
            case "read": return new Token(TokenType.READ, word, startLine, startCol);
            case "write": return new Token(TokenType.WRITE, word, startLine, startCol);
            case "integer": return new Token(TokenType.INTEGER, word, startLine, startCol);
            case "real": return new Token(TokenType.REAL, word, startLine, startCol);
            case "and": return new Token(TokenType.AND, word, startLine, startCol);
            case "or": return new Token(TokenType.OR, word, startLine, startCol);
            case "not": return new Token(TokenType.NOT, word, startLine, startCol);
            default: return new Token(TokenType.IDENTIFIER, result.toString(), startLine, startCol);
        }
    }

    private Token readNumber() {
        StringBuilder result = new StringBuilder();
        int startLine = line;
        int startCol = column;
        boolean isReal = false;
        
        while (currentChar != '\0' && Character.isDigit(currentChar)) {
            result.append(currentChar);
            advance();
        }
        
        if (currentChar == '.') {
            result.append(currentChar);
            advance();
            isReal = true;
            while (currentChar != '\0' && Character.isDigit(currentChar)) {
                result.append(currentChar);
                advance();
            }
        }
        
        if (isReal) {
            return new Token(TokenType.NUMBER_REAL, result.toString(), startLine, startCol);
        } else {
            return new Token(TokenType.NUMBER_INT, result.toString(), startLine, startCol);
        }
    }
    
    private Token readString() {
        StringBuilder result = new StringBuilder();
        int startLine = line;
        int startCol = column;
        advance(); 
        
        while (currentChar != '\0' && currentChar != '"' && currentChar != '\'') {
            result.append(currentChar);
            advance();
        }
        
        if (currentChar == '"' || currentChar == '\'') {
            advance(); 
        }
        
        return new Token(TokenType.STRING, result.toString(), startLine, startCol);
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        
        while (currentChar != '\0') {
            if (Character.isWhitespace(currentChar)) {
                skipWhitespace();
                continue;
            }
            
            if (Character.isLetter(currentChar)) {
                tokens.add(readIdentifier());
                continue;
            }
            
            if (Character.isDigit(currentChar)) {
                tokens.add(readNumber());
                continue;
            }
            
            if (currentChar == '"' || currentChar == '\'') {
                tokens.add(readString());
                continue;
            }
            
            int startLine = line;
            int startCol = column;
            
            switch (currentChar) {
                case '+': tokens.add(new Token(TokenType.PLUS, "+", startLine, startCol)); advance(); break;
                case '-': tokens.add(new Token(TokenType.MINUS, "-", startLine, startCol)); advance(); break;
                case '*': tokens.add(new Token(TokenType.MULTIPLY, "*", startLine, startCol)); advance(); break;
                case '/': tokens.add(new Token(TokenType.DIVIDE, "/", startLine, startCol)); advance(); break;
                
                case ':':
                    advance();
                    if (currentChar == '=') {
                        tokens.add(new Token(TokenType.ASSIGN, ":=", startLine, startCol));
                        advance();
                    } else {
                        tokens.add(new Token(TokenType.COLON, ":", startLine, startCol));
                    }
                    break;
                    
                case '=': tokens.add(new Token(TokenType.EQUALS, "=", startLine, startCol)); advance(); break;
                
                case '<':
                    advance();
                    if (currentChar == '>') {
                        tokens.add(new Token(TokenType.NOT_EQUALS, "<>", startLine, startCol));
                        advance();
                    } else if (currentChar == '=') {
                        tokens.add(new Token(TokenType.LESS_EQ, "<=", startLine, startCol));
                        advance();
                    } else {
                        tokens.add(new Token(TokenType.LESS, "<", startLine, startCol));
                    }
                    break;
                    
                case '>':
                    advance();
                    if (currentChar == '=') {
                        tokens.add(new Token(TokenType.GREATER_EQ, ">=", startLine, startCol));
                        advance();
                    } else {
                        tokens.add(new Token(TokenType.GREATER, ">", startLine, startCol));
                    }
                    break;
                    
                case ';': tokens.add(new Token(TokenType.SEMICOLON, ";", startLine, startCol)); advance(); break;
                case ',': tokens.add(new Token(TokenType.COMMA, ",", startLine, startCol)); advance(); break;
                case '.': tokens.add(new Token(TokenType.DOT, ".", startLine, startCol)); advance(); break;
                case '(': tokens.add(new Token(TokenType.LPAREN, "(", startLine, startCol)); advance(); break;
                case ')': tokens.add(new Token(TokenType.RPAREN, ")", startLine, startCol)); advance(); break;
                
                case '{':
                    advance();
                    while (currentChar != '\0' && currentChar != '}') {
                        advance();
                    }
                    if (currentChar == '}') advance();
                    tokens.add(new Token(TokenType.COMMENT, "{...}", startLine, startCol));
                    break;
                    
                default:
                    tokens.add(new Token(TokenType.ERROR, String.valueOf(currentChar), startLine, startCol));
                    advance();
                    break;
            }
        }
        
        tokens.add(new Token(TokenType.EOF, "FIM", line, column));
        return tokens;
    }
}