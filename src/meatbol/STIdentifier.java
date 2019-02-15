package meatbol;

public class STIdentifier extends STEntry {
    /** Primitive type of the identifier. For homogeneous arrays
     * this is the underlying primitive type of each value.
     * <p>
     * Current types include: INT, FLOAT, STRING, BOOL, DATE */
    //TODO: might need to add a default type for more complex
    //data structures.
    SubClassif declareType;

    /** Structure of the identifier.
     * <p>
     * Current types include: PRIMITIVE, FIXED, UNBOUNDED */
    SubClassif structure;

    /** How the identifier is passed as a parameter
     * <p>
     * Current types include: NOT, REFERENCE, VALUE */
    SubClassif paramType;

    /** Indicates the location of the base address of the identifier
     * <p>
     * Use 0 for local and 99 for global. In all other cases, this indicates
     * how many functions above this one to go to reach this identifier's
     * stack address.
     */
    int nonlocal;

    public STIdentifier(String symbol, Classif primClassif, SubClassif type, SubClassif struct, SubClassif param, int address){
        super(symbol, primClassif);
        this.declareType = type;
        this.structure = struct;
        this.paramType = param;
        this.nonlocal = address;
    }

    public STIdentifier copy(STIdentifier other){
        return new STIdentifier(other.symbol, other.primClassif, other.declareType, other.structure
                , other.paramType, other.nonlocal);
    }

    public String toString(){
        return (symbol + " " + primClassif.toString() + " " + declareType.toString()+ " " + structure.toString()
                + " " + paramType.toString() + " " + nonlocal);
    }
}
