package game.gui.states;

import game.factories.ImageFactory;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class Menu extends BasicGameState {
	
	private int ID;
	private Image logo;
	private String[] menuButtons;
	private int selectedButton;
	
	public Menu(int state){
		ID = state;
	}
	/**
	 * Initializes the state.
	 */
	@Override
	public void init(GameContainer gc, StateBasedGame sbg)
			throws SlickException {
		logo = ImageFactory.getLogoImage();
		logo.setFilter(Image.FILTER_NEAREST);
		logo = logo.getScaledCopy(5); //temporary
		
		menuButtons = new String[4];
		menuButtons[0] = ">Start Game<";
		menuButtons[1] = "Enter Code";
		menuButtons[2] = "High Scores";
		menuButtons[3] = "Quit Game";
		
		selectedButton = 0;
	}
	/**
	 * Render part of the state. This is where the graphics printed on the screen.
	 */
	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g)
			throws SlickException {
		g.setBackground(Color.black);
		g.drawImage(logo, gc.getWidth() / 2 - logo.getWidth() /2, 50);
		
		for (int i = 0, j = 200; i < menuButtons.length; i++, j += 50) {
			g.drawString(menuButtons[i], gc.getWidth() / 2, j);
		}
	}
	/**
	 * Update part of the state. This is where all the changes made.
	 */
	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int delta)
			throws SlickException {
		Input input = gc.getInput();
		input.disableKeyRepeat();
		
		if (input.isKeyPressed(Input.KEY_DOWN)) {
			menuButtons[selectedButton] = menuButtons[selectedButton].replace(">", "");
			menuButtons[selectedButton] = menuButtons[selectedButton].replace("<", "");
			selectedButton++;
			
			if(selectedButton > 3){ selectedButton = 0; }
			else if(selectedButton < 0) { selectedButton = 3; }
			
			menuButtons[selectedButton] = ">" + menuButtons[selectedButton] + "<";
		}
		if(input.isKeyPressed(Input.KEY_UP)){
			menuButtons[selectedButton] = menuButtons[selectedButton].replace(">", "");
			menuButtons[selectedButton] = menuButtons[selectedButton].replace("<", "");
			selectedButton--;
			
			if(selectedButton > 3){ selectedButton = 0; }
			else if(selectedButton < 0) { selectedButton = 3; }
			
			menuButtons[selectedButton] = ">" + menuButtons[selectedButton] + "<";
		}
		if (input.isKeyPressed(Input.KEY_ENTER)) {
			if(selectedButton == 3) System.exit(0);
			sbg.enterState(selectedButton + 1);
		}
	}

	@Override
	public int getID() {
		return ID;
	}
}
