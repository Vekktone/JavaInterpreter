package meatbol;

/**Symbol Table entry superclass.
 *
 * @author Gregory Pugh
 */
public class STEntry {

    /** String representation of the entry. */
    String symbol;

    /** Primary classification of the entry
     * <p>
     * Uses same classification as Token */
    Classif primClassif;

    /** Main Constructor, called by subclasses */
    public STEntry(String symbol, Classif primClassif) {
        this.symbol = symbol;
        this.primClassif = primClassif;
    }

    public STEntry copy(STEntry other)
    {
        return new STEntry(other.symbol, other.primClassif);
    }

    public String toString(){
        return (symbol + " " + primClassif.toString());
    }
}
