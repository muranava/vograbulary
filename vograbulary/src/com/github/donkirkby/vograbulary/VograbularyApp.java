package com.github.donkirkby.vograbulary;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.github.donkirkby.vograbulary.ultraghost.UltraghostScreen;

//stopJesting
public class VograbularyApp extends Game {
    private UltraghostScreen ultraghostScreen;
    private MenuScreen menuScreen;
    private VograbularyPreferences preferences;
	private Skin skin;
	
	@Override
	public void create() {
	    skin = new Skin(Gdx.files.internal("data/ui/uiskin.json"));
	    menuScreen = new MenuScreen(this);
	    ultraghostScreen = new UltraghostScreen(this);
	    setScreen(menuScreen);
	}
	
	public Skin getSkin() {
        return skin;
    }
	
	public void startUltraghost(boolean isComputerOpponent) {
	    ultraghostScreen.setComputerOpponent(isComputerOpponent);
	    setScreen(ultraghostScreen);
	}
	
	public void showMenu() {
	    setScreen(menuScreen);
	}
	
    public VograbularyPreferences getPreferences() {
        if (preferences == null) {
            preferences = new VograbularyPreferences(
                    Gdx.app.getPreferences("vograbulary"));
        }
        return preferences;
    }

    @Override
	public void dispose() {
	    menuScreen.dispose();
	    ultraghostScreen.dispose();
	}
}
//resumeJesting