package com.siegedog.lemonade.screens;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.siegedog.egglib.Dude;
import com.siegedog.egglib.EggGame;
import com.siegedog.egglib.GameScreen;
import com.siegedog.egglib.juice.FLabel;
import com.siegedog.egglib.juice.UIFLabel;
import com.siegedog.egglib.juice.UIFLabel.Fade;
import com.siegedog.egglib.physics.PointShape;
import com.siegedog.egglib.util.Log;

public class LemonadeMenu extends GameScreen {
	
	private static final String UI_LAYER = "ui";
	
	public enum State {
		/** Shows the "press any key" message */
		Splash,
		/** The main menu */
		Menu,
		/** Before a general level comes - shows a message + maybe cutscene */
		PreLevel,
		/** Shows wave number */
		PreWave,
		/** Enemies are spawned and the player fights them off */
		Gameplay,
		/** The player beat a wave - show him stats and the store */
		PostWave,
		/** After a level is done. Maybe not needed. */
		PostLevel,
		/** The player has died. Allow them to retry (go to prewave) or to return to the main menu. */
		Dead,
		/** Display a message and allow the player to return */
		Credits
	}
	
	private State state;
	
	private BitmapFont menuFont;
	private Dude walkway;
	private float elapsed;
	
	private Dude rise;
	private Dude molemen;
	
	private UIFLabel anyKey;
	
	public interface MenuAction {
		public void invoke();
	}
	
	public class MenuEntry {
		
		public UIFLabel label;
		public MenuAction action;
		private String text;
		private GameScreen screen;
		
		public MenuEntry(GameScreen screen, String text, MenuAction action) {
			this.screen = screen;
			this.action = action;
			this.text = text;
		}
		
		public void init(BitmapFont font, Vector2 relativePosition, float width, float fadeInTime, float fadeOutTime) {
			System.out.println(relativePosition);
			label = new UIFLabel(text, font, relativePosition, width, fadeInTime, fadeOutTime);
			screen.addDude(UI_LAYER, label);
		}

		public void selected() {
			label.setColor(Color.YELLOW);
		}
		
		public void deselected() {
			label.setColor(Color.WHITE);
		}
		
		public void activated() {
			action.invoke();
		}

		public void show() {
			label.show();
		}
		
		public void hide() {
			label.hide();
		}
	}
	
	public class Menu extends InputAdapter {
		public List<MenuEntry> entries;
		
		private int index = 0;
		private BitmapFont font;
		private int entryHeight;
		private int width;
		
		/**
		 * Entry 0 is selected by default.
		 */
		public Menu(Vector2 position, BitmapFont font, int width, int entryHeight, List<MenuEntry> entries) {
			this.entries = entries;
			this.font = font;
			this.width = width;
			this.entryHeight = entryHeight;
			
			Vector2 cpos = new Vector2(position);
			
			for(MenuEntry me : entries) {
				me.init(font, new Vector2(cpos), width, 0.5f, 0.5f);
				cpos.y -= entryHeight;
			}
			
			entries.get(0).selected();
		}

		@Override
		public boolean keyUp(int keycode) {
			switch(keycode) {
			case Keys.UP:
				entries.get(index).deselected();
				
				index -= 1;
				if(index < 0) {
					index = entries.size() - 1;
				}
				
				entries.get(index).selected();
				
				return true;
				
			case Keys.DOWN:
				entries.get(index).deselected();	
				
				index += 1;
				if(index >= entries.size()) {
					index = 0;
				}
				
				entries.get(index).selected();
				
				return true;
				
			case Keys.A:
			case Keys.SPACE:
			case Keys.ENTER:
				entries.get(index).activated();
			}
			
			return false;			
		}
		
		protected void show() {
			for(MenuEntry me : entries) {
				me.show();
			}
		}
		
		protected void hide() {
			for(MenuEntry me : entries) {
				me.hide();
			}
		}
		
		public void activate() {
			Gdx.input.setInputProcessor(this);
			show();
		}
		
		public void deactivate() {
			Gdx.input.setInputProcessor(null);
			hide();
		}
	}
	
	InputAdapter splashInputHandler = new InputAdapter() {
		public boolean keyUp(int keycode) {
			LemonadeMenu.this.splashToMainMenu();
			return true;
		}
	};
	
	private Menu menu;

	private UIFLabel waveAndLevelLabel;

	private UIFLabel scoreLabel;
	
	private int currentLevel;
	private int currentWave;

	private int score;
	
	@Override
	public void init(EggGame game) {
		super.init(game, 2);
		super.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		prepareLayers("gameplay", "ui");
		
		menuFont = EggGame.R.font("menuFont");
		
		float width = Gdx.graphics.getWidth() / 2.0f;
		float height = Gdx.graphics.getHeight() / 2.0f;
		
		int logoX = (int) ((width - EggGame.R.sprite("logo-rise").getWidth()) / 2.0f);
		
		addDude("ui", rise = new Dude(EggGame.R.spriteAsAnimatedSprite("logo-rise"), new PointShape(logoX, height - 220.0f)));
		addDude("ui", molemen = new Dude(EggGame.R.spriteAsAnimatedSprite("logo-molemen"), new PointShape(logoX, height - 220.0f)));
		addDude("ui", anyKey = new UIFLabel("press the any key", menuFont, new Vector2(0.0f, height - 300.0f), width, 0.5f, 0.5f));
		
		addDude("ui", scoreLabel = new UIFLabel("", menuFont, new Vector2(10.0f, height - 5.0f), 200.0f, 0.3f, 0.3f));
		addDude("ui", waveAndLevelLabel = new UIFLabel("", menuFont, new Vector2(width - 170.0f, height - 5.0f), 170.0f, 0.3f, 0.3f));
		scoreLabel.alignment = HAlignment.LEFT;
		waveAndLevelLabel.alignment = HAlignment.RIGHT;
		
		rise.physics.interactive = false;
		molemen.physics.interactive = false;
		rise.addAction(Actions.fadeOut(0.0f));
		molemen.addAction(Actions.fadeOut(0.0f));
		
		List<MenuEntry> entries = new ArrayList<>();
		entries.add(new MenuEntry(this, "Start", new MenuAction() {
			@Override
			public void invoke() {
				descendIntoAbyss();
			}
		}));
		entries.add(new MenuEntry(this, "Credits", new MenuAction() {
			@Override
			public void invoke() {
				Log.D("Credits go here");
			}
		}));
		entries.add(new MenuEntry(this, "Quit", new MenuAction() {
			@Override
			public void invoke() {
				Gdx.app.exit();
			}
		}));
		menu = new Menu(new Vector2(0, height - 180.0f), menuFont, (int) width, 40, entries);
		
		addDude("gameplay", walkway = new Dude(EggGame.R.spriteAsAnimatedSprite("walkway"), new PointShape(0.0f, -120.0f)));
		
		showSplash();
	}
	
	/**
	 * Shows the initial logo with the "press any key" prompt which leads to the menu.
	 */
	private void showSplash() {
		state = State.Splash;
		
		Gdx.input.setInputProcessor(splashInputHandler);
		anyKey.show();
		rise.addAction(Actions.delay(0.1f, Actions.fadeIn(0.33f)));
		molemen.addAction(Actions.delay(0.9f, Actions.fadeIn(0.33f)));
	}
	
	private void splashToMainMenu() {
		// Hide splash prompt and shift logo above
		anyKey.hide();
		
		rise.addAction(Actions.moveBy(0.0f, 50.0f, 0.50f, Interpolation.pow2));
		molemen.addAction(Actions.moveBy(0.0f, 50.0f, 0.50f, Interpolation.pow2));
		
		stage.addAction(Actions.delay(0.33f, new Action() {
			@Override
			public boolean act(float delta) {
				state = State.Menu;
				menu.activate();
				return true;
			}
		}));
	}

	/** 
	 * Starts the game by "tilting" the camera downwards, into the cellar.
	 */
	private void descendIntoAbyss() {
		// Tilt camera down
		final Camera cam = stage.getCamera();
		final float startY = cam.position.y;
		stage.addAction(Actions.sequence(
				Actions.delay(0.25f),
				new Action() {
					float panTime = 2.0f;
					float panDistance = 400.0f;
					float current = 0.0f;
					
					@Override
					public boolean act(float delta) {
						current += delta; 
						
						float a = current / panTime;
						if(a > 1.0f) {
							a = 1.0f;
						}
						
						cam.position.y = Interpolation.pow3.apply(startY, startY - panDistance, a);	
						return current >= panTime;
					}
				},
				new Action() {
					@Override
					public boolean act(float delta) {
						newGame();
						return true;
					}
					
				}
		));
		
		// Make menu vanish!
		menu.hide();
	}
	
	private void newGame() {
		currentLevel = 1;
		currentWave = 1;
		score = 0;
		
		scoreLabel.show();
		waveAndLevelLabel.show();
		
		// Make the game know that we mean business
		state = State.PreLevel;
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		
		elapsed += delta;
		
		// Note: keep game event-driven! Use as little switch(state)-based logic
		// as possible
		waveAndLevelLabel.message = String.format("Level %d:%d", currentLevel, currentWave);
		scoreLabel.message = String.format("Score: %d", score);
	}

}
