package info.bschambers.toothpick.actor;

import info.bschambers.toothpick.TPProgram;

public class KeyInputThrust extends KeyInputHandler {

    @Override
    public KeyInputThrust copy() {
        KeyInputThrust a = new KeyInputThrust();
        duplicateParameters(a);
        return a;
    }

    @Override
    public void update(TPProgram prog, TPActor tp) {
        super.update(prog, tp);
        if (bindUp.value()) {
            tp.x += Math.sin(Math.PI * tp.angle) * xyStep;
            tp.y -= Math.cos(Math.PI * tp.angle) * xyStep;
        }
        if (bindDown.value()) {
            tp.x -= Math.sin(Math.PI * tp.angle) * xyStep;
            tp.y += Math.cos(Math.PI * tp.angle) * xyStep;
        }
        if (bindLeft.value()) {
            tp.angle -= angleStep;
        }
        if (bindRight.value()) {
            tp.angle += angleStep;
        }
    }

}