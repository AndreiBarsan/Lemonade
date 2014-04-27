package com.siegedog.lemonade.screens;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.siegedog.egglib.Doodad;
import com.siegedog.egglib.Dude;
import com.siegedog.egglib.EggGame;
import com.siegedog.egglib.GameScreen;
import com.siegedog.egglib.juice.FLabel;
import com.siegedog.egglib.juice.UIFLabel;
import com.siegedog.egglib.juice.UIFLabel.Fade;
import com.siegedog.egglib.physics.PointShape;
import com.siegedog.egglib.util.Log;
import com.siegedog.lemonade.data.Waves;
import com.siegedog.lemonade.entities.MoleSlayer;
import com.siegedog.lemonade.entities.Moleman;
import com.siegedog.lemonade.menu.Menu;
import com.siegedog.lemonade.menu.MenuAction;
import com.siegedog.lemonade.menu.MenuEntry;

/**
 * Most of the game happens in this screen, in order to facilitate fancy transitions
 * e.g. from menu to gameplay.
 *
 */
public class MoleGame extends GameScreen {
	
	public static final String UI_LAYER = "ui";
	
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
		/** The player is within a level, but he paused the game */
		Paused,
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
	
	private InputProcessor splashInputHandler = new InputAdapter() {
		public boolean keyUp(int keycode) {
			MoleGame.this.splashToMainMenu();
			return true;
		}
	};
	
	/** Handles non-gameplay stuff. The player object just polls the device for
	 * better responsiveness. Seriously. */
	private InputProcessor gameplayInputHandler = new InputAdapter() {
		public boolean keyUp(int keycode) {
			if(keycode == Keys.ESCAPE) {
				pauseGame();
				return true;
			}
			
			return false;
		}
	};
	
	private InputProcessor pausedInputHandler = new InputAdapter() {
		public boolean keyUp(int key) {
			if(key == Keys.SPACE || key == Keys.ENTER || key == Keys.ESCAPE) {
				MoleGame.this.unpauseGame();
				return true;
			}
			
			return false;
		}
	};
	
	private Menu menu;

	private UIFLabel waveAndLevelLabel;

	private UIFLabel scoreLabel;
	
	private int currentLevel;
	private int currentWave;

	private int score;

	private Dude caveBackground;

	private MoleSlayer player;

	private BitmapFont splashFont;

	private UIFLabel pauseLabel;

	private UIFLabel pauseInfo;
	
	
	@Override
	public void init(EggGame game) {
		super.init(game, 2);
		super.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		prepareLayers("background", "gameplay", "gameplay-foreground", "ui");
		
		menuFont = EggGame.R.font("menuFont");
		splashFont = EggGame.R.font("splashFont");
		
		float width = Gdx.graphics.getWidth() / 2.0f;
		float height = Gdx.graphics.getHeight() / 2.0f;
		
		int logoX = (int) ((width - EggGame.R.sprite("logo-rise").getWidth()) / 2.0f);
		
		addDude("background", caveBackground = new Doodad("cave", 0, -384));
		
		addDude("ui", rise = new Doodad("logo-rise", logoX, height - 220.0f));
		addDude("ui", molemen = new Doodad("logo-molemen", logoX, height - 220.0f));
		addDude("ui", anyKey = new UIFLabel("press the any key", menuFont, new Vector2(0.0f, height - 300.0f), width, 0.5f, 0.5f));
		
		addDude("ui", scoreLabel = new UIFLabel("", menuFont, new Vector2(10.0f, height - 5.0f), 200.0f, 0.3f, 0.3f));
		addDude("ui", waveAndLevelLabel = new UIFLabel("", menuFont, new Vector2(width - 170.0f, height - 5.0f), 170.0f, 0.3f, 0.3f));
		
		addDude("ui", pauseLabel = new UIFLabel("Game paused", splashFont, new Vector2(0.0f, height - 150.0f), width, 0.2f, 0.2f));
		addDude("ui", pauseInfo = new UIFLabel("Press Space or something to resume", menuFont, new Vector2(0.f, height - 200.0f), width, 1.2f, 0.2f));
		scoreLabel.alignment = HAlignment.LEFT;
		waveAndLevelLabel.alignment = HAlignment.RIGHT;
		
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
		
		addDude("gameplay-foreground", walkway = new Doodad("walkway", 0.0f, -120.0f));
		
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
		menu.deactivate();
	}
	
	private void newGame() {
		// Initialize core game logic params 
		currentLevel = 1;
		currentWave = 1;
		score = 0;
		
		// Show the HUD
		scoreLabel.show();
		waveAndLevelLabel.show();
		
		state = State.PreLevel;
		// TODO: show wave/level number
				
		Gdx.input.setInputProcessor(gameplayInputHandler);
		
		addDude("gameplay", player = new MoleSlayer(100.0f, -105.0f));
		
		stage.addAction(Actions.delay(1.0f, new Action() {
			public boolean act(float delta) {
				startNextWave();
				return true;
			}
		}));
	}
	
	private void startNextWave() {
		
		// TODO: show wave number
		
		stage.addAction(Actions.delay(0.5f, new Action() {
			public boolean act(float delta) {
				startSpawning();
				return true;
			}
		}));
	}
	
	private void startSpawning() {
		Waves.waves[currentLevel - 1][currentWave - 1].schedule(this);
	}
	
	private void pauseGame() {
		// TODO: freeze all enemies
		player.pause();
		pauseLabel.show();
		pauseInfo.show();
		state = State.Paused;
		
		Gdx.input.setInputProcessor(pausedInputHandler);
	}
	
	private void unpauseGame() {
		// TODO: unfreeze all enemies
		player.unpause();
		pauseLabel.hide();
		pauseInfo.hide();
		
		state = State.Gameplay;
		
		Gdx.input.setInputProcessor(gameplayInputHandler);
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

	public void spawnMole(Moleman moleman) {
		addDude("gameplay", moleman);
	}
}
