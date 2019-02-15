package meatbol;

/** Symbol Table Entry for control functions
 *
 * @author Mason Pohler
 * @author Reviewed by Gregory Pugh
 */

public class STControl extends STEntry{
    /** Subclassification of control type entries.
     * <p>
     * Types used are FLOW, END, and DECLARE */
    SubClassif subClassif;

    /** Basic Constructor, used in initGlobal */
    public STControl(String symbol, Classif primClassif, SubClassif subClassif)
    {
        super(symbol, primClassif);
        this.subClassif = subClassif;
    }

    public STControl copy(STControl other){
        return new STControl(other.symbol, other.primClassif, other.subClassif);
    }

    public String toString(){
        return (symbol + " " + primClassif.toString());
    }
}
