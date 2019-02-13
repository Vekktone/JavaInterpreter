package meatbol;

public class STControl extends STEntry
{
    public SubClassif subClassif;

    public STControl(String symbol, Classif primClassif, SubClassif subClassif)
    {
        super(symbol, primClassif);
        this.subClassif = subClassif;
    }
}
