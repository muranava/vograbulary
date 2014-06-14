package com.github.donkirkby.vograbulary.ultraghost;

public class Puzzle {
    public static String NOT_SET = null;
    public static String NO_SOLUTION = "";
    
    private String letters;
    private String solution;
    private String response;
    private String hint;
    private Student owner;
    private WordList wordList;
    private int minimumWordLength = 4;
    
    public Puzzle(String letters, Student owner, WordList wordList) {
        if (letters == null) {
            throw new IllegalArgumentException("Puzzle letters were null.");
        }
        if (owner == null) {
            throw new IllegalArgumentException("Puzzle owner was null.");
        }
        this.letters = letters;
        this.owner = owner;
        this.wordList = wordList;
    }
    
    public Puzzle(String letters, Student owner) {
        this(letters, owner, new DummyWordList());
    }
    
    /**
     * A dummy word list that contains all words.
     *
     */
    private static class DummyWordList extends WordList {
        @Override
        public boolean contains(String word) {
            return true;
        }
    }

    /**
     * The three letters that must be matched in a valid solution.
     */
    public String getLetters() {
        return letters;
    }
    
    /**
     * A solution to the puzzle. If it matches the three letters, then it's 
     * valid. Null means it hasn't been set yet, and empty string means it
     * has been skipped.
     */
    public String getSolution() {
        return solution;
    }
    public void setSolution(String solution) {
        this.solution = solution;
    }
    
    /**
     * Another solution that tries to improve on the original solution by being
     * shorter or the same length and earlier in the dictionary. Null means
     * it hasn't been set yet, and empty string means that no response is 
     * wanted.
     */
    public String getResponse() {
        return response;
    }
    public void setResponse(String response) {
        this.response = response;
    }
    
    /**
     * Another solution that could have been used, if any exists.
     */
    public String getHint() {
        return hint;
    }
    public void setHint(String hint) {
        this.hint = hint;
    }
    
    /**
     * The result of this puzzle, including the change in score. It will be
     * UKNOWN until a solution and response have been entered.
     */
    public WordResult getResult() {
        if (solution == NOT_SET) {
            return WordResult.UNKNOWN;
        }
        if (response == NOT_SET) {
            return checkSolution();
        }
        return checkResponse();
    }

    /**
     * Compare the solution and response.
     * @return the result of comparing the two solutions
     */
    private WordResult checkResponse() {
        if (response == null || response.length() == 0) {
            return solution == null || solution.length() == 0
                    ? WordResult.SKIPPED
                    : WordResult.NOT_IMPROVED;
        }
        String challengeUpper = response.toUpperCase();
        if ( ! isMatch(challengeUpper)) {
            return solution == null || solution.length() == 0
                    ? WordResult.IMPROVED_SKIP_NOT_A_MATCH
                    : WordResult.IMPROVEMENT_NOT_A_MATCH;
        }
        if ( ! wordList.contains(challengeUpper)) {
            return solution == null || solution.length() == 0
                    ? WordResult.IMPROVED_SKIP_NOT_A_WORD
                    : WordResult.IMPROVEMENT_NOT_A_WORD;
        }
        if (challengeUpper.length() < getMinimumWordLength()) {
            return solution == null || solution.length() == 0
                    ? WordResult.IMPROVED_SKIP_TOO_SHORT
                    : WordResult.IMPROVEMENT_TOO_SHORT;
        }
        if (solution == null || solution.length() == 0) {
            return WordResult.WORD_FOUND;
        }
        return challengeWord(solution.toUpperCase(), challengeUpper);
    }

    /**
     * Check to see if the solution is in the word list and a match for the puzzle
     * letters.
     * @return VALID if the solution is valid, otherwise the reason the solution
     * was rejected.
     */
    private WordResult checkSolution() {
        if (solution == null || solution.length() == 0) {
            return WordResult.SKIPPED;
        }
        String solutionUpper = solution.toUpperCase();
        if ( ! wordList.contains(solutionUpper)) {
            return WordResult.NOT_A_WORD;
        }
        return ! isMatch(solution)
                ? WordResult.NOT_A_MATCH
                : solution.length() < getMinimumWordLength()
                ? WordResult.TOO_SHORT
                : WordResult.VALID;
    }
    
    /**
     * The student who was assigned this puzzle. Any score will be given to
     * that student.
     */
    public Student getOwner() {
        return owner;
    }
    
    @Override
    public String toString() {
        return "Puzzle(" + letters + ", " + owner.getName() + ")";
    }

    /**
     * Check if a word is a match to the puzzle letters, but don't check 
     * if it is in the word list.
     * @param word the word to check, case insensitive
     */
    public boolean isMatch(String word) {
        String upper = word.toUpperCase();
        if (upper.charAt(upper.length()-1) != letters.charAt(2)) {
            return false;
        }
        if (upper.charAt(0) != letters.charAt(0)) {
            return false;
        }
        int foundAt = upper.indexOf(letters.charAt(1), 1);
        return 0 < foundAt && foundAt < upper.length() - 1;
    }

    /**
     * Compare a new word with the current best solution. Both words must
     * be valid solutions all in upper case.
     */
    private WordResult challengeWord(String solution, String challenge) {
        return challenge.length() > solution.length()
                ? WordResult.LONGER
                : challenge.length() == solution.length()
                && challenge.compareTo(solution) > 0
                ? WordResult.LATER
                : challenge.length() < solution.length()
                ? WordResult.SHORTER
                : challenge.equals(solution)
                ? WordResult.NOT_IMPROVED
                : WordResult.EARLIER;
    }

    /**
     * Find the most common word that beats both the solution and the 
     * response.
     * @return a valid solution that beats both, or null if none found
     */
    public String findNextBetter() {
        String bestSoFar = 
                isImproved() 
                ? response == null ? "" : response.toUpperCase() 
                : solution == null ? "" : solution.toUpperCase();
        Puzzle searchPuzzle = new Puzzle(letters, owner);
        searchPuzzle.setSolution(bestSoFar);
        for (String word : wordList) {
            if (word.length() < getMinimumWordLength()) {
                continue;
            }
            searchPuzzle.setResponse(word);
            if (searchPuzzle.isImproved()) {
                return word;
            }
        }
        return null; // no improvement found.
    }

    public boolean isImproved() {
        WordResult result = getResult();
        return result == WordResult.SHORTER || 
                result == WordResult.EARLIER || 
                result == WordResult.WORD_FOUND;
    }

    /**
     * Set a limit for how long a word must be to solve the puzzle. Default 4.
     * @param minimumWordLength
     */
    public void setMinimumWordLength(int minimumWordLength) {
        this.minimumWordLength = minimumWordLength;
    }
    public int getMinimumWordLength() {
        return minimumWordLength;
    }
}
