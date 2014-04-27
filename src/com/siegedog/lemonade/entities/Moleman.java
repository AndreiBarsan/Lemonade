package com.siegedog.lemonade.entities;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.siegedog.egglib.AnimatedSprite;
import com.siegedog.egglib.Dude;
import com.siegedog.egglib.EffectDude;
import com.siegedog.egglib.EggGame;
import com.siegedog.egglib.physics.AABB;
import com.siegedog.egglib.physics.Shape;
import com.siegedog.lemonade.screens.MoleGame;

public abstract class Moleman extends Dude {

	public enum State {
		Walking,
		Jetpacking,
		Falling,
		Decaying
	}
	
	public static final int FALL_SPEED = 90;
	
	protected int maxHealth;
	protected int curHealth;
	
	protected State state;
	private float startY;
	
	private AnimatedSprite aliveSprite;
	private AnimatedSprite fallingSprite;
	private AnimatedSprite deadSprite;
	
	public Moleman(AnimatedSprite sprite, AnimatedSprite fallingSprite, AnimatedSprite deadSprite, Shape shape, Vector2 spriteOffset, int hp) {
		super(sprite, shape);
		curHealth = maxHealth = hp;
		
		state = State.Walking;
		physics.interactive = false;
		
		startY = shape.getY();
		setSpriteOffset(spriteOffset);
		
		this.aliveSprite = sprite;
		this.fallingSprite = fallingSprite;
		this.deadSprite = deadSprite;
	}
	
	public void takeDamage(int amount) {
		curHealth -= amount;
		
		if(curHealth <= 0) {
			screen.addDude("effects", new EffectDude(physics.getCenter(), "puff"));
			
			if(state == State.Jetpacking) {
				state = State.Falling;
				clearActions();
				
				addAction(Actions.sequence(
						Actions.delay(0.05f),
						new Action() {
							public boolean act(float delta) {
								setSprite(fallingSprite);
								return true;
							}
						},
						Actions.delay(0.2f),
						Actions.moveTo(getX(), startY, Math.abs(startY - getY()) / FALL_SPEED, Interpolation.exp5In),
						new Action() {
							public boolean act(float delta) {
								startDecay();
								return true;
							}
						}
				));
			} else {
				// He didn't even manage to take off
				startDecay();
			}
		}
	}
	
	protected void startDecay() {
		clearActions();
		
		state = State.Decaying;
		setSprite(deadSprite);
		screen.addDude("effects", new EffectDude(physics.getCenter(), "puff"));
		addAction(Actions.sequence(Actions.delay(0.2f), Actions.fadeOut(1.0f), new Action() {
			public boolean act(float delta) {
				Moleman.this.kill();
				return true;
			}
		}));
	}

	public boolean isAttackable() {
		// Can only be attacked if alive
		return state == State.Walking || state == State.Jetpacking;
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
	}

	@Override
	public void kill() {
		super.kill();
		((MoleGame)screen).moleDied();
	}
}
