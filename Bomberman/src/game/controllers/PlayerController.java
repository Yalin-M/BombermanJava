package game.controllers;

import game.constants.Constants;
import game.controllers.interfaces.IPlayerController;
import game.gui.main.Game;
import game.gui.states.GameOver;
import game.gui.states.Play;
import game.models.Cell;
import game.models.Direction;
import game.models.Door;
import game.models.ElementType;
import game.models.Player;
import game.models.PowerUpElement;

import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class PlayerController implements IPlayerController {

	private StateBasedGame game;
	private Player player;
	private float moveTimer;
	private float smoothShift;

	
	public PlayerController(Player player, StateBasedGame game) {
		this.player = player;
		resetMoveTimer();
		this.game = game;
		smoothShift = 0;
	}

	@Override
	public void update(int delta) {
		
		if(!player.isAlive()){
			try {
				game.initStatesList(game.getContainer());
				((GameOver) game.getState(Game.gameOver)).setScore(player.getScore());
				((GameOver) game.getState(Game.gameOver)).setLevelCode(((Play)game.getCurrentState()).getLevelCode());
				game.enterState(Game.gameOver);
				return;
			} catch (SlickException e) {
				e.printStackTrace();
			}
		}
		
		handleInput();
		
		if(player.isMoving()){
			((Play) game.getCurrentState()).getElementPainter().startPlayerAnim(player.getCurrentDir());
			handlePlayerMovement(delta);
		}else
			((Play) game.getCurrentState()).getElementPainter().stopPlayerAnim(player.getCurrentDir());
		
		handleCollisionWithOtherElements();
		
	}
	
	private void handleCollisionWithOtherElements() {
		MapController mapController = ((Play) game.getCurrentState()).getMapController();
		
		Cell cell = mapController.getCellAt(player.getX(), player.getY());
		if(!player.isMoving()){
			if(cell.isContains(ElementType.Door) && ((Door) cell.getElement(ElementType.Door)).isOpen()){
				player.setActiveBombCount(0);
				((Play) game.getState(Game.play)).levelCompleted(game);
			}
			if(cell.isContains(ElementType.PowerUp)){
				PowerUpElement pe = (PowerUpElement)cell.getElement(ElementType.PowerUp);
				player.powerUp(pe.getPowerType());
				pe.setTaken(true);
			}
			if(player.isKilled()){
				player.setKilled(false);
				cell.deleteElement(player);
				player.initLoc(1, 1);
				mapController.getCellAt(player.getX(), player.getY()).addElement(player);
			}		
		}
	}

	private void handlePlayerMovement(float delta) {
		int playerX = player.getX();
		int playerY = player.getY();
		
		MapController mapController = ((Play) game.getState(Game.play)).getMapController();
		
		moveTimer -= delta;
		smoothShift = (float) delta / getMoveTime();
		float realX = player.getRealX();
		float realY = player.getRealY();
		
		if(realX > playerX)
			player.setRealX(realX -= smoothShift);
		else if(realX < playerX)
			player.setRealX(realX += smoothShift);
		if(realY > playerY)
			player.setRealY(realY -= smoothShift);
		else if(realY < playerY)
			player.setRealY(realY += smoothShift);
		
		if(moveTimer < 0){
			player.setMoving(false);
			resetMoveTimer();
			smoothShift = 0;
			player.setRealX(realX = playerX);
			player.setRealY(realY = playerY);
			
			mapController.deleteElementAtCell(player.getPrevX(), player.getPrevY(), player);
			mapController.addElementToCell(player.getX(), player.getY(), player);
			
			player.setPrevX(player.getX());
			player.setPrevY(player.getY());
		}
	}
	private void handleInput() {
		Input input = game.getContainer().getInput();
		
		if(input.isKeyDown(Input.KEY_LCONTROL)){
			placeBomb();
		}if(input.isKeyDown(Constants.PLAYER_KEY_UP)){
			movePlayer(Direction.Up);
		}if (input.isKeyDown(Constants.PLAYER_KEY_DOWN)) {
			movePlayer(Direction.Down);
		}if (input.isKeyDown(Constants.PLAYER_KEY_LEFT)) {
			movePlayer(Direction.Left);
		}if (input.isKeyDown(Constants.PLAYER_KEY_RIGHT)) {
			movePlayer(Direction.Right);
		}
	}
	
	private void resetMoveTimer() {
		moveTimer = getMoveTime();
	}
	
	private float getMoveTime(){
		return (float) 300.f / player.getMoveSpeed();
	}

	@Override
	public void movePlayer(Direction dir) {
		if(player.isMoving()){
			return;
		}
		
		int x = player.getX(), y = player.getY();
		MapController mapController = ((Play) game.getCurrentState()).getMapController();
		if(mapController.getCellAt(x, y).isContains(ElementType.Explosion)){
			return;
		}
		
		int toX = x, toY = y;
		
		switch (dir) {
		case Up:
			toY--;
			break;
		case Down:
			toY++;
			break;
		case Left:
			toX--;
			break;
		case Right:
			toX++;
			break;
		default:
			break;
		}
		
		Cell cell = mapController.getCellAt(toX, toY);
		if(		cell.isContains(ElementType.SolidWall)
				|| cell.isContains(ElementType.BrickWall)
				|| cell.isContains(ElementType.Bomb)){
			return;
		}
		//finally
		player.setMoving(true);
		player.setCurrentDir(dir);
		player.setX(toX);
		player.setY(toY);

	}
	private void placeBomb(){
		if(player.isMoving()){
			return;
		}
		MapController mapController = ((Play) game.getCurrentState()).getMapController();
		Cell cell = mapController.getCellAt(player.getX(), player.getY());
		if(!cell.isContains(ElementType.Bomb) && player.getActiveBombCount() < player.getBombCount()){
			BombController bc = ((Play) game.getCurrentState()).getBombController();
			bc.spawnBomb(player.getX(), player.getY(), player);
			player.setActiveBombCount(player.getActiveBombCount() + 1);
//			if(player.isMoving()){//this part may change later
//				bc.spawnBomb(player.getPrevX(), player.getPrevY(), player);
//			}else{
//				bc.spawnBomb(player.getX(), player.getY(), player);
//			}
		}
	}
	public float getRealX() {
		return player.getRealX();
	}

	public float getRealY() {
		return player.getRealY();
	}

	public void addScore(int score) {
		player.addScore(score);
	}
	public void bombExploded(){
		player.setActiveBombCount(player.getActiveBombCount() - 1);
	}

	public Player getPlayer() {
		return player;
	}
}
