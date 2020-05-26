package main;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.ImageIcon;

import game2D.*;


// Game demonstrates how we can override the GameCore class
// to create our own 'game'. We usually need to implement at
// least 'draw' and 'update' (not including any local event handling)
// to begin the process. You should also add code to the 'init'
// method that will initialise event handlers etc. By default GameCore
// will handle the 'Escape' key to quit the game but you should
// override this with your own event handler.

/**
 * @author 2425693
 *
 */
@SuppressWarnings("serial")

public class MyGame extends GameCore 
{

	// Useful game constants
	private static final int SCREEN_WIDTH = 900;
	private static final int SCREEN_HEIGHT = 700;
	private static final float	GRAVITY = 0.001f;

	// Game state flags
	private boolean paused = false;
	private boolean jumpIsPressed = false;
	private boolean leftIsPressed = false;
	private boolean rightIsPressed = false;
	private boolean attackIsPressed = false;

	private int level = 0;

	// Game resources	    

	//sprites
	private Creature player;
	private ArrayList<Creature> enemies = new ArrayList<Creature>();
	private ArrayList<PowerUp> crystals = new ArrayList<PowerUp>();
	private PowerUp.Goal treasure;

	//preloaded sound
	private Sound music = new Sound("sounds/music.wav");

	//maps
	private TileMap tmapLevel1 = new TileMap();	// level 1 map
	private TileMap tmapLevel2 = new TileMap(); // level 2 map
	private TileMap currentLevelMap; //

	private ArrayList<Sprite> visibleSprites = new ArrayList<Sprite>();

	// background images
	private Image backgroundLevel1;
	private Image backgroundLevel2;
	private Image background;

	private long total;    //game score

	/**
	 * The obligatory main method that creates
	 * an instance of our class and starts it running
	 * 
	 * @param args	The list of parameters this program might use (ignored)
	 */
	public static void main(String[] args) {
		MyGame gct = new MyGame();
		gct.init();

		// Start in windowed mode with the given screen height and width
		gct.run(false,SCREEN_WIDTH,SCREEN_HEIGHT);

	} //main

	/**
	 * Initialise the class, e.g. set up variables, load images,
	 * create animations, register event handlers
	 */
	public void init()
	{   

		//load background images
		backgroundLevel1 = new ImageIcon("images/level1Background.png").getImage();
		backgroundLevel2 = new ImageIcon("images/level2Background.png").getImage();
		background = backgroundLevel1; //set intial background image

		// Load the tile map and print it out so we can check it is valid
		tmapLevel1.loadMap("maps", "level1.txt");
		tmapLevel2.loadMap("maps", "level2.txt");
		currentLevelMap = tmapLevel1;

		// creates and loads the animations and assigns initial anim to global player
		loadPlayer();

		//loads goal sprite
		loadTreasure();


		// Create 5 enemies at random positions off the screen
		// to the right
		for (int e=0; e<3; e++)
		{
			enemies.add(loadEnemySprite());
		}

		//create 10 power ups
		for (int c= 0; c<10; c++) {
			Animation anim = new Animation();
			anim.addFrame(loadImage("images/powerups/crystal01.png"), 150);
			crystals.add(new PowerUp.Crystal(anim));
		}

		initialiseGame();

	} //init


	/**
	 * You will probably want to put code to restart a game in
	 * a separate method so that you can call it to restart
	 * the game.
	 */
	public void initialiseGame()
	{
		//initialise the score
		total = 0;

		//set player speed and location
		player.setX(20);
		player.setY(0);
		player.setVelocityX(0);
		player.setVelocityY(0);
		player.show();


		//set enemies speed and locations
		for (Creature e:enemies) {
			//e.setX(400);
			e.setX(SCREEN_WIDTH + (int)(Math.random()*600.0f));
			e.setY(currentLevelMap.getPixelHeight()-2*e.getHeight());
			e.setVelocityX(-0.01f);
			e.show();

			visibleSprites.add(e);
		}

		//set the crystal power up locations
		int cX = currentLevelMap.getTileWidth()*7;

		for (Sprite c: crystals) {
			c.setX(cX);
			cX+= currentLevelMap.getTileWidth();
			c.setY(currentLevelMap.getTileHeight()*3);
			c.show();
			visibleSprites.add(c);
		}

		//set the goal treasure location
		treasure.setX(currentLevelMap.getPixelWidth()-4*currentLevelMap.getTileWidth());
		treasure.setY(currentLevelMap.getPixelHeight()-2*currentLevelMap.getTileHeight());
		treasure.show();
		visibleSprites.add(treasure);

		music.start();

	} //initialiseGame

	
	/**
	 * restart: resets the level, "refreshing all sprites"
	 * @param newLevel - whether or not a new level should be loaded
	 */
	public void restart(boolean newLevel) {
		//clear all lists
		visibleSprites.clear();
		enemies.clear();
		crystals.clear();

		//if the player died and restart was called, clear the score
		if (!newLevel)
			total = 0;


		//set the player location again
		player.setX(20);
		player.setY(0);
		player.setVelocityX(0);
		player.setVelocityY(0);
		player.show();


		///create new enemies
		for (int e=0; e<5; e++)
		{
			enemies.add(loadEnemySprite());
		}

		//create new powerups
		for (int c= 0; c<10; c++) {
			Animation anim = new Animation();
			anim.addFrame(loadImage("images/powerups/crystal01.png"), 150);
			crystals.add(new PowerUp.Crystal(anim));
		}

		//set positions for enemies
		for (Creature e:enemies) {
			e.setX(SCREEN_WIDTH + (int)(Math.random()*300.0f));
			e.setY(currentLevelMap.getPixelHeight()-2*currentLevelMap.getTileHeight());
			e.setVelocityX(-0.01f);
			e.show();
			visibleSprites.add(e);

		}

		//set positions for crystals
		int cX = currentLevelMap.getTileWidth()*7;

		for (Sprite c: crystals) {
			c.setX(cX);
			cX+= currentLevelMap.getTileWidth();
			c.setY(currentLevelMap.getTileHeight()*3);
			c.show();
			visibleSprites.add(c);
		}

		//new goal sprite
		loadTreasure();
		//set its location
		treasure.setX(currentLevelMap.getPixelWidth()-4*currentLevelMap.getTileWidth());
		treasure.setY(currentLevelMap.getPixelHeight()-2*currentLevelMap.getTileHeight());
		treasure.show();
		visibleSprites.add(treasure);

	} // restart

	
	/**
	 *  Pauses the game if not paused by stopping sprite movements
	 */
	private void pause() {
		for (Creature e: enemies) {
			e.stop();
		}

		player.stop();
	} // pause

	/**
	 * Called if the sprite is killed and calls the restart method
	 */
	public void gameOver() {
		pause();
		restart(false);
		player.setState(Creature.STATE_NORMAL);
	} //gameOver


	/**
	 * Draw the current state of the game
	 * 
	 *@param g - the graphics object to draw to  
	 */
	public void draw(Graphics2D g)
	{    	
		//set the game offsets
		int xo = SCREEN_WIDTH/2 - Math.round(player.getX()) - currentLevelMap.getTileWidth();
		int yo = 0;


		//only the x offset is used in the current implementation
		//so set a min and max for it
		xo = Math.min(xo,  0);
		xo = Math.max(xo, SCREEN_WIDTH - currentLevelMap.getPixelWidth());


		//add offsets to the background
		int backgroundX = xo * (SCREEN_WIDTH - background.getWidth(null)) /
				(SCREEN_WIDTH - currentLevelMap.getPixelWidth());

		//draw the background
		g.drawImage(background, backgroundX, 0, null);

		// Apply offsets to tile map and draw  it
		currentLevelMap.draw(g,xo,yo);

		//apply ofsets to goal treasure and draw
		treasure.setOffsets(xo, yo);
		treasure.draw(g);


		//apply offsets to crystals and draw
		Iterator<PowerUp> iC = crystals.iterator();
		while (iC.hasNext()) {
			Sprite crystal = iC.next();
			crystal.setOffsets(xo+currentLevelMap.getTileWidth()/4,currentLevelMap.getTileHeight());
			crystal.setScale((float)0.5);
			crystal.drawTransformed(g);
		}

		//aply offsets to player and draw
		player.setOffsets(xo, yo);
		player.draw(g);

		// Apply offsets to sprites then draw them
		Iterator<Creature> iE = enemies.iterator();
		while (iE.hasNext()) {
			Creature enemy = iE.next();
			enemy.setOffsets(xo,yo);
			enemy.draw(g);
		}


		// Show score and status information
		String msg = String.format("Score: %d", total/100);
		g.setColor(Color.darkGray);
		g.drawString(msg, getWidth() - 80, 50);

	} //draw


	/**
	 * Update any sprites and check for collisions
	 * 
	 * @param elapsed The elapsed time between this call and the previous call of elapsed
	 */    
	public void update(long elapsed)
	{

		//if the player is dead, game over
		if (player.getState() == Creature.STATE_DEAD) {
			gameOver();
		} else if (paused) { //paused, do nothing
			pause();
		} else {
			//apply gravity
			player.setVelocityY(player.getVelocityY()+(GRAVITY*elapsed));

			//set the animation speed for the player
			player.setAnimationSpeed(1.0f);

			// Now update the sprites animation and position

			Iterator<Creature> iE = enemies.iterator();
			while (iE.hasNext()) {
				Creature enemy = (Creature)iE.next();
				if (enemy.getState() == Creature.STATE_DYING) {
					iE.remove();
					visibleSprites.remove(enemy);
				} else {
					enemy.setVelocityY(enemy.getVelocityY()+(GRAVITY*elapsed));
					updateCreature(enemy, elapsed);
					enemy.update(elapsed);
				}
			}

			Iterator<PowerUp> iC = crystals.iterator();
			while (iC.hasNext()) {
				PowerUp crystal = (PowerUp)iC.next();
				if (crystal.isCollected()) {
					iC.remove();
					visibleSprites.remove(crystal);
				}
			}

			treasure.update(elapsed);

			processInput();

			//update player position and animation
			updateCreature(player, elapsed);
			player.update(elapsed);

		}
	} //update


	/**
	 * Processes any input since the last update.
	 * Called by update()
	 */
	public void processInput() {

		if (leftIsPressed) { //left key

			player.setVelocityX(-Creature.MAX_SPEED);

		} else if (rightIsPressed) { //right key
			player.setVelocityX(Creature.MAX_SPEED);

		} else if (leftIsPressed && rightIsPressed) {//no action
			player.setVelocityX(0);
		} else {
			player.setVelocityX(0);
		}

		// if the player is on the ground, jump
		if (jumpIsPressed) {

			if (player.isOnGround()) {
				jumpIsPressed = false;
				player.jump(false);
			} else {
				player.jump(true);
			}
		}

		if (attackIsPressed) {
			player.setAttacking(true);
		} else {
			player.setAttacking(false);
		}
	} // processInput

	/**
	 * 
	 * Updates the sprite passed in, moving their position if possible
	 * otherwise triggering collision handling
	 * 
	 * @param sprite - the sprite to be updated
	 * @param elapsed - duration since start of game
	 */
	private void updateCreature(Creature sprite, long elapsed) {
		//get the current creature speed in each direction
		float dx = sprite.getVelocityX();
		float dy = sprite.getVelocityY();

		if (cornerCollision(sprite,elapsed)) {
			handleTileMapCollisions(sprite, elapsed, false);
		} else {
			//update as expected
			sprite.setX(sprite.getX()+dx*elapsed); 
			sprite.setY(sprite.getY()+dy*elapsed);

			//check for sprite colliison
			if(sprite.equals(player)) {
				checkPlayerCollision(player, true);
			}
			//handle map collisions, player moved is true
			handleTileMapCollisions(sprite, elapsed, true);
		}

	} // updateCreature


	/**
	 * Checks and handles collisions with the tile map for the
	 * given sprite 's'.
	 * 
	 * @param s			The Sprite to check collisions for
	 * @param elapsed	How time has gone by
	 */
	public void handleTileMapCollisions(Creature sprite, long elapsed, boolean spriteMoved)
	{		
		//if the player is at the far left or right of the map
		if (sprite.getX() < 2) 
		{
			sprite.setX(2);
			sprite.setVelocityX(-sprite.getVelocityX());
		} else if (sprite.getX() + sprite.getImage().getWidth(null) >= currentLevelMap.getPixelWidth() - 2) {
			sprite.setVelocityX(-sprite.getVelocityX());
			sprite.setX(currentLevelMap.getPixelWidth() - sprite.getImage().getWidth(null) - 2);	
		}

		float dx = sprite.getVelocityX();
		float dy = sprite.getVelocityY();

		float currY = sprite.getY();

		if (!spriteMoved) { //if the sprite hasn't moved since the last update
			//collisionX(sprite, elapsed);
			int proposedNewX = (int)(sprite.getX()+dx*elapsed);
			moveX(sprite, elapsed, proposedNewX);

			int proposedNewY = (int)(sprite.getY() + dy * elapsed);
			moveY(sprite, elapsed, proposedNewY);

			if (sprite.equals(player)) {
				boolean canKill = (currY < sprite.getY());
				checkPlayerCollision(player, canKill);
			}
		} 
		
		//the player is below the ground
		if (sprite.getY() + sprite.getHeight() >= (currentLevelMap.getPixelHeight() - currentLevelMap.getTileHeight() - 2 )) {
			sprite.setOnGround(true);
			sprite.setUpCount(0);
		}
	} // handleTileMapCollisions


	/**
	 * 
	 * Moves the x position of a sprite after a collision has been detected
	 * 
	 * @param sprite
	 * @param elapsed
	 * @param proposedNewX
	 */
	private void moveX(Creature sprite, long elapsed, int proposedNewX) {
		//current speed of sprite  in the x direction
		float dx = sprite.getVelocityX();
		float dy = sprite.getVelocityY();

		//whether or not there has been a collision
		boolean collision = false;

		int newX = (int)sprite.getX();


		// while we still have not collided and we have less than the proposed amount
		if (sprite.getX() + sprite.getImage().getWidth(null) >= currentLevelMap.getPixelWidth()) {
			sprite.setVelocityX(-sprite.getVelocityX());
			sprite.setX(currentLevelMap.getPixelWidth() - sprite.getImage().getWidth(null) - 2);	
		} else {
			if ( dx > 0) {
				// check if is left collision or right collision
				while (!collision) {
					newX = (int)sprite.getX() + 2;

						int tileX = (int)Math.floor((newX + sprite.getWidth())/currentLevelMap.getTileWidth());
						int tileY = (int)Math.floor((sprite.getY()+sprite.getHeight()/2)/currentLevelMap.getTileHeight());

						int topTileY = tileY -1;
						
						if (currentLevelMap.getTileChar(tileX, tileY) == '.'  && currentLevelMap.getTileChar(tileX, topTileY) == '.' ) {
							sprite.setX(newX);
							collision = true;
							break;
						} else {
							collision = true;
							sprite.setX((currentLevelMap.getTileXC(tileX, tileY)) - sprite.getWidth());
							sprite.setVelocityX(-sprite.getVelocityX());
							sprite.setX(sprite.getX()-2);
							break;
						}

					}
			}

			if ( dx < 0) {
				// check if is left collision or right collision
				while (newX >= proposedNewX) {
					if (!collision) {
						newX = (int)sprite.getX() - 2;

						int tileX = (int)Math.floor(newX/currentLevelMap.getTileWidth());
						int tileY = (int)Math.floor((sprite.getY()+sprite.getHeight()/2)/currentLevelMap.getTileHeight());

						int topTileY = tileY -1;
						
						if (currentLevelMap.getTileChar(tileX, tileY) == '.'  && currentLevelMap.getTileChar(tileX,topTileY) == '.' && currentLevelMap.getTileChar(tileX, tileY) != 'g') {
							sprite.setX(newX);
							break;
						} else {
							collision = true;
							sprite.setX(currentLevelMap.getTileXC(tileX+1, tileY));
							sprite.setX(sprite.getX()+2);
							sprite.setVelocityX(-sprite.getVelocityX());
							break;
						}

					}
				}
			}
		}

	} //moveX


	/**
	 * 
	 * Moves the y position of a sprite after a collision has been detected
	 * 
	 * @param sprite
	 * @param elapsed
	 * @param proposedNewY
	 */
	private void moveY(Creature sprite, long elapsed, int proposedNewY) {

		//current speed of sprite  in the x direction
		float dy = sprite.getVelocityY();

		//whether or not there has been a collision
		boolean collision = false;

		int newY = (int)sprite.getY();

		// while we still have not collided and we have less than the proposed amount

		if ( dy > 0) {
			// check if is top collision or bottom collision
			while (newY <= proposedNewY) {
				if (!collision) {
					newY = (int)sprite.getY() + 2;

					int tileX = (int)Math.floor((sprite.getX()+sprite.getWidth()/2)/currentLevelMap.getTileWidth());
					int tileY = (int)Math.floor((newY + sprite.getHeight())/currentLevelMap.getTileHeight());

					int leftTileX = tileX -1;
					int rightTileX = tileX +1;

					if (currentLevelMap.getTileChar(tileX, tileY) == '.' && currentLevelMap.getTileChar(rightTileX, tileY) == '.' &&
							currentLevelMap.getTileChar(leftTileX, tileY) == '.' && currentLevelMap.getTileChar(tileX, tileY) != 'g' && currentLevelMap.getTileChar(tileX, tileY) != '?') {
						sprite.setY(newY-1);
						collision = true;
						break;
					} else {
						collision = true;
						sprite.setOnGround(collision);
						sprite.setUpCount(0);
						sprite.setVelocityY(0);
						break;
					}

				}
			}
		}

		if ( dy < 0) {
			// check if is left collision or right collision
			while (newY >= proposedNewY) {
				if (!collision) {
					newY = (int)sprite.getY() - 2;

					int tileX = (int)Math.floor((sprite.getX()+sprite.getWidth()/2)/currentLevelMap.getTileWidth());
					int tileY = (int)Math.floor((newY/currentLevelMap.getTileHeight()));

					int leftTileX = tileX -1;
					int rightTileX = tileX +1;

					if (currentLevelMap.getTileChar(tileX, tileY) == '.'  && currentLevelMap.getTileChar(rightTileX, tileY) == '.' &&
							currentLevelMap.getTileChar(leftTileX, tileY) == '.' && currentLevelMap.getTileChar(tileX, tileY) != 'g' && currentLevelMap.getTileChar(tileX, tileY) != 'l' && currentLevelMap.getTileChar(tileX, tileY) != '?') {
						sprite.setY(newY);
						break;
					} else {
						collision = true;
						sprite.setY(currentLevelMap.getTileYC(tileX, tileY+1));
						sprite.setVelocityY(0);
						break;
					}

				}
			}
		}
	} //moveY

	/**
	 * Checks player collision with another sprite
	 * 
	 * @param player
	 * @param canKill
	 */
	private void checkPlayerCollision(Creature player, boolean canKill) {
		if (!player.isAlive()) {
			return;
		}

		// check for player collision with other sprites
		Sprite collisionSprite = getSpriteCollision(player);

		if (collisionSprite instanceof PowerUp.Goal) { //if the collision sprite was the treasure

			collectTreasure(collisionSprite);

		} else if (collisionSprite instanceof PowerUp.Crystal) { 	//if it was a crystal
			collectCrystal(collisionSprite);

		} else if (collisionSprite instanceof Creature) { //if the collision was with an enemy
			Creature enemy = (Creature)collisionSprite;
			// kill the enemy and make player bounce

			killEnemy(player, canKill, enemy);
		}
	} //checkPlayerCollision

	/**
	 * Called when the player is able to kill an enemy sprite
	 * 
	 * @param player
	 * @param canKill
	 * @param enemy
	 */
	private void killEnemy(Creature player, boolean canKill, Creature enemy) {
		if (boundingCircleCollision(player, enemy)) { //if there is also a bounding circle collision with the enemy..
			if (canKill) {								//and the player is moving downwards
				this.leftIsPressed = false;				//force the player to stop moving
				this.rightIsPressed = false;
				enemy.setVelocityX(0);
				player.jump(true);						//make the player jump even though they arent on the ground
				Sound enemyDieSound = new Sound("sounds/enemyhurt.wav"); //load and play enemy hurt sound
				enemyDieSound.start();
				enemy.setState(Creature.STATE_DYING);						//set enemy to dying
				total+=1000;												//increase score
				//player.setY(enemy.getY() - player.getHeight());				//move the player up
			} else {									
				// player dies!
				this.leftIsPressed = false;					//if the player is not moving horizontally
				this.rightIsPressed = false;				//the enemy kills the player

				player.setState(Creature.STATE_DYING);
				Sound s = new Sound("sounds/gameover.wav");
				s.start();
			}
		}
	}

	/**
	 * Called when a player interacts with a crystal
	 * 
	 * @param powerUp
	 */
	private void collectCrystal(Sprite powerUp) {
		if (boundingCircleCollision(player, powerUp)) {
			powerUp.setY(powerUp.getY()-20);		// move the crystal position up	
		total+=2000;											// increase the score
		((PowerUp.Crystal) powerUp).setCollected(true);	// set the crystal to collect
		}
	}

	
	/**
	 * Called when the sprite interacts with the treasure goal
	 * 
	 * @param collisionSprite
	 */
	private void collectTreasure(Sprite collisionSprite) {
		Sound s = new Sound("sounds/goal.wav");
		s.start();								   //load and play the goal sound
		treasure.setAnimationFrame(1);			   //set the treasue frame to open
		((PowerUp.Goal) collisionSprite).setCollected(true); //set the treasure to collected
		visibleSprites.remove(collisionSprite);		//remove the treasure from the list of visible sprites
		level++;									//increment the level
		currentLevelMap = tmapLevel2;				//set the current map to level 2 (as there are only two levels)
		background = backgroundLevel2;				//set the background image to level 2 background
		total+=5000;								//increase the score
		if ( level < 2) {
			restart(true);							//restart new level parameter is true
		} else {
			pause();								//else end of level 2 do nothing
		}
	}


	/**
    Gets the Sprite that collides with the specified Sprite,
    or null if no Sprite collides with the specified Sprite.
	 */
	public Sprite getSpriteCollision(Creature player) {

		// run through the list of Sprites
		Iterator<Sprite> i = visibleSprites.iterator();
		while (i.hasNext()) {
			Sprite otherSprite = (Sprite)i.next();
			if (boundingBoxCollision(player, otherSprite)) {
				// collision found, return the Sprite
				return otherSprite;
				//}
			}
		}

		// no collision found
		return null;
	} //getSpriteCollision

	/**
	 * Checks and handles collisions with the tile map for the
	 * given sprite 's'. Initial functionality is limited...
	 * 
	 * @param s			The Sprite to check collisions for
	 * @param elapsed	How time has gone by
	 */    
	public boolean boundingBoxCollision(Sprite s1, Sprite s2)
	{
		if (s1 == s2) {
			return false;
		}

		Rectangle r1 = s1.getBoundingRectSprite();
		Rectangle r2 = s2.getBoundingRectSprite();

		return r1.intersects(r2);

	}//boundingBoxCollision

	/**
	 * Checks for a bounding circle collision between the player and another
	 * sprite. 
	 * 
	 * @param player
	 * @param enemy collided with
	 * @return whether not the circles bounding the player and the enemy overlap
	 */
	public boolean boundingCircleCollision(Creature player, Sprite sprite) {
		float playerCenterX = player.getX()+player.getWidth()/2;
		float playerCenterY = player.getY()+player.getHeight()/2;

		float enemyCenterX = sprite.getX()+sprite.getWidth()/2;
		float enemyCenterY = sprite.getY()+sprite.getHeight()/2;

		float dx, dy, minimum;

		dx = playerCenterX - enemyCenterX;
		dy = playerCenterY - enemyCenterY;

		float spriteRadius = sprite.getRadius();
		if (sprite instanceof PowerUp) {
			spriteRadius = spriteRadius/4;
		}
		minimum = (player.getRadius() + sprite.getRadius());
		return (((dx * dx) + (dy * dy)) < (minimum*minimum));
	} //boundingCircleCollision


	/**
	 * Checks whether a collision has occurred at any of a sprite's
	 * corners
	 * 
	 * @param sprite
	 * @param elapsed
	 * @return
	 */
	private boolean cornerCollision(Creature sprite, long elapsed) {
		// do this for all four corners - reject movement if true;
		// when you do this for all four corners make sure to "switch off checking for the ground
		// and add a constant to say where ground level is in the tilemap file

		Point[] spriteCorners = sprite.getCorners();

		for (int i = 0; i < spriteCorners.length; i++) {

			float oldX = spriteCorners[i].x;
			float newX = oldX + sprite.getVelocityX()*elapsed;
			float oldY = spriteCorners[i].y;
			float newY = oldY + sprite.getVelocityY()*elapsed;

			int tileX = (int)Math.floor(newX/currentLevelMap.getTileWidth());
			int tileY = (int)Math.floor(newY/currentLevelMap.getTileHeight());

			if ((currentLevelMap.getTileChar(tileX, tileY) != '.')  && currentLevelMap.getTileChar(tileX, tileY) != '?' )  {
				return true;
			}
		}

		return false;
	} // cornerCollision


	/**
	 * load the animations for the player and initialises the player sprite
	 */
	private void loadPlayer() {

		Animation deadRight = loadAnimation("player", "right", "die");
		Animation idleRight = loadAnimation("player", "right", "idle");
		Animation walkRight = loadAnimation("player", "right", "walk");
		Animation attackRight = loadAnimation("player", "right", "attack");

		Animation deadLeft = loadAnimation("player", "left", "die");
		Animation idleLeft = loadAnimation("player", "left", "idle");
		Animation walkLeft = loadAnimation("player", "left", "walk");
		Animation attackLeft = loadAnimation("player", "left", "attack");

		deadRight.setLoop(false);
		deadLeft.setLoop(false);


		player = new Creature(idleLeft, idleRight, walkLeft,
				walkRight, deadLeft, deadRight, attackLeft, attackRight);

	} // loadPlayer 

	/**
	 * loads the animations for the goal sprite and initialises it
	 */
	private void loadTreasure() {
		Image closedChest = loadImage("images/treasure/chest1.png");
		Image openChest = loadImage("images/treasure/treasurechest1.png");
		Animation goal = new Animation();
		goal.addFrame(closedChest, 300);
		goal.addFrame(openChest, 300);

		treasure = new PowerUp.Goal(goal);

	} // loadTreasure

	/**
	 * 
	 * @param fileName sprite type directory e.g. player or enemies
	 * @param direction direction the sprite is facing for the animation
	 * @param action sprite is doing for the animation
	 * @return
	 */
	private Animation loadAnimation(String fileName, String direction, String action) {

		Animation anim = new Animation();
		int frames = new File("images/"+fileName+"/"+direction+"/"+action).list().length;

		for (int i = 0; i< frames-1; i++) {
			Image image = loadImage("images/"+fileName+"/"+direction+
					"/"+action+"/"+action+i+".png");
			anim.addFrame(image, 200);
		}
		return anim;
	} // loadAnimation

	/**
	 * loads animations for and creates an enemy sprite
	 * @return an enemy sprite(Creature)
	 */
	private Creature loadEnemySprite() {
		Animation deadRight = loadAnimation("enemies", "right", "die");
		Animation idleRight = loadAnimation("enemies", "right", "idle");
		Animation walkRight = loadAnimation("enemies", "right", "walk");

		Animation deadLeft = loadAnimation("enemies", "left", "die");
		Animation idleLeft = loadAnimation("enemies", "left", "idle");
		Animation walkLeft = loadAnimation("enemies", "left", "walk");

		deadRight.setLoop(false);
		deadLeft.setLoop(false);


		return new Creature(idleLeft, idleRight, walkLeft,
				walkRight, deadLeft, deadRight);

	} // loadEnemySprite


	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getX() < player.getX())
			leftIsPressed = true;
		if (e.getX() > player.getX());
		rightIsPressed = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		leftIsPressed = false;
		rightIsPressed = false;

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (e.getWheelRotation() > 2)
			player.jump(false);

	}

	/**
	 * Override of the keyPressed event defined in GameCore to catch our
	 * own events
	 * 
	 *  @param e The event that has been generated
	 */
	public void keyPressed(KeyEvent e) 
	{ 
		int key = e.getKeyCode();

		switch(key) {
		case KeyEvent.VK_ESCAPE	: stop(); break;
		case KeyEvent.VK_W		: jumpIsPressed = true; break;
		case KeyEvent.VK_Q		: restart(false); break;
		//case KeyEvent.VK_S		: Sound s = new Sound("sounds/caw/wav"); s.start(); break;
		case KeyEvent.VK_D	: rightIsPressed = true; break;
		case KeyEvent.VK_A	: leftIsPressed = true; break;
		case KeyEvent.VK_K	: attackIsPressed = true; break;
		//case KeyEvent.VK_P		: paused = !paused; break;
		default : break;
		}

		e.consume();
	}// keyPressed

	/**
	 * Override of the keyPressed event defined in GameCore to catch our
	 * own events
	 * 
	 *  @param e The event that has been generated
	 */
	public void keyReleased(KeyEvent e) { 

		int key = e.getKeyCode();

		switch (key)
		{
		case KeyEvent.VK_ESCAPE : stop(); break;
		case KeyEvent.VK_W    	: jumpIsPressed = false; break;
		case KeyEvent.VK_Q		: restart(false); break;
		case KeyEvent.VK_D 	: rightIsPressed = false; break;
		case KeyEvent.VK_A   : leftIsPressed = false; break;
		case KeyEvent.VK_P		: paused = !paused; break;
		case KeyEvent.VK_K	: attackIsPressed = false; break;
		default :  break;
		}

		e.consume();
	} // keyReleased

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}


