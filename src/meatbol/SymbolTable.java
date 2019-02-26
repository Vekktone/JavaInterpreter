package meatbol;

import java.util.HashMap;

/** A class that maps symbols represented as Strings to STEntries.
 *
 * @author Mason Pohler
 * @author Reviewed by Riley Marfin, Mason Pohler, Gregory Pugh
 */
public class SymbolTable
{
    /** The hashmap used internally by the SymbolTable. */
    private HashMap<String, STEntry> table;

    /** The constructor for the SymbolTable. It creates the hashmap and
     * inserts the global symbols into the SymbolTable.
     */
    public SymbolTable()
    {
        this.table = new HashMap<String, STEntry>();
        initGlobal();
    }

    /** Used to retrieve an STEntry that is mapped to a symbol.
     * <p>
     * The STEntry is only retrieved if the matching symbol is within the
     * SymbolTable.
     * TODO: if not, should throw error: unknown symbol
     *
     * @param string
     * 			string representing a symbol within the SymbolTable.
     *
     * @return COPY OF STEntry mapped to the symbol.
     *
     * @throws Exception if symbol is not within the SymbolTable's hashmap.
     *
     * @author Mason Pohler
     * @author Gregory Pugh (modified: 2-24-2019)
     * @author Mason Pohler (modified: 2-25-2019)
     */
    public STEntry getSymbol(String string) throws IllegalAccessError
    {
        // if it exists, return it
        if (table.containsKey(string)){
            return table.get(string).copy();
        }
        else
            throw new IllegalAccessError("Symbol \"" + string + "\" is not within this symbol table.");
    }

    /** A put function used to map a symbol to an STEntry. The symbol - STEntry
     * relationship is only added if the symbol is not already within the
     * SymbolTable.
     *
     * @param entry
     * 				Entry to be mapped to the symbol.
     *
     * @author Mason Pohler
     */
    public void putSymbol(STEntry entry)
    {
        //check to see if this already exists
        if (!table.containsKey(entry.symbol))
            table.put(entry.symbol, entry);
    }

    /** A function that inserts the global symbols and their matching STEntries into
     * the SymbolTable.
     *
     * @author Mason Pohler
     */
    private void initGlobal()
    {
        // Control flow/end
        table.put("def", new STControl("def", Classif.CONTROL, SubClassif.FLOW, SubClassif.EMPTY));
        table.put("enddef", new STControl("enddef", Classif.CONTROL, SubClassif.END, SubClassif.EMPTY));
        table.put("if", new STControl("if", Classif.CONTROL, SubClassif.FLOW, SubClassif.EMPTY));
        table.put("endif", new STControl("endif", Classif.CONTROL, SubClassif.END, SubClassif.EMPTY));
        table.put("else", new STControl("else", Classif.CONTROL, SubClassif.END, SubClassif.EMPTY));
        table.put("for", new STControl("for", Classif.CONTROL, SubClassif.FLOW, SubClassif.EMPTY));
        table.put("endfor", new STControl("endfor", Classif.CONTROL, SubClassif.END, SubClassif.EMPTY));
        table.put("while", new STControl("while", Classif.CONTROL, SubClassif.FLOW, SubClassif.EMPTY));
        table.put("endwhile", new STControl("endwhile", Classif.CONTROL, SubClassif.END, SubClassif.EMPTY));

        // Function void
        table.put("print", new STFunction("print", Classif.FUNCTION, SubClassif.VOID, STFunction.VAR_ARGS));

        // Control declare
        table.put("Int", new STControl("Int", Classif.CONTROL, SubClassif.DECLARE, SubClassif.INTEGER));
        table.put("Float", new STControl("Float", Classif.CONTROL, SubClassif.DECLARE, SubClassif.FLOAT));
        table.put("String", new STControl("String", Classif.CONTROL, SubClassif.DECLARE, SubClassif.STRING));
        table.put("Bool", new STControl("Bool", Classif.CONTROL, SubClassif.DECLARE, SubClassif.BOOLEAN));
        table.put("Date", new STControl("Date", Classif.CONTROL, SubClassif.DECLARE, SubClassif.DATE));

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




