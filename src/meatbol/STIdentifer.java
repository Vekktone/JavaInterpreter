package meatbol;

public class STIdentifer extends STEntry
{
    public SubClassif type;
    public SubClassif structure;
    public SubClassif parm;
    public int nonLocal;

    public STIdentifer(String symbol, Classif primClassif, SubClassif type, SubClassif structure, SubClassif parm
            , int nonLocal)
    {
        super(symbol, primClassif);
        this.type = type;
        this.structure = structure;
        this.parm = parm;
        this.nonLocal = nonLocal;
    }
}
