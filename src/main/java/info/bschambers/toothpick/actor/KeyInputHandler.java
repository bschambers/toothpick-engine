package info.bschambers.toothpick.actor;

import info.bschambers.toothpick.game.TPProgram;

public abstract class KeyInputHandler implements TPBehaviour {

    public static final KeyInputHandler NULL = new KeyInputHandler() {
            @Override
            public void update(TPProgram prog, TPActor a) {}
        };

    protected double xyStep = 1;
    protected double angleStep = 0.005;

    protected Binding bindUp      = new Binding(81); // q
    protected Binding bindDown    = new Binding(65); // a
    protected Binding bindLeft    = new Binding(87); // w
    protected Binding bindRight   = new Binding(69); // e
    protected Binding bindX       = new Binding(90); // z
    protected Binding bindZoomIn  = new Binding(49); // 1
    protected Binding bindZoomOut = new Binding(50); // 2

    private Binding[] bindings = new Binding[] { bindUp,
                                                 bindDown,
                                                 bindLeft,
                                                 bindRight,
                                                 bindX,
                                                 bindZoomIn,
                                                 bindZoomOut };

    public void setKey(int keyCode, boolean val) {
        // System.out.println(getClass().getSimpleName() + ".setKey() --> "
        //                    + "keyCode=" + keyCode + " value=" + val);
        for (Binding b : bindings)
            if (keyCode == b.code())
                b.setValue(val);
    }

    public class Binding {

        public int keyCode = 0;
        public boolean value = false;

        public Binding(int keyCode) { this.keyCode = keyCode; }

        public int code() { return keyCode; }

        public void setCode(int val) { keyCode = val; }

        public boolean value() { return value; }

        public void setValue(boolean val) { value = val; }
    }

}