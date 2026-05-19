package analisadorlexico;

public class LLMSuggester {
    
    public static String suggestFix(String errorToken, int line, int column, String contextLine) {
        String suggestion;
        String possibleFix;
        
        switch (errorToken) {
            case "@":
                suggestion = " '@' não é válido em Pascal";
                possibleFix = " Substitua por ':' (dois pontos) ou remova";
                break;
            case "#":
                suggestion = "'#' não faz parte do Pascal";
                possibleFix = "Remova este caractere";
                break;
            case "$":
                suggestion = "'$' não é usado em Pascal padrão";
                possibleFix = "Use identificadores sem '$'";
                break;
            case "&":
                suggestion = "'&' não é operador em Pascal";
                possibleFix = "Use 'and' (palavra reservada) se necessário";
                break;
            default:
                suggestion = "Caractere inválido: '" + errorToken + "'";
                possibleFix = "Verifique se não é um operador mal escrito";
                break;
        }
        
        String result = suggestion + "\n" + possibleFix;
        
        if (contextLine != null && !contextLine.isEmpty()) {
            result += "\n Contexto: \"" + contextLine + "\"";
        }
        
        return result;
    }
}