package analisadorlexico;

import java.util.*;

public class SymbolTable {
    private Map<String, Integer> symbolMap;
    private List<String> symbols;
    private int nextId;
    
    public SymbolTable() {
        symbolMap = new LinkedHashMap<>();
        symbols = new ArrayList<>();
        nextId = 1;
    }
    
    public int addSymbol(String name) {
        if (isReservedWord(name)) {
            return -1;
        }
        
        if (symbolMap.containsKey(name)) {
            return symbolMap.get(name);
        }
        
        symbolMap.put(name, nextId);
        symbols.add(name);
        nextId++;
        return nextId - 1;
    }
    
    private boolean isReservedWord(String word) {
        String[] reserved = {
            "program", "var", "begin", "end", "if", "then", "else",
            "while", "do", "read", "write", "integer", "real", 
            "and", "or", "not"
        };
        for (String r : reserved) {
            if (r.equalsIgnoreCase(word)) {
                return true;
            }
        }
        return false;
    }
    
    public int getSymbolId(String name) {
        return symbolMap.getOrDefault(name, -1);
    }
    
    public String getSymbolName(int id) {
        if (id >= 1 && id <= symbols.size()) {
            return symbols.get(id - 1);
        }
        return null;
    }
    
    public Map<String, Integer> getAllSymbols() {
        return new LinkedHashMap<>(symbolMap);
    }
    
    public int getSize() {
        return symbolMap.size();
    }
    
    public void clear() {
        symbolMap.clear();
        symbols.clear();
        nextId = 1;
    }
    
    public String[][] getTableData() {
        String[][] data = new String[symbolMap.size()][2];
        int i = 0;
        for (Map.Entry<String, Integer> entry : symbolMap.entrySet()) {
            data[i][0] = entry.getValue().toString();
            data[i][1] = entry.getKey();
            i++;
        }
        return data;
    }
}