package info.bstancham.toothpick.actor;

import info.bstancham.toothpick.TPEncoding;
import info.bstancham.toothpick.TPEncodingHelper;
import info.bstancham.toothpick.TPGeometry;
import info.bstancham.toothpick.TPProgram;
import info.bstancham.toothpick.geom.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * NEW ANGLE (winding is clockwise because of reversed Y-direction in window):
 * - right = 0
 * - down  = PI * 0.5
 * - left  = PI
 * - up    = PI * 1.5
 *
 *
 *
 *

 * angle:
 * - straight up = 0
 * - right = 0.5
 * - down = 1
 * - right = 1.5
 *
 * RADIANS
 * Math.PI * angle:
 * - straight up = 0
 * - right = 0.5
 * - down = 1
 * - right = 1.5
 *
 * DEGREES
 * Math.toDegrees(Math.PI * angle):
 * - straight up = 0
 * - right = 90
 * - down = 180
 * - right = 270
 */
public class TPActor implements TPEncodingHelper {

    public enum BoundaryBehaviour {
        DO_NOTHING_AT_BOUNDS,
        DIE_AT_BOUNDS,
        WRAP_AT_BOUNDS,
        WRAP_PARTS_AT_BOUNDS
    };

    public static final TPActor NULL = new TPActor(new TPForm());

    private Color bgColor = Color.BLACK;

    public String name = "UNNAMED ACTOR";
    private TPForm form;
    private TPActor parent = null;
    private List<TPActor> children = new ArrayList<>();
    private List<TPActor> childrenToAdd = new ArrayList<>();
    private List<TPActor> childrenToRemove = new ArrayList<>();
    private List<ActorBehaviour> behaviours = new ArrayList<>();
    private BoundaryBehaviour boundsBehaviour = BoundaryBehaviour.WRAP_AT_BOUNDS;
    private ColorGetter color = new ColorMono(Color.PINK);
    private ColorGetter vertexColor = null;
    private boolean actionTrigger = false;
    private TriggerBehaviour trigAction = null;
    private boolean isPlayer = false;
    public double x = 0;
    public double y = 0;
    public double angle = 0;
    public double xInertia = 0;
    public double yInertia = 0;
    public double angleInertia = 0;
    public int numDeaths = 0;
    public int numKills = 0;

    public TPActor() {
        this(new TPForm());
    }

    public TPActor(TPForm form) {
        setForm(form);
    }

    public String infoString() {
        StringBuffer s = new StringBuffer();
        s.append("name: " + name + "\n");
        s.append("instance: " + super.toString() + "\n");
        s.append("alive: " + isAlive() + "\n");
        s.append("position: x=" + x + " y=" + y + "\n");
        s.append("inertia: x=" + xInertia + " y=" + yInertia + "\n");
        s.append("angle:           " + angle + " (angle-inertia=" + angleInertia + ")\n");
        s.append("angle (RADIANS): " + (Math.PI * angle) + " (angle-inertia=" + angleInertia + ")\n");
        s.append("angle (DEGREES): " + Math.toDegrees(Math.PI * angle) + " (angle-inertia=" + angleInertia + ")\n");
        s.append("bounds-behaviour: " + boundsBehaviour + "\n");
        s.append("trigger-action: " + trigAction + "\n");
        s.append("is-player: " + isPlayer + "\n");
        s.append("FORM: (" + form.numParts() + " parts)\n");
        for (int i = 0; i < form.numParts(); i++)
            s.append("... " + form.getPart(i) + "\n");
        s.append("parent: " + parent + "\n");
        s.append("num-children: " + children.size() + "\n");
        s.append("stats: deaths=" + numDeaths + " kills=" + numKills + "\n");
        s.append("BEHAVIOURS:\n");
        for (ActorBehaviour b : behaviours)
            s.append("... " + b + "\n");
        return s.toString();
    }

    public TPActor copy() {
        TPActor actor = new TPActor(form.copy());
        actor.name = name;
        actor.boundsBehaviour = boundsBehaviour;
        actor.trigAction = trigAction;
        actor.isPlayer = isPlayer;
        actor.x = x;
        actor.y = y;
        actor.angle = angle;
        actor.xInertia = xInertia;
        actor.yInertia = yInertia;
        actor.angleInertia = angleInertia;
        actor.numDeaths = numDeaths;
        actor.numKills = numKills;
        actor.color = color.copy();
        if (vertexColor != null)
            actor.vertexColor = vertexColor.copy();
        for (ActorBehaviour b : behaviours)
            actor.behaviours.add(b);
        actor.updateForm();
        return actor;
    }

    public void copyStats(TPActor tp) {
        numDeaths = tp.numDeaths;
        numKills = tp.numKills;
    }

    /**
     * <p>often null</p>
     */
    public TPActor getParent() {
        return parent;
    }

    public TPForm getForm() {
        return form;
    }

    public void setForm(TPForm form) {
        this.form = form;
        this.form.setActor(this);
    }

    public BoundaryBehaviour getBoundaryBehaviour() {
        return boundsBehaviour;
    }

    public void setBoundaryBehaviour(BoundaryBehaviour val) {
        boundsBehaviour = val;
    }

    public boolean getActionTrigger() {
        return actionTrigger;
    }

    public void setActionTrigger(boolean val) {
        actionTrigger = val;
    }

    public void setTriggerBehaviour(TriggerBehaviour action) {
        trigAction = action;
    }

    public boolean isPlayer() { return isPlayer; }

    public void setIsPlayer(boolean val) { isPlayer = val; }

    public Color getColor() {
        // return color.get();
        return color.getWithBG(bgColor);
    }

    public void setColorGetter(ColorGetter cg) {
        color = cg;
    }

    /**
     * WARNING! may return null
     */
    public Color getVertexColor() {
        if (vertexColor == null)
            return null;
        return vertexColor.getWithBG(bgColor);
    }

    public void setVertexColorGetter(ColorGetter cg) {
        vertexColor = cg;
    }

    public boolean isAlive() {
        return form.isAlive();
    }

    public int numChildren() {
        return children.size();
    }

    public TPActor getChild(int index) {
        return children.get(index);
    }

    public void addChild(TPActor child) {
        childrenToAdd.add(child);
    }

    /**
     * <p>Add a behaviour. If behaviour {@code tpb} is a member of a singleton-group then
     * any existing member of that group will be removed.</p>
     */
    public void addBehaviour(ActorBehaviour ab) {
        String group = ab.getSingletonGroup();
        if (!group.isEmpty()) {
            List<ActorBehaviour> abToRemove = new ArrayList<>();
            for (ActorBehaviour other : behaviours)
                if (other.getSingletonGroup().equals(group))
                    abToRemove.add(other);
            for (ActorBehaviour other : abToRemove)
                behaviours.remove(other);
        }
        behaviours.add(ab);
    }

    /**
     * Removes any existing input handlers and adds this one.
     */
    public void setInputHandler(KeyInputHandler newInput) {
        // KeyInputHandler will be identified by it's singleton-group ID
        addBehaviour(newInput);
    }

    public void update(TPProgram prog) {

        bgColor = prog.getBGColor();

        x += xInertia;
        y += yInertia;
        angle += angleInertia;

        boundaryCheckPosition(prog);

        updateForm();

        if (boundsBehaviour == BoundaryBehaviour.WRAP_PARTS_AT_BOUNDS)
            wrapFormAtBounds(prog.getGeometry());

        for (ActorBehaviour ab : behaviours)
            ab.update(prog, this);

        // remove children as required
        for (TPActor child : children)
            if (!child.isAlive())
                childrenToRemove.add(child);
        for (TPActor child : childrenToRemove)
            children.remove(child);
        childrenToRemove.clear();

        // add children as required
        for (TPActor child : childrenToAdd) {
            child.parent = this;
            children.add(child);
        }
        childrenToAdd.clear();

        for (TPActor child : children)
            child.update(prog);

        if (trigAction != null)
            trigAction.update(this, actionTrigger);
    }

    /**
     * Updates form in accordance with current angle and position.
     */
    public void updateForm() {
        form.update(this);
    }

    private void wrapFormAtBounds(TPGeometry geom) {
        form.wrapAtBounds(geom);
    }

    private void boundaryCheckPosition(TPProgram prog) {
        if (boundsBehaviour == BoundaryBehaviour.DIE_AT_BOUNDS) {
            TPGeometry geom = prog.getGeometry();
            if (x < 0 ||
                x > geom.getWidth() ||
                y < 0 ||
                y > geom.getHeight())
                // if form is empty then actor will die
                setForm(new TPForm());
        } else if (boundsBehaviour == BoundaryBehaviour.WRAP_AT_BOUNDS ||
                   boundsBehaviour == BoundaryBehaviour.WRAP_PARTS_AT_BOUNDS) {
            TPGeometry geom = prog.getGeometry();
            if (x < 0)
                x = geom.getWidth();
            else if (x > geom.getWidth())
                x = 0;
            else if (y < 0)
                y = geom.getHeight();
            else if (y > geom.getHeight())
                y = 0;
        }
    }

    public void setPos(Pt pos) {
        x = pos.x;
        y = pos.y;
    }

    public void deathEvent(TPLink protagonist, Pt p) {
        numDeaths++;
    }

    public void killEvent(TPLink victim, Pt p) {
        numKills++;
    }

    /**
     * Find center of form and set as center of actor.
     */
    public void autosetCenter() {
        int numNodes = getForm().numNodes();
        if (numNodes > 0) {
            // get initial high and low values
            Node n = getForm().getNode(0);
            // Node n2 = getForm().getLink(0).getEndNode();
            double x1 = n.getXArchetype();
            double x2 = n.getXArchetype();
            double y1 = n.getYArchetype();
            double y2 = n.getYArchetype();
            // find highest and lowest values
            int i = 1;
            while (i < numNodes) {
                n = getForm().getNode(i++);
                if (n.getXArchetype() < x1) x1 = n.getXArchetype();
                if (n.getXArchetype() > x2) x2 = n.getXArchetype();
                if (n.getYArchetype() < y1) y1 = n.getYArchetype();
                if (n.getYArchetype() > y2) y2 = n.getYArchetype();
            }
            // mid point
            double xMid = Geom.midVal(x1, x2);
            double yMid = Geom.midVal(y1, y2);
            // shift each line to be centered on zero
            i = 0;
            while (i < numNodes) {
                n = getForm().getNode(i++);
                n.update(0, 0, 0); // reset to archetype
                n.update(0, -xMid, -yMid); // shift to new position
                n.resetArchetype(); // set archetype to current position
            }
            // set position of actor to mid point
            x = xMid;
            y = yMid;
        }
    }

    /*---------------------------- Encoding ----------------------------*/

    @Override
    public TPEncoding getEncoding() {
        TPEncoding params = new TPEncoding();
        params.addField(String.class, name, "name");
        params.addField(Double.class, x, "x");
        params.addField(Double.class, y, "y");
        params.addField(Double.class, angle, "angle");
        params.addField(Double.class, xInertia, "xInertia");
        params.addField(Double.class, yInertia, "yInertia");
        params.addField(Double.class, angleInertia, "angleInertia");
        params.addMethod(Double.class, isPlayer(), "setIsPlayer");
        params.addField(Integer.class, numDeaths, "numDeaths");
        params.addField(Integer.class, numKills, "numKills");
        params.addMethod(ColorGetter.class, color, "setColorGetter");
        params.addMethod(TPForm.class, getForm(), "setForm");
        params.addListMethod(ActorBehaviour.class, behaviours, "addBehaviour");
        params.addListMethod(TPActor.class, children, "addChild");
        params.addVoidMethod("updateForm");
        return params;
    }

}
