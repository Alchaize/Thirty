package se.umu.cs.c19aky.thirty

import androidx.lifecycle.ViewModel
import kotlin.random.Random

private const val TAG = "DiceViewModel"
private const val MAX_THROWS = 2

class DiceViewModel : ViewModel() {

    private var throwsLeft = MAX_THROWS
    private val diceList = listOf(
        Die(1, false, used = false),
        Die( 1, false, used = false),
        Die( 1, false, used = false),
        Die( 1, false, used = false),
        Die( 1, false, used = false),
        Die(1, false, used = false),
    )

    // Get the amount of throws left
    fun getThrowsLeft(): Int {
        return throwsLeft
    }

    // Set throws left to something specific
    fun setThrowsLeft(throwCount: Int) {
        throwsLeft = throwCount
    }

    // Reset the amount of throws
    fun resetThrows() {
        throwsLeft = MAX_THROWS
    }

    // Throw the dice, assigning a new value to each of the unlocked dice
    fun throwDice() {
        for (die in diceList) {
            if (!die.locked) {
                die.value = Random.nextInt(1,7)
            }
        }
        throwsLeft -= 1
    }

    // Set the die value of a die
    fun setDiceValues(diceValues: ArrayList<Int>) {
        for (i in diceList.indices) {
            diceList[i].value = diceValues[i]
        }
    }

    // Get the die value of a die
    fun getDieValue(index : Int): Int {
        return diceList[index].value
    }

    // Get all dice values
    fun getDiceValues(): ArrayList<Int> {
        val diceValues = ArrayList<Int>()
        for (die in diceList) {
            diceValues.add(die.value)
        }
        return diceValues
    }

    // Set all dice to be unlocked
    fun clearLockedDice() {
        for (die in diceList) {
            die.locked = false
        }
    }

    // Get all locked dice
    fun getLockedDiceValues(): ArrayList<Int> {
        val lockedDiceValues = ArrayList<Int>()
        for (die in diceList) {
            if (die.locked) {
                lockedDiceValues.add(die.value)
            }
        }
        return lockedDiceValues
    }

    // Get the locked status of a die
    fun getDieLocked(index : Int) : Boolean {
        return diceList[index].locked
    }

    // Set the locked status of a die
    fun setDieLocked(index: Int, locked : Boolean) {
        diceList[index].locked = locked
    }

    // Use a die, preventing it from being locked
    fun setDieUsed(index: Int, used: Boolean) {
        diceList[index].used = used
    }

    // Get the locked status of a die
    fun getDieUsed(index : Int) : Boolean {
        return diceList[index].used
    }

    // Set all dice to be used
    fun setAllDiceUsed() {
        for (die in diceList) {
            die.used = true
        }
    }

    fun getAllUsedDiceValues() : ArrayList<Int> {
        val usedDiceValues = ArrayList<Int>()
        for (die in diceList) {
            if (die.locked) {
                usedDiceValues.add(die.value)
            }
        }
        return usedDiceValues
    }

    fun getAllUnusedDiceValues() : ArrayList<Int> {
        val usedDiceValues = ArrayList<Int>()
        for (die in diceList) {
            if (!die.used) {
                usedDiceValues.add(die.value)
            }
        }
        return usedDiceValues
    }

    fun clearUsedDice() {
        for (die in diceList) {
            die.used = false
        }
    }

    fun useLockedDice() {
        for (die in diceList) {
            if (die.locked) {
                die.used = true
            }
        }
    }

    companion object {
        private const val STATE_THROWS = "throwsLeft"
        private const val STATE_DICE_VALUES = "diceValues"
    }
}