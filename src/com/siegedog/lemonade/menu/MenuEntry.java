package com.siegedog.lemonade.menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.siegedog.egglib.GameScreen;
import com.siegedog.egglib.juice.UIFLabel;
import com.siegedog.lemonade.screens.MoleGame;

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
		screen.addDude(MoleGame.UI_LAYER, label);
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