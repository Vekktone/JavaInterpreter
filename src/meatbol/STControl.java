package meatbol;

/**
 * Symbol Table Entry for control functions
 * <p>
 * Subclass of STEntry for control entry types.
 *
 * @author Mason Pohler
 * @author Gregory (modified ?)
 * @author Reviewed by Riley Marfin, Mason Pohler, and Gregory Pugh
 */

public class STControl extends STEntry
{
    /** Subclassification of control type entries.
     * <p>
     * Types used are FLOW, END, and DECLARE.
     */
    SubClassif subClassif;

    /** Basic Constructor, used in initGlobal or new Entry
     *
     * @param symbol
     * 			Text representation of the entry
     * @param primClassif
     * 			Primary Classification of the Entry (same as Token)
     * @param subClassif
     * 			Secondary Classification of the Entry (same as Token)
     *
     * @author Mason Pohler
     */
    public STControl(String symbol, Classif primClassif, SubClassif subClassif)
    {
        super(symbol, primClassif);
        this.subClassif = subClassif;
    }

    /** Creates a deep copy of a STControl entry
     * <p>
     * Used to avoid unintentional changes when passing by reference.
     *
     * @param other
     * 			The entry from which to make a copy
     *
     * @return copy of the original STControl
     *
     * @author Gregory Pugh
     */
    public STControl copy(STControl other)
    {
        return new STControl(other.symbol, other.primClassif, other.subClassif);
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
        System.out.printf("%-12s %-12s %s", symbol, primClassif.toString()
                ,this.subClassif.toString());
    }
}
