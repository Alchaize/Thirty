package se.umu.cs.c19aky.thirty.controller

import android.os.Bundle
import android.util.Log
import se.umu.cs.c19aky.thirty.model.DiceViewModel
import se.umu.cs.c19aky.thirty.model.PointCalculator

private const val MAX_ROUNDS = 2

private const val STATE_COUNT_PHASE = "countPhase"
private const val STATE_ROUND_COUNTER = "currentRound"

private const val TAG = "GameLogic"

/*
* Class for the game logic
* */
class GameLogic {

    private var pointCalculator: PointCalculator = PointCalculator()
    private var round: Int = 1
    private var countPhase: Boolean = false

    fun saveInstance(outState: Bundle) {
        Log.d(TAG, "Saving instance")
        outState.putInt(STATE_ROUND_COUNTER, round)
        outState.putBoolean(STATE_COUNT_PHASE, countPhase)
        pointCalculator.storeCategories(outState)
    }

    fun restoreInstance(savedInstanceState: Bundle) {
        Log.d(TAG, "Restoring instance")
        round = savedInstanceState.getInt(STATE_ROUND_COUNTER)
        countPhase = savedInstanceState.getBoolean(STATE_COUNT_PHASE)
        pointCalculator.restoreCategories(savedInstanceState)
    }

    // Get the points in a list
    fun getPoints(): ArrayList<Int> {
        return pointCalculator.getAllPoints()
    }

    // Get the total points
    fun getTotalPoints(): Int {
        return pointCalculator.getTotalPoints()
    }

    // Check if game is done
    fun isGameDone(): Boolean {
        return round >= MAX_ROUNDS
    }

    // Start a new game
    fun newGame(diceViewModel: DiceViewModel) {
        round = 1

        diceViewModel.clearLockedDice()
        diceViewModel.clearUsedDice()
        diceViewModel.throwDice()
        diceViewModel.resetThrows()

        pointCalculator = PointCalculator()
    }

    // Go to next round
    fun nextRound(diceViewModel: DiceViewModel) {
        round += 1
        countPhase = false

        diceViewModel.clearLockedDice()
        diceViewModel.clearUsedDice()
        diceViewModel.throwDice()
        diceViewModel.resetThrows()

        pointCalculator.unselectCategory()
    }

    // Go to next phase
    fun nextPhase(diceViewModel: DiceViewModel) {
        diceViewModel.throwDice()
        if (diceViewModel.getThrowsLeft() == 0) {
            countPhase = true
        }
    }

    // Get if it is the count phase
    fun getCountPhase(): Boolean {
        return countPhase
    }

    // Check if currently selected dice are valid
    fun checkIfValidSelection(diceViewModel: DiceViewModel): Boolean {
        return if (diceViewModel.getLockedDiceValues().size == 0) {
            true
        } else {
            pointCalculator.checkIfValidSelection(diceViewModel.getLockedDiceValues())
        }
    }

    // Get points from selection, returns true points were added
    fun runCountPhase(diceViewModel: DiceViewModel): Boolean {

        val sum: Int

        // Split into whether user chose any dice or not
        if (diceViewModel.getLockedDiceValues().size != 0) {
            sum = pointCalculator.calculatePoints(diceViewModel.getLockedDiceValues())
            // Check if combination didn't work
            if (sum == -1) {
                return false
            }
        } else {
            sum = pointCalculator.calculatePoints(diceViewModel.getAllUnusedDiceValues())
            // If no dice were locked, but a sum was still found that means the category "Low" was used
            if (sum == -1) {
                return false
            } else {
                // Use all dice since we don't want the user to add the points unlimited times
                diceViewModel.setAllDiceUsed()
            }
        }
        // Add points
        pointCalculator.addPoints(sum)

        diceViewModel.useLockedDice()
        diceViewModel.clearLockedDice()

        // Points were added, so return true
        return true
    }

    // Select a category
    fun selectCategory(category: String): Boolean {
        return pointCalculator.selectCategory(category)
    }

    // Check if a category has been selected
    fun categorySelected(): Boolean {
        return pointCalculator.checkIfCategoryIsChosen()
    }
}