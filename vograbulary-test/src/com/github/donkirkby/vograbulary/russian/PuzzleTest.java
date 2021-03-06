package com.github.donkirkby.vograbulary.russian;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.github.donkirkby.vograbulary.russian.Puzzle.NoSolutionException;
import com.github.donkirkby.vograbulary.ultraghost.WordList;

public class PuzzleTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Test
    public void twoWordClue() {
        String expectedClue = "";
        Puzzle puzzle = new Puzzle("two words");
        
        String clue = puzzle.getClue();
        assertThat("clue", clue, is(expectedClue));
    }

    @Test
    public void twoWordTargets() {
        Puzzle puzzle = new Puzzle("two words");
        
        String target1 = puzzle.getTarget(0);
        String target2 = puzzle.getTarget(1);
        assertThat("target 1", target1, is("TWO"));
        assertThat("target 2", target2, is("WORDS"));
    }
    
    @Test
    public void threeWordClue() {
        String expectedClue = "three *big* *words*";
        Puzzle puzzle = new Puzzle(expectedClue);
        
        String clue = puzzle.getClue();
        assertThat("clue", clue, is(expectedClue));
    }

    @Test
    public void threeWordTargets() {
        Puzzle puzzle = new Puzzle("three *big* *words*");
        
        String target1 = puzzle.getTarget(0);
        String target2 = puzzle.getTarget(1);
        assertThat("target 1", target1, is("BIG"));
        assertThat("target 2", target2, is("WORDS"));
    }
    
    @Test
    public void solve() {
        Puzzle puzzle = new Puzzle("unable comfort");
        
        puzzle.setTargetWord(0);
        puzzle.setTargetCharacter(2);
        String combination = puzzle.getCombination();
        
        assertThat("combination", combination, is("UNCOMFORTABLE"));
    }
    
    @Test
    public void solveReverse() {
        Puzzle puzzle = new Puzzle("comfort unable");
        
        puzzle.setTargetWord(1);
        puzzle.setTargetCharacter(2);
        String combination = puzzle.getCombination();
        
        assertThat("combination", combination, is("UNCOMFORTABLE"));
    }
    
    @Test
    public void combinationWithoutTarget() {
        Puzzle puzzle = new Puzzle("comfort unable");

        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Target word and character are not set.");
        puzzle.getCombination();
    }

    @Test
    public void targetsWithPunctuation() {
        Puzzle puzzle = new Puzzle("*targets* sometimes have *punctuation*!");
        
        String target1 = puzzle.getTarget(0);
        String target2 = puzzle.getTarget(1);
        assertThat("target 1", target1, is("TARGETS"));
        assertThat("target 2", target2, is("PUNCTUATION"));
    }
    
    @Test
    public void findSolutionEarly() {
        Puzzle puzzle = new Puzzle("LIPS SHOD");
        WordList wordList = new WordList("slipshod", "uncomfortable");
        
        String solution = puzzle.findSolution(wordList);
        
        assertThat("combination", solution, is("SLIPSHOD"));
    }
    
    @Test
    public void findSolutionLate() {
        Puzzle puzzle = new Puzzle("predict amen");
        WordList wordList = new WordList("potato", "predicament");
        
        String solution = puzzle.findSolution(wordList);
        
        assertThat("combination", solution, is("PREDICAMENT"));
    }
    
    @Test
    public void noSolution() {
        Puzzle puzzle = new Puzzle("comfort unstable");
        WordList wordList = new WordList("potato", "uncomfortable");
        
        thrown.expect(NoSolutionException.class);
        puzzle.findSolution(wordList);
    }

    @Test
    public void adjustmentsAddUp() {
        Puzzle puzzle1 = new Puzzle("not relevant");
        Puzzle puzzle2 = new Puzzle("not relevant");
        
        puzzle1.adjustScore(10);
        BigDecimal score1 = puzzle1.getScore();
        
        puzzle2.adjustScore(1);
        puzzle2.adjustScore(9);
        BigDecimal score2 = puzzle2.getScore();
        
        assertThat("score after 2 adjustments", score2, is(score1));
    }

    @Test
    public void adjustScoreAfterSolving() {
        Puzzle puzzle1 = new Puzzle("not relevant");
        
        puzzle1.adjustScore(10);
        BigDecimal score1 = puzzle1.getScore();
        
        puzzle1.setSolved(true);
        puzzle1.adjustScore(5);
        BigDecimal score2 = puzzle1.getScore();
        
        assertThat("score after 2 adjustments", score2, is(score1));
    }

    @Test
    public void totalScoreWithoutPrevious() {
        Puzzle puzzle = new Puzzle("not relevant");
        
        BigDecimal score = puzzle.getTotalScore();
        String scoreDisplay = puzzle.getTotalScoreDisplay();
        
        assertThat("score", score, is(BigDecimal.ZERO));
        assertThat("score display", scoreDisplay, is("0"));
    }

    @Test
    public void totalScoreAfterSolving() {
        Puzzle puzzle = new Puzzle("not relevant");
        
        float seconds = 10;
        puzzle.adjustScore(seconds);
        puzzle.setSolved(true);
        BigDecimal totalScore = puzzle.getTotalScore();
        BigDecimal score = puzzle.getScore();
        
        assertThat("score", totalScore, is(score));
    }
    
    @Test
    public void totalScoreAfterNotSolving() {
        Puzzle puzzle = new Puzzle("not relevant");
        
        float seconds = 10;
        puzzle.adjustScore(seconds);
        puzzle.setSolved(false);
        BigDecimal totalScore = puzzle.getTotalScore();
        
        assertThat("score", totalScore, is(BigDecimal.ZERO));
    }

    @Test
    public void totalScoreWithPrevious() {
        Puzzle puzzle1 = new Puzzle("not relevant");
        puzzle1.adjustScore(10);
        puzzle1.setSolved(true);
        BigDecimal score1 = puzzle1.getScore();
        
        Puzzle puzzle2 = new Puzzle("still irrelevant", puzzle1);
        BigDecimal totalScore1 = puzzle2.getTotalScore();
        
        puzzle2.adjustScore(20);
        puzzle2.setSolved(true);
        BigDecimal score2 = puzzle2.getScore();
        BigDecimal totalScore2 = puzzle2.getTotalScore();

        assertThat("score", totalScore1, is(score1));
        assertThat("score2", totalScore2, is(score1.add(score2)));
    }
    
    @Test
    public void isTargetSetOnNew() {
        Puzzle puzzle = new Puzzle("not relevant");
        
        assertThat("isTargetSet", puzzle.isTargetSet(), is(false));
    }
    
    @Test
    public void isTargetSetOnCharacter() {
        Puzzle puzzle = new Puzzle("not relevant");

        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Target character set before target word.");

        puzzle.setTargetCharacter(1);
    }
    
    @Test
    public void isTargetSetOnWord() {
        Puzzle puzzle = new Puzzle("not relevant");
        puzzle.setTargetWord(0);
        
        assertThat("isTargetSet", puzzle.isTargetSet(), is(false));
    }
    
    @Test
    public void isTargetSetOnWordAndCharacter() {
        Puzzle puzzle = new Puzzle("not relevant");
        puzzle.setTargetWord(0);
        puzzle.setTargetCharacter(1);
        
        assertThat("isTargetSet", puzzle.isTargetSet(), is(true));
    }
    
    @Test
    public void clearTargets() {
        Puzzle puzzle = new Puzzle("not relevant");
        puzzle.setTargetWord(0);
        puzzle.setTargetCharacter(1);
        
        puzzle.clearTargets();
        
        assertThat("isTargetSet", puzzle.isTargetSet(), is(false));
    }
}
