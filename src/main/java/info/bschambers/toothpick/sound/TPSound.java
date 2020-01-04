package info.bschambers.toothpick.sound;

public interface TPSound {

    public static final TPSound NULL = new TPSound() {
            @Override
            public void sfxExplode() {
                System.out.println("TPSound.NULL.sfxExplode()");
            }
        };

    void sfxExplode();

}
