package meatbol;

import java.util.ArrayList;

public class STFunction extends STEntry
{
    public static final int VAR_ARGS = -1;

    public SubClassif returnType;
    public SubClassif definedBy;
    public int numArgs;
    public ArrayList<Object> parmList;
    public SymbolTable symbolTable;

    public STFunction(String symbol, Classif primClassif, SubClassif returnType, SubClassif definedBy, int numArgs)
    {
        super(symbol, primClassif);
        this.returnType = returnType;
        this.definedBy = definedBy;
        this.numArgs = numArgs;
    }
}
