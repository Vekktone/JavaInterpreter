package meatbol;

/**
 * Symbol Table entry superclass.
 *
 * @author Mason Pohler
 * @author Gregory Pugh (modified ?)
 * @author Reviewed by Riley Marfin, Mason Pohler, and Gregory Pugh
 */
public class STEntry
{
    /** String representation of the entry. */
    String symbol;
    /** Primary classification of the entry
     * <p>
     * Uses same classification as Token.
     */
    Classif primClassif;

    /** Constructor
     * <p>
     * Used directly for operations and indirectly as super for other
     * entries
     *
     * @param symbol
     * 			Text representation of entry.
     * @param primClassif
     * 			Primary classification of entry
     *
     * @author Mason Pohler
     */
    public STEntry(String symbol, Classif primClassif)
    {
        this.symbol = symbol;
        this.primClassif = primClassif;
    }

    /** Creates a deep copy of a STEntry for operation entries
     * <p>
     * Used to avoid unintentional changes when passing by reference.
     *
     * @param other
     * 			The entry from which to make a copy
     *
     * @return copy of the STEntry
     *
     * @author Gregory Pugh
     */
    public STEntry copy()
    {
        return new STEntry(this.symbol,this.primClassif);
    }

    /** Prints formated data of entry.
     * <p>
     * Convenience function for printing symbol tables and error checking
     *
     * @author Gregory Pugh
     */
    public void printEntry()
    {
        System.out.printf("%-12s %s", symbol, primClassif.toString());
    }
}
