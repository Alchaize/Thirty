package se.umu.cs.c19aky.thirty

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import se.umu.cs.c19aky.thirty.GameResults.Companion.EXTRA_CATEGORIES
import se.umu.cs.c19aky.thirty.GameResults.Companion.EXTRA_DICE_VALUES

private const val TAG = "MainActivity"

private const val STATE_THROWS = "throwsLeft"
private const val STATE_DICE_VALUES = "diceValues"
private const val STATE_COUNT_STATE = "countState"
private const val STATE_ROUND_COUNTER = "currentRound"

private const val MAX_ROUNDS = 2

class MainActivity : AppCompatActivity() {

    private lateinit var throwButton: Button
    private lateinit var throwCountText: TextView
    private lateinit var diceViewModel : DiceViewModel
    private lateinit var diceButtons: MutableList<ImageButton>
    private lateinit var categorySpinner: Spinner
    private var pointCalculator = PointCalculator()
    private var currentRound: Int = 1

    private var countState: Boolean = false

    private val startCountPointsForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            currentRound = 1
        }
    }

    // Save instance
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

                0 -> {if(tryGettingPoints()) {
                    startNewRound()
                }}

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

        // Select the first item in the spinner
        categorySpinner.setSelection(0)

        // If instance has been saved, restore that data
        if (savedInstanceState != null) {
            Log.d(TAG, "Restoring instance")
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

    // Try to get points using the currently selected spinner and dice
    private fun tryGettingPoints(): Boolean {
        val selectedCategory: String = categorySpinner.selectedItem as String

        // Check if category is already used
        if (!pointCalculator.checkIfCategoryIsChosen(selectedCategory)) {

            // Use the appropriate way to calculate the sum to store
            val sum = if (selectedCategory == "Low") {
                pointCalculator.calculatePointsLow(diceViewModel.getDiceValues())
            } else {
                pointCalculator.calculatePoints((selectedCategory).toInt(), diceViewModel.getLockedDiceValues())
            }

            // Make sure that the selected dice works for the current category before storing
            return checkSelection(sum, selectedCategory)

        } else {
            val toast = Toast.makeText(this, resources.getString(R.string.tt_category_already_chosen), Toast.LENGTH_SHORT)
            toast.show()
            return false
        }

    }

    // Make sure that only valid dice selections are stored.
    private fun checkSelection(sum: Int, category: String): Boolean {
        return if (sum >= 0) {
            pointCalculator.addPoints(sum, category)
            true
        } else {
            val toast = Toast.makeText(this, resources.getString(R.string.tt_invalid_selection), Toast.LENGTH_SHORT)
            toast.show()
            false
        }
    }

    // Start a new round
    private fun startNewRound() {
        if (currentRound == MAX_ROUNDS) {
            Log.d(TAG, "Game completed, now it's time to show the player the result.")
            startCountPointsForResult.launch(Intent(this, GameResults::class.java).apply {
                    putIntegerArrayListExtra(EXTRA_DICE_VALUES, pointCalculator.getAllPoints())
                    putStringArrayListExtra(EXTRA_CATEGORIES, pointCalculator.getAllCategories())
                })
        }
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

            // Use different dice depending on which state the player is in
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