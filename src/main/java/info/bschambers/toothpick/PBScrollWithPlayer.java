package info.bschambers.toothpick;

import info.bschambers.toothpick.actor.TPActor;

/**
 * Scrolls with player 1.
 */
public class PBScrollWithPlayer implements ProgramBehaviour {

    private TPActor player = null;

    @Override
    public PBScrollWithPlayer copy() {
        return new PBScrollWithPlayer();
    }

    @Override
    public void update(TPProgram prog) {

        // find player
        if (player == null && prog.numPlayers() > 0)
            if (prog.getPlayer(0).getActor().isAlive())
                player = prog.getPlayer(0).getActor();

        // update scrolling
        if (player != null) {
            TPGeometry g = prog.getGeometry();
            g.xOffset = -player.x;
            g.yOffset = -player.y;

            // don't keep following a dead player
            if (!player.isAlive())
                player = null;
        }
    }

}
