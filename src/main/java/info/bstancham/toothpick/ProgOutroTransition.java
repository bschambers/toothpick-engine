package info.bstancham.toothpick;

import info.bstancham.toothpick.actor.TPActor;
import info.bstancham.toothpick.actor.TPFactory;

/**
 * <p>Waits until {@code TPProgram.isFinished()} returns true, sets program not-finished
 * and runs a simple outro-sequence, then sets the program as finished again.</p>
 *
 * <ul>
 * <li>The Outro-message slides up from the bottom of the screen and into the center of
 * the play-area.</li>
 * <li>After a short delay, the whole play-area slides off the left hand side of the
 * screen.</li>
 * </ul>
 */
public class ProgOutroTransition implements ProgramBehaviour {

    private String message;
    private boolean triggered = false;
    private boolean finished = false;
    private double offsetGoal = 0;
    private double offset = 0;
    private int messagePause = 1000;
    private int messageCount = 0;
    private TPActor textActor = null;

    public ProgOutroTransition() {
        this("...");
    }

    public ProgOutroTransition(String message) {
        this.message = message;
    }

    @Override
    public ProgOutroTransition copy() {
        ProgOutroTransition out = new ProgOutroTransition(message);
        out.triggered = triggered;
        out.finished = finished;
        out.offsetGoal = offsetGoal;
        out.offset = offset;
        out.messagePause = messagePause;
        out.messageCount = messageCount;
        if (textActor != null)
            out.textActor = textActor.copy();
        return out;
    }

    @Override
    public String getSingletonGroup() {
        return ProgramBehaviour.OUTRO_TRANSITION_ID;
    }

    @Override
    public void reset() {
        triggered = false;
        messageCount = 0;
    }

    @Override
    public void update(TPProgram prog) {
        if (prog.isFinished() && !triggered) {
            // on discovering that program is finished - set prog back to not-finished and
            // begin outro-sequence
            prog.setFinished(false);
            triggered = true;
            TPGeometry geom = prog.getGeometry();
            offsetGoal = geom.xOffset - geom.getWidth();
            offset = geom.xOffset;
            textActor = TPFactory.textActor(prog, message);
            textActor.setPos(TPFactory.centerPos(prog));
            textActor.x = geom.getWidth() / 2;
            textActor.y = geom.getHeight();
            textActor.yInertia = -2;
            prog.addActor(textActor);
        } else if (triggered) {
            // wait until message reached center of screen before stopping
            if (textActor.y <= prog.getGeometry().getHeight() / 2) {
                textActor.yInertia = 0;
                if (messageCount < messagePause) {
                    // delay for a while
                    messageCount++;
                } else if (offset > offsetGoal) {
                    // slide the play-area off screen to the left
                    offset -= 4;
                    prog.getGeometry().xOffset = offset;
                } else {
                    // allow program to finish now
                    prog.setFinished(true);
                }
            }
        }
    }

}
