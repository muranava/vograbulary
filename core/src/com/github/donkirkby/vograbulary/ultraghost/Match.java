package com.github.donkirkby.vograbulary.ultraghost;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;


public class Match implements Serializable {
    private static final long serialVersionUID = 3147466969252314807L;
    private boolean isHyperghost;
    private transient UltraghostRandom random = new UltraghostRandom();
    private int matchScore;
    private Student[] students;
    private int studentIndex = Integer.MIN_VALUE;
    private Puzzle puzzle;
    private int minimumWordLength = 4;

    public Match(int matchScore, Student... students) {
        this.matchScore = matchScore;
        this.students = students;
    }
    
    /** Get the score required to win the match. */
    public int getMatchScore() {
        return matchScore;
    }
    
    private void checkStudentOrder() {
        if (studentIndex < 0) {
            for (int i = 0; i < students.length - 1; i++) {
                int source = random.chooseStartingStudent(students.length - i);
                Student student = students[i + source];
                students[i + source] = students[i];
                students[i] = student;
            }
            studentIndex = students.length - 1;
        }
    }
    
    /**
     * Replace the default implementation of the random generator. Useful for
     * testing.
     */
    public void setRandom(UltraghostRandom random) {
        this.random = random;
    }

    public Puzzle createPuzzle(WordList wordList) {
        checkStudentOrder();
        studentIndex = (studentIndex + 1) % students.length;
        String letters;
        String previousWord;
        if (puzzle == null || 
                ! isHyperghost || 
                puzzle.getResult() == WordResult.SKIP_NOT_IMPROVED ||
                puzzle.getResult() == WordResult.IMPROVED_SKIP_NOT_A_WORD ||
                puzzle.getResult() == WordResult.IMPROVED_SKIP_TOO_SOON ||
                puzzle.getResult() == WordResult.IMPROVED_SKIP_TOO_SHORT) {
            letters = random.generatePuzzle();
            previousWord = null;
        }
        else {
            letters = puzzle.getLetters();
            WordResult result = puzzle.getResult();
            switch (result) {
            case LONGER:
            case LATER:
            case WORD_FOUND:
                previousWord = puzzle.getResponse();
                break;
            default:
                previousWord = puzzle.getSolution();
                break;
            }
        }
        boolean isPaused = puzzle != null && puzzle.isPaused();
        puzzle = new Puzzle(
                letters, 
                students[studentIndex], 
                wordList);
        puzzle.setPreviousWord(previousWord);
        puzzle.setMinimumWordLength(minimumWordLength);
        puzzle.setPaused(isPaused);
        return puzzle;
    }

    public String getSummary() {
        checkStudentOrder();
        StringBuilder writer = new StringBuilder();
        for (Student student : students) {
            writer.append(student).append('\n');
        }
        return writer.toString();
    }

    public Puzzle getPuzzle() {
        return puzzle;
    }
    
    public void setPuzzle(Puzzle puzzle) {
        this.puzzle = puzzle;
    }

    public Student getWinner() {
        Student bestStudent = null;
        int bestScore = Integer.MIN_VALUE;
        int scoreCount = Integer.MIN_VALUE;
        boolean isTie = false;
        for (Student student : students) {
            if (bestStudent == null) {
                scoreCount = student.getScoreCount();
            }
            else if (student.getScoreCount() != scoreCount) {
                return null;
            }
            if (student.getScore() > bestScore) {
                bestStudent = student;
                bestScore = student.getScore();
            }
            else {
                isTie = student.getScore() == bestScore;
            }
        }
        return !isTie && bestScore >= matchScore ? bestStudent : null;
    }
    
    /**
     * Set the minimum length for solutions in this match. Default is 4.
     * @param minimumWordLength
     */
    public void setMinimumWordLength(int minimumWordLength) {
        this.minimumWordLength = minimumWordLength;
    }

    /**
     * Set to true if new puzzles should use the same letters as previous
     * puzzles until no player can think of any worse solutions.
     */
    public void setHyperghost(boolean isHyperghost) {
        this.isHyperghost = isHyperghost;
    }
    public boolean isHyperghost() {
        return isHyperghost;
    }
    
    public List<Student> getStudents() {
        return Arrays.asList(students);
    }
}
