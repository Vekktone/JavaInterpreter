package meatbol;

import java.util.ArrayList;

/**Symbol table entry for functions
 *
 * @author Mason Pohler
 * @author Reviewed by Gregory Pugh
 */
public class STFunction extends STEntry {

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
     * -1 denotes VAR_ARGS. (VAR_ARGS is declared)*/
    int numArgs;

    /** Array list of STIdentifiers used as formal parameters by this
     * function.
     */
    ArrayList<STEntry> paramList;

    /** SymbolTable containing STEntries for the function.
     * <p>
     * Used for built-in functions load by initGlobal()).
     */
    SymbolTable symbolTable;

    /** Basic Constructor, used in initGlobal */
    public STFunction(String symbol, Classif primClassif, SubClassif returnType
            , int numArgs)
    {
        super(symbol, primClassif);
        this.retType = returnType;
        this.definedBy = SubClassif.BUILTIN;
        this.numArgs = numArgs;
    }

    /** Constructor for user defined functions */
    public STFunction(String symbol, Classif primClassif, SubClassif returnType
            , int numArgs, ArrayList<STEntry> paramList, SymbolTable funcTable)
    {
        super(symbol, primClassif);
        this.retType = returnType;
        this.definedBy = SubClassif.USER;
        this.numArgs = numArgs;
        this.paramList = paramList;
        this.symbolTable = funcTable;
    }

    public STFunction copy(STFunction other){
        return new STFunction(other.symbol, other.primClassif, other.retType, other.numArgs
                , other.paramList, other.symbolTable);
    }

    public String toString(){
        return (symbol + " " + primClassif.toString() + " " + retType.toString()+ " " + definedBy.toString()
                + " " + numArgs);
    }
}