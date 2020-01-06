package info.bschambers.toothpick;

public interface ProgramBehaviour {

    /** Singleton-group ID for physics-models. */
    public static final String PHYSICS_MODEL_ID = "physics-model";

    default String[] getInfoLines() {
        return new String[0];
    }

    /**
     * <p>Gets the name of the singleton-group which this behaviour belongs to, or an
     * empty string if it belongs to none. A program may only have one member instance of
     * any singleton-group. If no group name is given then a program may have multiple
     * instances of this behaviour.</p>
     *
     * @return The name of the singleton-group which this behaviour belongs to, or an
     * empty string if it belongs to none.
     */
    default String getSingletonGroup() {
        return "";
    }

    void update(TPProgram prog);

}
