package se.umu.cs.c19aky.thirty

import android.app.Activity
import android.content.Intent
import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner

private const val TAG = "PointCount"

class PointCount : AppCompatActivity() {

    private lateinit var diceButtons: MutableList<ImageButton>
    private lateinit var diceViewModel : DiceViewModel
    private lateinit var categorySpinner: Spinner
    private lateinit var pointCalculator: PointCalculator
    private lateinit var buttonDone: Button

    private fun returnResult(pointSum: Int) {
        val data = Intent()
        data.putExtra(EXTRA_POINT_SUM, pointSum)
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_point_count)

        val diceValues = intent.getIntegerArrayListExtra(EXTRA_DICE_VALUES)
        diceViewModel = DiceViewModel()
        diceButtons = getDiceButtons()
        categorySpinner = findViewById(R.id.spinner_categories)
        pointCalculator = PointCalculator()
        buttonDone = findViewById(R.id.btn_done)

        if (diceValues != null) {
            for (value in diceValues) {
                Log.d(TAG, value.toString())
            }
            diceViewModel.setDiceValues(diceValues)
        }

        // On click listeners for dice
        for (imgButton in diceButtons) {
            imgButton.setOnClickListener {
                val index = diceButtons.indexOf(imgButton)
                diceViewModel.setDieLocked(index, !diceViewModel.getDieLocked(index))
                updateButtonImage(imgButton, diceButtons.indexOf(imgButton), diceViewModel)
            }
            updateButtonImage(imgButton, diceButtons.indexOf(imgButton), diceViewModel)
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

        categorySpinner.setSelection(0)

        buttonDone.setOnClickListener {
            val selectedCategory = categorySpinner.selectedItem

            val sum = if (selectedCategory == "Low") {
                pointCalculator.calculatePointsLow(diceViewModel.getDiceValues())
            } else {
                pointCalculator.calculatePoints((selectedCategory as String).toInt(), diceViewModel.getLockedDiceValues())
            }

            if (sum >= 0) {
                Log.d(TAG, "Returning $sum, chosen category $selectedCategory")
                returnResult(sum)
            } else {
                // Tell user that they need to redo their selection of dice
                TODO()
            }
        }

    }



    companion object {
        const val EXTRA_POINT_SUM = "se.umu.cs.c19aky.thirty.point_sum"
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
                1 -> R.drawable.red1
                2 -> R.drawable.red2
                3 -> R.drawable.red3
                4 -> R.drawable.red4
                5 -> R.drawable.red5
                6 -> R.drawable.red6
                else -> throw Exception("Die value outside of 1-6")
            }
            button.setImageResource(image)
        }
    }
}