package se.umu.cs.c19aky.thirty

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider

private const val TAG = "MainActivity"

const val EXTRA_DICE_VALUES = "se.umu.cs.c19aky.thirty.dice_values"

private const val STATE_THROWS = "throwsLeft"
private const val STATE_DICE_VALUES = "diceValues"

class MainActivity : AppCompatActivity() {

    private lateinit var throwButton: Button
    private lateinit var throwCountText: TextView
    private lateinit var diceViewModel : DiceViewModel
    private lateinit var diceButtons: MutableList<ImageButton>

    private val startCountPointsForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val i = it.data
                if (i != null) {
                    val points = i.getIntExtra(PointCount.EXTRA_POINT_SUM, 0)
                    diceViewModel.addPoints(points)
                }
            } else {
                diceViewModel.setThrowsLeft(0)
                updateThrowsLeft(throwCountText, 0)
            }
        }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.d(TAG, "Saving instance")
        outState.putInt(STATE_THROWS, diceViewModel.getThrowsLeft())
        outState.putIntegerArrayList(STATE_DICE_VALUES, diceViewModel.getDiceValues())
        super.onSaveInstanceState(outState)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        diceViewModel = ViewModelProvider(this).get(DiceViewModel::class.java)

        throwButton = findViewById(R.id.btn_throw)
        throwCountText = findViewById(R.id.tv_throws_left)

        diceButtons = getDiceButtons()

        // On click listeners for dice
        for (imgButton in diceButtons) {
            imgButton.setOnClickListener {
                val index = diceButtons.indexOf(imgButton)
                diceViewModel.setDieLocked(index, !diceViewModel.getDieLocked(index))
                updateDiceButtonImages(diceButtons, diceViewModel)
            }
        }

        // On click listener for throw button
        throwButton.setOnClickListener {

            when (val throwsLeft = diceViewModel.getThrowsLeft()) {
                0 -> {startCountPointsForResult.launch(Intent(this, PointCount::class.java).apply {
                    putIntegerArrayListExtra(EXTRA_DICE_VALUES, diceViewModel.getDiceValues())
                })
                diceViewModel.resetThrows()}

                1 -> {diceViewModel.throwDice()
                    updateDiceButtonImages(diceButtons, diceViewModel)
                    updateThrowsLeft(throwCountText, 0)
                    throwButton.setText(R.string.btn_continue)}

                else -> {diceViewModel.throwDice()
                    updateDiceButtonImages(diceButtons, diceViewModel)
                    updateThrowsLeft(throwCountText, throwsLeft-1)}
            }
        }

        // Throw the dice once before the user gets to interact, to make sure the dice are randomized
        if (savedInstanceState != null) {
            diceViewModel.setThrowsLeft(savedInstanceState.getInt(STATE_THROWS))
            diceViewModel.setDiceValues(savedInstanceState.getIntegerArrayList(STATE_DICE_VALUES) as ArrayList<Int>)
            updateThrowsLeft(throwCountText, diceViewModel.getThrowsLeft())
            updateDiceButtonImages(diceButtons, diceViewModel)
        } else {
            diceViewModel.throwDice()
            diceViewModel.resetThrows()
            updateDiceButtonImages(diceButtons, diceViewModel)
        }
    }

    // Update the images on all of the dice
    private fun updateDiceButtonImages(diceButtons : MutableList<ImageButton>, diceViewModel : DiceViewModel) {
        for (button in diceButtons) {
            updateButtonImage(button, diceButtons.indexOf(button), diceViewModel)
        }
    }

    // Update the image of a die
    private fun updateButtonImage(button : ImageButton, index : Int, diceViewModel: DiceViewModel) {

        if (!diceViewModel.getDieLocked(index)) {
            val image = when(diceViewModel.getDieValue(index)) {
                1 -> R.drawable.white1
                2 -> R.drawable.white2
                3 -> R.drawable.white3
                4 -> R.drawable.white4
                5 -> R.drawable.white5
                6 -> R.drawable.white6
                else -> throw Exception("Die value outside of 1-6")
            }
            button.setImageResource(image)
        } else {
            val image = when(diceViewModel.getDieValue(index)) {
                1 -> R.drawable.grey1
                2 -> R.drawable.grey2
                3 -> R.drawable.grey3
                4 -> R.drawable.grey4
                5 -> R.drawable.grey5
                6 -> R.drawable.grey6
                else -> throw Exception("Die value outside of 1-6")
            }
            button.setImageResource(image)
        }
    }

    // Get the dice buttons
    private fun getDiceButtons(): MutableList<ImageButton> {
        val diceButtons = mutableListOf<ImageButton>()
        diceButtons.add(findViewById(R.id.die_one))
        diceButtons.add(findViewById(R.id.die_two))
        diceButtons.add(findViewById(R.id.die_three))
        diceButtons.add(findViewById(R.id.die_four))
        diceButtons.add(findViewById(R.id.die_five))
        diceButtons.add(findViewById(R.id.die_six))
        return diceButtons
    }

    // Update the text showing how many throws are left
    private fun updateThrowsLeft(textView: TextView, throwsLeft: Int) {
        when(throwsLeft) {
            2 -> textView.setText(R.string.tv_throws_left_2)
            1 -> textView.setText(R.string.tv_throws_left_1)
            else -> textView.setText(R.string.tv_throws_left_0)
        }
    }
}