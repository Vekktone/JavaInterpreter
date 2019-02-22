package meatbol;

import java.util.HashMap;

/**
 * A class that maps symbols represented as Strings to STEntries.
 * @author Mason Pohler
 * @author reviewed by Gregory Pugh
 */
public class SymbolTable
{
    /**
     * The hashmap used internally by the SymbolTable.
     */
    private HashMap<String, STEntry> table;

    /**
     * The constructor for the SymbolTable. It creates the hashmap and
     * inserts the global symbols into the SymbolTable.
     */
    public SymbolTable()
    {
        this.table = new HashMap<String, STEntry>();
        initGlobal();
    }

    /**
     * A get function used to retrieve an STEntry that is mapped to a symbol.
     * The STEntry is only retrieved if the matching symbol is within the
     * SymbolTable.
     * @param symbol a string representing a symbol within the SymbolTable.
     * @return an STEntry that is mapped to the symbol.
     * @throws Exception if symbol is not within the SymbolTable's hashmap.
     */
    public STEntry getSymbol(String symbol) throws Exception
    {
        if (table.containsKey(symbol))
        {
            return table.get(symbol);
        }
        else
        {
            throw new Exception("Symbol \"" + symbol + "\" is not within this symbol table.");
        }
    }

    /**
     * A put function used to map a symbol to an STEntry. The symbol - STEntry
     * relationship is only added if the symbol is not already within the
     * SymbolTable.
     * @param symbol a string representing a symbol to be mapped to an STEntry.
     * @param entry an STEntry to be mapped to the symbol.
     */
    public void putSymbol(String symbol, STEntry entry)
    {
        if (!table.containsKey(symbol))
        {
            table.put(symbol, entry);
        }
    }

    /**
     * A function that inserts the global symbols and their matching STEntries into
     * the SymbolTable.
     */
    private void initGlobal()
    {
        // Control flow/end
        table.put("def", new STControl("def", Classif.CONTROL, SubClassif.FLOW));
        table.put("enddef", new STControl("enddef", Classif.CONTROL, SubClassif.END));
        table.put("if", new STControl("if", Classif.CONTROL, SubClassif.FLOW));
        table.put("endif", new STControl("endif", Classif.CONTROL, SubClassif.END));
        table.put("else", new STControl("else", Classif.CONTROL, SubClassif.END));
        table.put("for", new STControl("for", Classif.CONTROL, SubClassif.FLOW));
        table.put("endfor", new STControl("endfor", Classif.CONTROL, SubClassif.END));
        table.put("while", new STControl("while", Classif.CONTROL, SubClassif.FLOW));
        table.put("endwhile", new STControl("endwhile", Classif.CONTROL, SubClassif.END));

        // Function void
        table.put("print", new STFunction("print", Classif.FUNCTION, SubClassif.VOID, STFunction.VAR_ARGS));

        // Control declare
        table.put("Int", new STControl("Int", Classif.CONTROL, SubClassif.DECLARE));
        table.put("Float", new STControl("Float", Classif.CONTROL, SubClassif.DECLARE));
        table.put("String", new STControl("String", Classif.CONTROL, SubClassif.DECLARE));
        table.put("Bool", new STControl("Bool", Classif.CONTROL, SubClassif.DECLARE));
        table.put("Date", new STControl("Date", Classif.CONTROL, SubClassif.DECLARE));

        // Function int
        // TODO: Put correct values for numArgs in future project
        table.put("LENGTH", new STFunction("LENGTH", Classif.FUNCTION, SubClassif.INTEGER, 0));
        table.put("MAXLENGTH", new STFunction("MAXLENGTH", Classif.FUNCTION, SubClassif.INTEGER, 0));
        table.put("SPACES", new STFunction("SPACES", Classif.FUNCTION, SubClassif.INTEGER, 0));
        table.put("ELEM", new STFunction("ELEM", Classif.FUNCTION, SubClassif.INTEGER, 0));
        table.put("MAXELEM", new STFunction("MAXELEM", Classif.FUNCTION, SubClassif.INTEGER, 0));

        // Operators
        table.put("and", new STEntry("and", Classif.OPERATOR));
        table.put("or", new STEntry("or", Classif.OPERATOR));
        table.put("not", new STEntry("not", Classif.OPERATOR));
        table.put("in", new STEntry("in", Classif.OPERATOR));
        table.put("notin", new STEntry("notin", Classif.OPERATOR));
    }
}
