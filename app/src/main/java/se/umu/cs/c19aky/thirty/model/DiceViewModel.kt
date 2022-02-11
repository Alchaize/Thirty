package se.umu.cs.c19aky.thirty.model

import androidx.lifecycle.ViewModel
import se.umu.cs.c19aky.thirty.model.Die
import kotlin.random.Random

private const val TAG = "DiceViewModel"
private const val MAX_THROWS = 2


/*
* Class for keeping track of the dice, it's a ViewModel mostly because I wanted to try using one.
* */
class DiceViewModel : ViewModel() {

    private var throwsLeft = MAX_THROWS
    private val diceList = listOf(
        Die(1, locked = false, used = false),
        Die( 1, locked = false, used = false),
        Die( 1, locked = false, used = false),
        Die( 1, locked = false, used = false),
        Die( 1, locked = false, used = false),
        Die(1, locked = false, used = false),
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

    // Get all dice that are used
    fun getAllUsedDiceValues() : ArrayList<Int> {
        val usedDiceValues = ArrayList<Int>()
        for (die in diceList) {
            if (die.locked) {
                usedDiceValues.add(die.value)
            }
        }
        return usedDiceValues
    }

    // Get all dice that are unused
    fun getAllUnusedDiceValues() : ArrayList<Int> {
        val usedDiceValues = ArrayList<Int>()
        for (die in diceList) {
            if (!die.used) {
                usedDiceValues.add(die.value)
            }
        }
        return usedDiceValues
    }

    // Make all dice unused
    fun clearUsedDice() {
        for (die in diceList) {
            die.used = false
        }
    }

    // Use every locked die
    fun useLockedDice() {
        for (die in diceList) {
            if (die.locked) {
                die.used = true
            }
        }
    }

    // Get dice locked states
    fun getDiceLockedStates(): BooleanArray? {
        val booleanArray: BooleanArray? = BooleanArray(diceList.size)
        for (x in diceList.indices) {
            booleanArray?.set(x, diceList[x].locked)
        }
        return booleanArray
    }

    // Get dice locked states
    fun getDiceUsedStates(): BooleanArray? {
        val booleanArray: BooleanArray? = BooleanArray(diceList.size)
        for (x in diceList.indices) {
            booleanArray?.set(x, diceList[x].used)
        }
        return booleanArray
    }

    // Set dice locked states
    fun setDiceLockedStates(array: BooleanArray?) {
        for (x in diceList.indices) {
            diceList[x].locked = array?.get(x) == true
        }
    }

    // Set dice locked states
    fun setDiceUsedStates(array: BooleanArray?) {
        for (x in diceList.indices) {
            diceList[x].used = array?.get(x) == true
        }
    }

    companion object {
        private const val STATE_THROWS = "throwsLeft"
        private const val STATE_DICE_VALUES = "diceValues"
        private const val STATE_DICE_LOCKED = "diceLocked"
        private const val STATE_DICE_USED = "diceUsed"
    }
}