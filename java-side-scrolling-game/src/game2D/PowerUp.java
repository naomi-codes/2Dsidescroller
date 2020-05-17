package game2D;

/**
    A PowerUp class is a Sprite that the player can pick up.
*/
public abstract class PowerUp extends Sprite {

	boolean collected;
    public PowerUp(Animation anim) {
        super(anim);
        collected = false;
    }


    public boolean isCollected() {
		return collected;
	}

	public void setCollected(boolean collected) {
		this.collected = collected;
	}


	/**
        A Star PowerUp. Gives the player points.
    */
    public static class Crystal extends PowerUp {
        public Crystal(Animation anim) {
            super(anim);
        }
    }

    /**
        A Goal PowerUp. Advances to the next map.
    */
    public static class Goal extends PowerUp {
        public Goal(Animation anim) {
            super(anim);
        }
    }

}
