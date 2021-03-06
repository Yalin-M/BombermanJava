package game.gui.painter;

import game.constants.Constants;
import game.controllers.MapController;
import game.factories.ImageFactory;
import game.gui.camera.Camera;
import game.gui.main.Game;
import game.gui.states.Play;
import game.models.Direction;
import game.models.ElementType;
import game.models.MapElement;
import game.models.Player;

import java.util.Iterator;
import java.util.LinkedList;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.fills.GradientFill;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.StateBasedGame;


public class ElementPainter {
	//replace this part with sprites
	private Image solidWallIMG;
	private Image brickWallIMG;
	private Image bombIMG;
	private Image doorIMG;
	private Image explosionIMG;
	
	private Image playerIMG;
	private SpriteSheet playerSprite;
	private Animation playerAnim;
	
	private Image powerUpIMG;
	
	private int topSpacing;
	private Rectangle topRect;
	private GradientFill topRectFill;
	
	private Camera cam;
	private StateBasedGame game;
	private Graphics g;
	private float topShift;
	private float sideShift;
	/**
	 * Constructor method.
	 * @param game Game instance that will be painted in to
	 * @param cam Camera instace
	 * @param solidWallIMG Image of the solid wall.
	 * @param brickWallIMG Image of the brick wall.
	 * @param bombIMG Image of the bomb.
	 * @param doorIMG Image of the  door.
	 * @param explosionIMG Image of the explosion.
	 * @param playerIMG Image of the player.
	 * @param powerUpIMG Image of the power up.
	 */
	public ElementPainter(StateBasedGame game, Camera cam){
		try {
			this.solidWallIMG = filterAndScale(ImageFactory.getSolidWallImage());
			this.brickWallIMG = filterAndScale(ImageFactory.getBrickWallImage());
			this.bombIMG = filterAndScale(ImageFactory.getBombImage());
			this.doorIMG = filterAndScale(ImageFactory.getDoorImage());
			this.explosionIMG = filterAndScale(ImageFactory.getExplosionImage());
			this.playerIMG = filterAndScale(ImageFactory.getPlayerImage());
			this.powerUpIMG = filterAndScale(ImageFactory.getPowerUpImage());
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.playerSprite = new SpriteSheet(this.playerIMG, Constants.GAME_TILESIZE, Constants.GAME_TILESIZE);
		this.playerAnim = new Animation(false);
		for(int i = 0; i < 4 ;i++)
			this.playerAnim.addFrame(playerSprite.getSubImage(0, i), 200);
		this.playerAnim.setCurrentFrame(1);
		
		this.cam = cam;
		this.game = game;
		
		topRect = new Rectangle(0, 0, 1024, Constants.GAME_TILESIZE);
		topRectFill = new GradientFill(0, 0, Color.gray, topRect.getMaxX(), topRect.getMaxY(), Color.gray);
		topSpacing = Constants.GAME_TILESIZE;
		
		//sprite init
	}
	/**
	 * Filters to nearest corner and scales the image.
	 * @param image Image to be scaled.
	 * @return Scaled image 
	 */
	private Image filterAndScale(Image image){
		if(image != null){
			image.setFilter(Image.FILTER_NEAREST);
			return image.getScaledCopy(Constants.GAME_SCALE);
		}else
			return null;
	}
	/**
	 * Draws the elements to screen.
	 * @param g Graphics instance which the images will be drawn.
	 */
	public void draw(Graphics g) {
		this.g = g;
		MapController mapController = ((Play) game.getState(Game.play)).getMapController();
		topShift = topSpacing - cam.getCameraY();
		sideShift = -cam.getCameraX();
		LinkedList<MapElement> temp = new LinkedList<MapElement>();
		
		
		for(int y = 0; y < mapController.getTileCountY(); y++){
			for(int x = 0; x < mapController.getTileCountX(); x++){
				Iterator<MapElement> iterator = mapController.getCellIteratorAt(x, y);
				if(iterator != null){
					while(iterator.hasNext()){
						MapElement e = (MapElement) iterator.next();
						if(e.getType() == ElementType.Player){
							temp.add(e);
						}else
							drawElement(e);
						}
					}
				}
		}
		
		Iterator<MapElement> iterator = temp.listIterator();
		while(iterator.hasNext()){
			MapElement e = (MapElement) iterator.next();
			if(e.getType() == ElementType.Player){
				drawTopMenu(e);
			}
			drawElement(e);
		}
	}
	/**
	 * Draws the top info panel
	 * @param e Player element
	 */
	private void drawTopMenu(MapElement e) {
		g.fill(topRect, topRectFill);//fix 800 later to game width
		g.drawString("LIVES: " + ((Player) e).getLives(), 20, topRect.getHeight() / 3);
		g.drawString("SCORE: " + ((Player) e).getScore(), 200, topRect.getHeight() / 4);
		g.drawString("BOMB: " + ((Player) e).getBombCount(), 200, 2 * topRect.getHeight() / 4);
		g.drawString("SPEED: " + ((Player) e).getMoveSpeed(), 300, topRect.getHeight() / 4);
		g.drawString("EXPLOSION: " + ((Player) e).getExplosionRange(), 300, 2 * topRect.getHeight() / 4);
		
		g.drawString("CURRENT STAGE: " + (((Play) game.getState(Game.play)).getCurrentDifficulty() + 1), 450, topRect.getHeight() / 3);
	}
	/**
	 * Draws the given MapElement to graphics instance.
	 * @param e MapElement to be drawn.
	 */
	private void drawElement(MapElement e) {
		g.setColor(Color.black);
		switch (e.getType()) {
		case SolidWall:
			solidWallIMG.draw(e.getRealX() * Constants.GAME_TILESIZE + sideShift, e.getRealY() * Constants.GAME_TILESIZE + topShift);
			break;
		case Door:
			doorIMG.draw(e.getRealX() * Constants.GAME_TILESIZE + sideShift, e.getRealY() * Constants.GAME_TILESIZE + topShift);
			break;
		case Player:
			playerAnim.draw(e.getRealX() * Constants.GAME_TILESIZE + sideShift, e.getRealY() * Constants.GAME_TILESIZE + topShift);
			break;
		case Bomb:
			bombIMG.draw(e.getRealX() * Constants.GAME_TILESIZE + sideShift, e.getRealY() * Constants.GAME_TILESIZE + topShift);
			break;
		case Explosion:
			explosionIMG.draw(e.getRealX() * Constants.GAME_TILESIZE + sideShift, e.getRealY() * Constants.GAME_TILESIZE + topShift);
			break;
		case BrickWall:
			brickWallIMG.draw(e.getRealX() * Constants.GAME_TILESIZE + sideShift, e.getRealY() * Constants.GAME_TILESIZE + topShift);
			break;
		case PowerUp:
			powerUpIMG.draw(e.getRealX() * Constants.GAME_TILESIZE + sideShift, e.getRealY() * Constants.GAME_TILESIZE + topShift);
			break;
		default:
			break;
		}
		g.setColor(Color.white);
	}
	/**
	 * Starts playing the player walk animation.
	 * @param dir Direction of the animation.
	 */
	public void startPlayerAnim(Direction dir){
		switch (dir) {
		case Down:
			playerAnim.setAutoUpdate(true);
			break;
		case Up:
			playerAnim.setAutoUpdate(true);
			break;
		case Left:
			playerAnim.setAutoUpdate(true);
			break;
		case Right:
			playerAnim.setAutoUpdate(true);
			break;
		default:
			break;
		}
	}
	/**
	 * Stops the player walk animation.
	 * @param dir Final direction of the animation.
	 */
	public void stopPlayerAnim(Direction dir) {
		switch (dir) {
		case Down:
			playerAnim.setAutoUpdate(false);
			playerAnim.setCurrentFrame(1);
			break;
		case Up:
			playerAnim.setAutoUpdate(false);
			playerAnim.setCurrentFrame(1);
			break;
		case Left:
			playerAnim.setAutoUpdate(false);
			playerAnim.setCurrentFrame(1);
			break;
		case Right:
			playerAnim.setAutoUpdate(false);
			playerAnim.setCurrentFrame(1);
			break;
		default:
			break;
		}
	}
	public Image getSolidWallIMG() {
		return solidWallIMG;
	}

	public void setSolidWallIMG(Image solidWallIMG) {
		this.solidWallIMG = solidWallIMG;
	}

	public Image getBrickWallIMG() {
		return brickWallIMG;
	}

	public void setBrickWallIMG(Image brickWallIMG) {
		this.brickWallIMG = brickWallIMG;
	}

	public Image getBombIMG() {
		return bombIMG;
	}

	public void setBombIMG(Image bombIMG) {
		this.bombIMG = bombIMG;
	}

	public Image getDoorIMG() {
		return doorIMG;
	}

	public void setDoorIMG(Image doorIMG) {
		this.doorIMG = doorIMG;
	}

	public Image getExplosionIMG() {
		return explosionIMG;
	}

	public void setExplosionIMG(Image explosionIMG) {
		this.explosionIMG = explosionIMG;
	}

	public Image getPlayerIMG() {
		return playerIMG;
	}

	public void setPlayerIMG(Image playerIMG) {
		this.playerIMG = playerIMG;
	}

	public Image getPowerUpIMG() {
		return powerUpIMG;
	}

	public void setPowerUpIMG(Image powerUpIMG) {
		this.powerUpIMG = powerUpIMG;
	}
}
