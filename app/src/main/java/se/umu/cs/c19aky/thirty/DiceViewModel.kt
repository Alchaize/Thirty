package se.umu.cs.c19aky.thirty

import androidx.lifecycle.ViewModel
import kotlin.random.Random

class DiceViewModel : ViewModel() {

    private val diceList = listOf(
        Die(1, false),
        Die( 1, false),
        Die( 1, false),
        Die( 1, false),
        Die( 1, false),
        Die(1, false),
    )

    fun throwDice() {
        for (die in diceList) {
            if (!die.locked) {
                die.value = Random.nextInt(1,7)
            }
        }
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