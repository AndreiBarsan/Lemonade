package com.siegedog.lemonade;

import com.siegedog.egglib.EggGame;
import com.siegedog.egglib.GameScreen;
import com.siegedog.egglib.PreloadingScreen;
import com.siegedog.egglib.PreloadingScreen.LoadTask;
import com.siegedog.egglib.Resources;
import com.siegedog.lemonade.screens.MoleGame;

public class LemonadeGame extends EggGame {

	@Override
	protected void startup() {		
		GameScreen next = new MoleGame();
		LoadTask loadTask = new LoadTask() {
			
			@Override
			public void performLoad(Resources r) {
				r.loadFont("menuFont");
				r.loadFont("splashFont");
				r.loadAtlas("img/atlas/pack.atlas");
				
				r.loadParticleEffect("puff");
				r.loadParticleEffect("blast");
			}
		};
		
		setScreen(new PreloadingScreen(next, loadTask) );
	}

}
