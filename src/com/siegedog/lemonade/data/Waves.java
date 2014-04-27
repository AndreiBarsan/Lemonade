package com.siegedog.lemonade.data;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.siegedog.lemonade.entities.BasicMole;
import com.siegedog.lemonade.screens.MoleGame;

public class Waves {
	
	public final static int BOTTOM = -400;
	public final static int RIGHT = 512;
	
	public interface Wave {
		public void schedule(MoleGame screen);
	}
	
	private static Action done(final MoleGame screen) {
		return new Action() {
			public boolean act(float delta) {
				screen.doneSpawning();
				return true;
			}
		};
	}
	
	private static Action spawnBasicMolemen(final MoleGame screen, final Vector2... positions) {
		return new Action() {
			public boolean act(float delta) {
				for(Vector2 pos : positions) {
					screen.spawnMole(new BasicMole(pos.x, pos.y));
				}
				return true;
			}
		};
	}
	
	public static final Wave L1_W1 = new Wave() {
		public void schedule(MoleGame screen) {
			screen.getStage().addAction(Actions.sequence(
				spawnBasicMolemen(screen, new Vector2(-50.0f, BOTTOM), new Vector2(RIGHT + 50.0f, BOTTOM)),
				Actions.delay(2.0f),
				spawnBasicMolemen(screen, new Vector2(-50.0f, BOTTOM), new Vector2(RIGHT + 50.0f, BOTTOM)),
				done(screen)
			));
		}
	};
	
	public static Wave[][] waves = new Wave[][] {
		new Wave[] { L1_W1 }
	};
}
