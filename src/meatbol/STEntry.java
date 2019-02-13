package meatbol;

public class STEntry
{
    protected String symbol;
    protected Classif primClassif;

    public STEntry() {
        this.symbol = "";
        this.primClassif = Classif.EMPTY;
    }

    public STEntry(String symbol, Classif primClassif)
    {
        this.symbol = symbol;
        this.primClassif = primClassif;
    }
}
