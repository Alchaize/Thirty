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

private const val STATE_THROWS = "throwsLeft"
private const val STATE_DICE_VALUES = "diceValues"
private const val STATE_COUNT_STATE = "countState"
private const val STATE_ROUND_COUNTER = "currentRound"

class MainActivity : AppCompatActivity() {

    private lateinit var throwButton: Button
    private lateinit var throwCountText: TextView
    private lateinit var diceViewModel : DiceViewModel
    private lateinit var diceButtons: MutableList<ImageButton>
    private lateinit var categorySpinner: Spinner
    private var pointCalculator = PointCalculator()
    private var currentRound: Int = 1

    private var countState: Boolean = false

    override fun onSaveInstanceState(outState: Bundle) {
        Log.d(TAG, "Saving instance")
        outState.putInt(STATE_ROUND_COUNTER, currentRound)
        outState.putInt(STATE_THROWS, diceViewModel.getThrowsLeft())
        outState.putIntegerArrayList(STATE_DICE_VALUES, diceViewModel.getDiceValues())
        outState.putBoolean(STATE_COUNT_STATE, countState)
        pointCalculator.storeCategories(outState)
        super.onSaveInstanceState(outState)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        diceViewModel = ViewModelProvider(this).get(DiceViewModel::class.java)

        categorySpinner = findViewById(R.id.spinner_categories)
        throwButton = findViewById(R.id.btn_throw)
        throwCountText = findViewById(R.id.tv_throws_left)
        diceButtons = getDiceButtons()

        // On click listeners for dice
        for (imgButton in diceButtons) {
            imgButton.setOnClickListener {
                val index = diceButtons.indexOf(imgButton)
                diceViewModel.setDieLocked(index, !diceViewModel.getDieLocked(index))
                updateDiceButtonImages()
            }
        }

        // On click listener for throw button
        throwButton.setOnClickListener {

            when (val throwsLeft = diceViewModel.getThrowsLeft()) {

                0 -> {tryGettingPoints()}

                1 -> {countState = true
                    diceViewModel.throwDice()
                    updateThrowsLeft(throwCountText, 0)
                    updateDiceButtonImages()
                    throwButton.setText(R.string.btn_continue)}

                else -> {diceViewModel.throwDice()
                    updateDiceButtonImages()
                    updateThrowsLeft(throwCountText, throwsLeft-1)}
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

        categorySpinner.setSelection(0)

        if (savedInstanceState != null) {
            currentRound = savedInstanceState.getInt(STATE_ROUND_COUNTER)
            diceViewModel.setThrowsLeft(savedInstanceState.getInt(STATE_THROWS))
            diceViewModel.setDiceValues(savedInstanceState.getIntegerArrayList(STATE_DICE_VALUES) as ArrayList<Int>)
            updateThrowsLeft(throwCountText, diceViewModel.getThrowsLeft())
            updateDiceButtonImages()
            pointCalculator.restoreCategories(savedInstanceState)
        } else {
            startNewRound()
        }
    }

    private fun tryGettingPoints() {
        val selectedCategory: String = categorySpinner.selectedItem as String

        val sum = if (selectedCategory == "Low") {
            pointCalculator.calculatePointsLow(diceViewModel.getDiceValues())
        } else {
            pointCalculator.calculatePoints((selectedCategory).toInt(), diceViewModel.getLockedDiceValues())
        }

        if (sum >= 0) {
            Log.d(TAG, "Returning $sum, chosen category $selectedCategory")
            pointCalculator.addPoints(sum, selectedCategory)
            startNewRound()
        } else {
            Log.d(TAG, "Category already chosen, please choose a different one")
        }
    }

    private fun startNewRound() {
        currentRound += 1
        countState = false
        diceViewModel.clearLockedDice()
        diceViewModel.throwDice()
        diceViewModel.resetThrows()
        updateDiceButtonImages()
        updateThrowsLeft(throwCountText, diceViewModel.getThrowsLeft())
        throwButton.setText(R.string.btn_throw)
    }

    // Update the images on all of the dice
    private fun updateDiceButtonImages() {
        for (button in diceButtons) {
            updateButtonImage(button, diceButtons.indexOf(button))
        }
    }

    // Update the image of a die
    private fun updateButtonImage(button : ImageButton, index : Int) {

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

            val image = if (countState) {
                when(diceViewModel.getDieValue(index)) {
                    1 -> R.drawable.red1
                    2 -> R.drawable.red2
                    3 -> R.drawable.red3
                    4 -> R.drawable.red4
                    5 -> R.drawable.red5
                    6 -> R.drawable.red6
                    else -> throw Exception("Die value outside of 1-6")
                }
            } else {
                when(diceViewModel.getDieValue(index)) {
                    1 -> R.drawable.grey1
                    2 -> R.drawable.grey2
                    3 -> R.drawable.grey3
                    4 -> R.drawable.grey4
                    5 -> R.drawable.grey5
                    6 -> R.drawable.grey6
                    else -> throw Exception("Die value outside of 1-6")
                }
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

// Saving this here to use later when I have to display all the categories and their points
//                 0 -> {startCountPointsForResult.launch(Intent(this, PointCount::class.java).apply {
//                    putIntegerArrayListExtra(EXTRA_DICE_VALUES, diceViewModel.getDiceValues())
//                })
/*

private fun returnResult(pointSum: Int) {
    val data = Intent()
    data.putExtra(EXTRA_POINT_SUM, pointSum)
    setResult(Activity.RESULT_OK, data)
    finish()
}

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
*/