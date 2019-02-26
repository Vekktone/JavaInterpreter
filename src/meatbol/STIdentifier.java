package meatbol;

/** Symbol table entry for identifiers
 * <p>
 * Subclass of STEntry for identity entry types.
 *
 * @author Mason Pohler
 * @author Gregory (modified ?)
 * @author Reviewed by Riley Marfin, Mason Pohler, and Gregory Pugh
 */
public class STIdentifier extends STEntry
{
    /** Primitive type of the identifier. For homogeneous arrays this is the
     * underlying primitive type of each value. */
    // TODO: might need to add a default type for more complex
    // data structures.
    SubClassif declareType;
    /** Structure of the identifier. */
    SubClassif structure;
    /** How the identifier is passed as a parameter */
    SubClassif paramType;
    /** Indicates the location of the base address of the identifier
     * <p>
     * Use 0 for local and 99 for global. In all other cases, this indicates how
     * many functions above this one to go to reach this identifier's stack
     * address.
     */
    int nonlocal;

    /** Constructor
     * * <p>
     * Declare types are INTEGER, FLOAT, BOOLEAN, STRING, DATE
     * Structure types are PRIMITIVE, FIXED, UNBOUNDED
     * Parameter types are NOT, REFERENCE, VALUE
     *
     * @author Mason Pohler
     */
    public STIdentifier(String symbol, Classif primClassif, SubClassif type
            , SubClassif struct, SubClassif param, int address)
    {
        super(symbol, primClassif);
        this.declareType = type;
        this.structure = struct;
        this.paramType = param;
        this.nonlocal = address;
    }

    /** Creates a deep copy of a STIdentifier entry
     * <p>
     * Used to avoid unintentional changes when passing by reference.
     *
     * @param other
     * 			The entry from which to make a copy
     *
     * @return copy of the original STFunction
     *
     * @author Gregory Pugh
     */
    public STIdentifier copy(STIdentifier other)
    {
        return new STIdentifier(other.symbol, other.primClassif, other.declareType
                , other.structure, other.paramType, other.nonlocal);
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
        System.out.printf("%-12s %-12s %-12s %-12s %-12s %d", symbol, primClassif.toString()
                , declareType.toString(), structure.toString(), paramType.toString()
                , nonlocal);
    }
}
