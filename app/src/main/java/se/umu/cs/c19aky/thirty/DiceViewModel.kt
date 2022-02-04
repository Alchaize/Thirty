package se.umu.cs.c19aky.thirty

import androidx.lifecycle.ViewModel
import kotlin.random.Random

private const val TAG = "DiceViewModel"
private const val MAX_THROWS = 2

class DiceViewModel : ViewModel() {

    private var throwsLeft = MAX_THROWS
    private val diceList = listOf(
        Die(1, false),
        Die( 1, false),
        Die( 1, false),
        Die( 1, false),
        Die( 1, false),
        Die(1, false),
    )

    fun getThrowsLeft(): Int {
        return throwsLeft
    }

    fun setThrowsLeft(throwCount: Int) {
        throwsLeft = throwCount
    }

    fun resetThrows() {
        throwsLeft = MAX_THROWS
    }

    fun throwDice() {
        for (die in diceList) {
            if (!die.locked) {
                die.value = Random.nextInt(1,7)
            }
        }
        throwsLeft -= 1
    }

    fun setDiceValues(diceValues: ArrayList<Int>) {
        for (i in diceList.indices) {
            diceList[i].value = diceValues[i]
        }
    }

    fun getDieValue(index : Int): Int {
        return diceList[index].value
    }

    fun getDiceValues(): ArrayList<Int> {
        val diceValues = ArrayList<Int>()
        for (die in diceList) {
            diceValues.add(die.value)
        }
        return diceValues
    }

    fun clearLockedDice() {
        for (die in diceList) {
            die.locked = false
        }
    }

    fun getLockedDiceValues(): ArrayList<Int> {
        val lockedDiceValues = ArrayList<Int>()
        for (die in diceList) {
            if (die.locked) {
                lockedDiceValues.add(die.value)
            }
        }
        return lockedDiceValues
    }

    fun getDieLocked(index : Int) : Boolean {
        return diceList[index].locked
    }

    fun setDieLocked(index: Int, locked : Boolean) {
        diceList[index].locked = locked
    }
}