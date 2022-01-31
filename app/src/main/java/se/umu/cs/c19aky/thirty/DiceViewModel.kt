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

    fun getDieValue(index : Int): Int {
        return diceList[index].value
    }

    fun getDieLocked(index : Int) : Boolean {
        return diceList[index].locked
    }

    fun setDieLocked(index: Int, locked : Boolean) {
        diceList[index].locked = locked
    }
}