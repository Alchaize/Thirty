package se.umu.cs.c19aky.thirty

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.lifecycle.ViewModelProvider

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val diceViewModel : DiceViewModel = ViewModelProvider(this).get(DiceViewModel::class.java)

        val throwButton : Button = findViewById(R.id.btn_throw)
        val categorySpinner : Spinner = findViewById(R.id.spinner_categories)
        val throwCountText : TextView = findViewById(R.id.tv_throws_left)
        val diceButtons = getDiceButtons()

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
            val throwsLeft = diceViewModel.getThrowsLeft()
            if (throwsLeft > 0) {
                diceViewModel.throwDice()
                updateDiceButtonImages(diceButtons, diceViewModel)
                updateThrowsLeft(throwCountText, throwsLeft-1)
            } else {
                updateThrowsLeft(throwCountText, throwsLeft)
            }
        }

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.categories_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            categorySpinner.adapter = adapter
        }

        // Throw the dice once before the user gets to interact, to make sure the dice are randomized
        diceViewModel.throwDice()
        diceViewModel.resetThrows()
        updateDiceButtonImages(diceButtons, diceViewModel)
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