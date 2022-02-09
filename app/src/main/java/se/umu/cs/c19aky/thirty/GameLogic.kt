package se.umu.cs.c19aky.thirty

import android.os.Bundle
import android.util.Log

private const val MAX_ROUNDS = 2

private const val STATE_COUNT_PHASE = "countPhase"
private const val STATE_ROUND_COUNTER = "currentRound"

private const val TAG = "GameLogic"

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

    fun getPoints(): ArrayList<Int> {
        return pointCalculator.getAllPoints()
    }

    fun getTotalPoints(): Int {
        return pointCalculator.getTotalPoints()
    }

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

    fun nextPhase(diceViewModel: DiceViewModel) {
        diceViewModel.throwDice()
        if (diceViewModel.getThrowsLeft() == 0) {
            countPhase = true
        }
    }

    fun getCountPhase(): Boolean {
        return countPhase
    }

    fun checkIfValidSelection(diceViewModel: DiceViewModel): Boolean {
        return if (diceViewModel.getLockedDiceValues().size == 0) {
            true
        } else {
            pointCalculator.checkIfValidSelection(diceViewModel.getLockedDiceValues())
        }
    }

    // Get points from selection, returns true points were added
    fun runCountPhase(diceViewModel: DiceViewModel): Boolean {

        val sum = pointCalculator.calculatePoints(diceViewModel.getAllUnusedDiceValues(), diceViewModel.getLockedDiceValues())

        // Split into whether user chose any dice or not
        if (diceViewModel.getLockedDiceValues().size != 0) {
            // Check if combination didn't work
            if (sum == -1) {
                return false
            }
        } else {
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

        return true
    }

    fun selectCategory(category: String): Boolean {
        return pointCalculator.selectCategory(category)
    }

    fun categorySelected(): Boolean {
        return pointCalculator.checkIfCategoryIsChosen()
    }
}