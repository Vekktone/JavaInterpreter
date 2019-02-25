package meatbol;

import java.util.ArrayList;

/** Symbol table entry for functions
 * <p>
 * Subclass of STEntry for function entry types.
 *
 * @author Mason Pohler
 * @author Gregory (modified ?)
 * @author Reviewed by Riley Marfin, Mason Pohler, and Gregory Pugh
 */
public class STFunction extends STEntry
{
    /** Constant for variable number of arguments */
    public final static int VAR_ARGS = -1;
    /** Type of the return value */
    SubClassif retType;
    /** Identifies whether the function is built-in or user-defined.
     * <p>
     * 0 is built-in, 1 is user defined.
     */
    SubClassif definedBy;
    /** Number of formal parameters used by function.
     * <p>
     * -1 denotes VAR_ARGS.
     */
    int numArgs;
    /** Array list of STIdentifiers used as formal parameters by this function. */
    ArrayList<STEntry> paramList;
    /** SymbolTable containing STEntries for the function.
     * <p>
     * Used for built-in functions load by initGlobal()).
     */
    SymbolTable symbolTable;

    /** Constructor for built-in functions in initGlobal()
     * <p>
     * Return types are IDENTIFIER, INTEGER, FLOAT, BOOLEAN, STRING, DATE, VOID
     * User types are BUILTIN and USER
     *
     * @author Mason Pohler
     * @author Gregory Pugh (modified ?)
     */
    public STFunction(String symbol, Classif primClassif, SubClassif returnType, int numArgs)
    {
        super(symbol, primClassif);
        this.retType = returnType;
        this.definedBy = SubClassif.BUILTIN;
        this.numArgs = numArgs;
    }

    /** Constructor for user defined functions from source code
     * * <p>
     * Return types are INTEGER, FLOAT, BOOLEAN, STRING, DATE, VOID
     * User types are BUILTIN and USER
     *
     * @author Mason Pohler
     * @author Gregory Pugh (modified ?)
     */
    public STFunction(String symbol, Classif primClassif, SubClassif returnType, int numArgs,
            ArrayList<STEntry> paramList, SymbolTable funcTable)
    {
        super(symbol, primClassif);
        this.retType = returnType;
        this.definedBy = SubClassif.USER;
        this.numArgs = numArgs;
        this.paramList = paramList;
        this.symbolTable = funcTable;
    }

    /** Creates a deep copy of a STFunction entry
     * <p>
     * Used to avoid unintentional changes when passing by reference.
     *
     * @param other
     * 			The entry from which to make a copy
     *
     * @return copy of the original STFunction
     *
     * @author Gregory Pugh
     */
    public STFunction copy(STFunction other)
    {
        return new STFunction(other.symbol, other.primClassif, other.retType
                , other.numArgs, other.paramList, other.symbolTable);
    }

    /** Prints formated data of entry.
     * <p>
     * Convenience function for printing symbol tables and error checking
     *
     * @author Gregory Pugh
     */
    @Override
    public void printEntry()
    {
        System.out.printf("%-12s %-12s %-12s %-12s %d", symbol, primClassif.toString()
                , retType.toString(), definedBy.toString(), numArgs);
    }
}