package game2D;

import java.awt.Point;

public class Creature extends Sprite {

	private static final int DIE_TIME = 1000;

	//int values for states
	public static final int STATE_NORMAL = 0;
	public static final int STATE_DYING = 1;
	public static final int STATE_DEAD = 2;

	//animations for this creature
	private Animation idleLeft;
	private Animation idleRight;
	private Animation walkLeft;
	private Animation walkRight;
	private Animation dyingLeft;
	private Animation dyingRight;

	protected boolean facing_right = true;
	protected boolean facing_left = false;
	private boolean attacking = false;

	private int state;
	private long stateTime;

	private boolean onGround;

	
	//useful constants
	public static final float JUMP_SPEED = -0.2f;
	public static final float MAX_FALLING_SPEED = 0.25f;
	public static final float MAX_SPEED = 0.08f;

	public Creature(Animation idleLeft, Animation idleRight, Animation walkLeft,
			Animation walkRight, Animation deadLeft, Animation deadRight) {
		super(idleRight);
		this.idleLeft = idleLeft;
		this.idleRight = idleRight;
		this.walkLeft = walkLeft;
		this.walkRight = walkRight;
		this.dyingLeft = deadLeft;
		this.dyingRight = deadRight;

		state = STATE_NORMAL;
	}

	/**
    Gets the state of this Creature. The state is either
    STATE_NORMAL, STATE_DYING, or STATE_DEAD.
	 */
	public int getState() {
		return state;
	}


	/**
	    Sets the state of this Creature to STATE_NORMAL,
	    STATE_DYING, or STATE_DEAD.
	 */
	public void setState(int state) {
		if (this.state != state) {
			this.state = state;
			stateTime = 0;
			if (state == STATE_DYING) {
				setVelocityX(0);
				setVelocityY(0);
			}
		}
	}

	/**
	 * @return whether or not this creature is alive
	 */
	public boolean isAlive() {
		return (state == STATE_NORMAL);
	}

	/**
	 *     Updates the animaton for this creature.
	 */
	public void update(long elapsedTime) {

		// select the correct Animation
		super.update(elapsedTime);
		Animation newAnim = anim;

		if (this.getState() == STATE_NORMAL) {
			if (getVelocityX() == 0) {
				if (facing_right) {	
					newAnim = idleRight;
				} else if (facing_left) {
					newAnim = idleLeft;
				}
			}
			else if (getVelocityX() < 0) {
				newAnim = walkLeft;

				facing_left = true;
				facing_right = false;
			}
			else if (getVelocityX() > 0) {
				newAnim = walkRight;
				facing_right = true;
				facing_left = false;
			}
		} else if (state == STATE_DYING && (newAnim == idleLeft || newAnim == walkLeft)) {
			newAnim = dyingLeft;
		}
		else if (state == STATE_DYING && (newAnim == idleRight || newAnim == walkRight)) {
			newAnim = dyingRight;
		}

		// update the Animation
		if (anim != newAnim) {
			anim = newAnim;
			anim.start();
		}
		else {
			anim.update(elapsedTime);
		}

		// update to "dead" state
		stateTime += elapsedTime;
		if (state == STATE_DYING && stateTime >= DIE_TIME) {
			newAnim.pauseAt(newAnim.getNoOfFrames()-1);
			setState(STATE_DEAD);
		}
	}


	/**
	 * sets the velocity for this creature
	 */
	public void setVelocityY(float dy) {
		if (dy < MAX_FALLING_SPEED) {
			super.setVelocityY(dy);
		}
	}

	/**
    Makes the player jump if the player is on the ground or
    if forceJump is true.
	 */
	public void jump(boolean forceJump) {
		if (onGround || forceJump) {
			onGround = false;
			setVelocityY(JUMP_SPEED);
		}
	}


	/**
	 * used to set the state flag onGround to true when sprite falls and hits the ground
	 */
	public void collideVertical() {
		// check if collided with ground

		 if (getVelocityY() > 0) {
	            onGround = true;
	        }
		super.setVelocityY(0);    
	}

	/**
	 * 
	 * @return an array list containing corner points and mid points of sprite
	 */
	public Point[] getCorners() {
		Point[] corners = new Point[6];

		int sX = (int)this.getX();
		int sY = (int)this.getY();

		Point topLeft = new Point(sX+5, sY);
		Point midTop = new Point(sX + this.getWidth()/2, sY);
		Point topRight = new Point(sX + this.getWidth()-5, sY);
		Point bottomRight = new Point(sX + this.getWidth()-5, sY + this.getHeight());
		Point midBottom = new Point(sX + this.getWidth()/2, sY + this.getHeight());
		Point bottomLeft = new Point(sX+5, sY + this.getHeight());

		corners[0] = topLeft;
		corners[1] = midTop;
		corners[2] = topRight;
		corners[3] = bottomRight;
		corners[4] = midBottom;
		corners[5] = bottomLeft;

		return corners;
	}

	/**
	 * 
	 * @return arraylist containing sprite left and right midpoints
	 */
	public Point[] getHorizontalPoints() {
		Point[] corners = new Point[2];

		int sX = (int)this.getX();
		int sY = (int)this.getY();

		Point midLeft = new Point(sX, sY + this.getHeight()/2);
		Point midRight = new Point(sX + this.getWidth(), sY + this.getHeight()/2);


		corners[0] = midRight;
		corners[1] = midLeft;
		

		return corners;
	}

	/**
	 * 
	 * @return arraylist containing sprite top and bottom midpoints
	 */
	public Point[] getVerticalPoints() {
		Point[] corners = new Point[2];

		int sX = (int)this.getX();
		int sY = (int)this.getY();

		Point midTop = new Point(sX+this.getWidth()/2, sY);
		Point midBottom = new Point(sX+this.getWidth()/2, sY + this.getHeight());

		corners[0] = midTop;

		corners[1] = midBottom;

		return corners;
	}


}
