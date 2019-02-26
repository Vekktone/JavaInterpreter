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

    /** The type of declaration or empty.
     * <p>
     * Types used are INT, FLOAT, STRING, BOOL, DATE, and EMPTY.
     */
    SubClassif type;

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
    public STControl(String symbol, Classif primClassif, SubClassif subClassif, SubClassif type)
    {
        super(symbol, primClassif);
        this.subClassif = subClassif;
        this.type = type;
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
    public STControl copy()
    {
        return new STControl(this.symbol,this.primClassif,this.subClassif,this.type);
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
