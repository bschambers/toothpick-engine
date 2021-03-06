package info.bstancham.toothpick.ui;

public interface TPMenuItem {

    enum Code { RET, CANCEL, HIDE, UP, DOWN, LEFT, RIGHT, UNDEFINED }

    /**
     * This is the text which will appear on the menu.<br/>
     *
     * This should return a single (short) line of text. The title or a brief description
     * of the menu item.
     */
    String text();

    /**
     * This method is run when the item is selected.<br/>
     *
     * You can get around the restrictions of having no return value by instantiating the
     * menu-item as an anonymous inner class inside a method.  Then you can use this to
     * assign variables local to the containing method!
     */
    void action(Code c, int keyCode);

    static final TPMenuItem SPACER = new TPMenuItem() {
            @Override
            public String text() { return ""; }
            @Override
            public void action(Code c, int keyCode) {}
        };

}
