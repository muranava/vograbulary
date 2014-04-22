package com.github.donkirkby.vograbulary.ultraghost;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.github.donkirkby.vograbulary.VograbularyApp;
import com.github.donkirkby.vograbulary.VograbularyPreferences;

public class UltraghostScreen implements Screen {
    //stopJesting
    private VograbularyApp app;
    private Stage stage;
    private Controller ultraghostController = 
            new Controller();
    private boolean isComputerOpponent;
    private View view;

    public UltraghostScreen(VograbularyApp vograbularyApp) {
        app = vograbularyApp;
        stage = new Stage();
        
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        
        view = new View();
        view.create(table, app, ultraghostController);
        WordList wordList = new WordList();
        wordList.read(
                Gdx.files.internal("data/wordlist.txt").reader());
        ultraghostController.setWordList(wordList);
    }
    
    public void setComputerOpponent(boolean isComputerOpponent) {
        this.isComputerOpponent = isComputerOpponent;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        view.clear();
        ComputerStudent computerStudent = 
                new ComputerStudent(app.getPreferences());
        computerStudent.setSearchBatchSize(30);
        computerStudent.setMaxSearchBatchCount(1000); // 10s
        VograbularyPreferences preferences = app.getPreferences();
        String student1Name = 
                isComputerOpponent 
                ? "You"
                : preferences.getStudent1Name();
        ultraghostController.clearStudents();
        ultraghostController.addStudent(new Student(student1Name));
        ultraghostController.addStudent(
                isComputerOpponent
                ? computerStudent
                : new Student(preferences.getStudent2Name()));

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
    //resumeJesting
}