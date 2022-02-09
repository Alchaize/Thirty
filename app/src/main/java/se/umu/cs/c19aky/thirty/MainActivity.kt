package se.umu.cs.c19aky.thirty

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import se.umu.cs.c19aky.thirty.GameResults.Companion.EXTRA_DICE_VALUES
import se.umu.cs.c19aky.thirty.GameResults.Companion.EXTRA_POINT_SUM

private const val TAG = "MainActivity"

private const val STATE_THROWS = "throwsLeft"
private const val STATE_DICE_VALUES = "diceValues"

class MainActivity : AppCompatActivity() {

    private lateinit var throwButton: Button
    private lateinit var throwCountText: TextView
    private lateinit var diceViewModel : DiceViewModel
    private lateinit var diceButtons: MutableList<ImageButton>
    private lateinit var categorySpinner: Spinner
    private var gameLogic = GameLogic()

    private val startCountPointsForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
        startNewGame()
    }

    // Save instance
    override fun onSaveInstanceState(outState: Bundle) {
        Log.d(TAG, "Saving instance")
        outState.putInt(STATE_THROWS, diceViewModel.getThrowsLeft())
        outState.putIntegerArrayList(STATE_DICE_VALUES, diceViewModel.getDiceValues())
        gameLogic.saveInstance(outState)
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
                if (!diceViewModel.getDieUsed(index)) {
                    diceViewModel.setDieLocked(index, !diceViewModel.getDieLocked(index))
                    updateDiceButtonImages()
                }
            }
        }

        // On click listener for throw button
        throwButton.setOnClickListener {
            when (diceViewModel.getThrowsLeft()) {
                0 -> {calculatingPhase()}
                1 -> {nextPhase()
                    throwButton.setText(R.string.btn_select)}
                else -> {nextPhase()}
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
            diceViewModel.setThrowsLeft(savedInstanceState.getInt(STATE_THROWS))
            diceViewModel.setDiceValues(savedInstanceState.getIntegerArrayList(STATE_DICE_VALUES) as ArrayList<Int>)
            updateThrowsLeft(diceViewModel.getThrowsLeft())
            gameLogic.restoreInstance(savedInstanceState)
            updateDiceButtonImages()
        } else {
            Log.d(TAG, "No saved instance, starting new game")
            startNewGame()
        }
    }

    // Run the calculating phase, before ending the round
    private fun calculatingPhase() {
        // Check if category has been chosen
        if (gameLogic.categorySelected()) {
            // Check selection
            if (gameLogic.checkIfValidSelection(diceViewModel)) {
                // If the selection was valid. Try to add points
                if (gameLogic.runCountPhase(diceViewModel)){
                    // Message user about result
                    messageUserAboutSelection(true)
                    updateDiceButtonImages()
                } else {
                    // If no points were added, continue to next round
                    startNewRound()
                }
            } else {
                // Message user about result
                messageUserAboutSelection(false)
            }
        } else {
            // Lock user to a category if none has been chosen
            lockUserToCategory()
        }
    }

    // Lock user to the currently chosen category. Returns true if possible
    private fun lockUserToCategory(): Boolean {
        val category = categorySpinner.selectedItem as String

        // Lock user to a category if it hasn't been chosen yet
        return if (gameLogic.selectCategory(category)) {
            val toast = Toast.makeText(this, resources.getString(R.string.tt_category_selected), Toast.LENGTH_SHORT)
            toast.show()

            // Indicate that user will be adding points now
            throwButton.setText(R.string.btn_add)
            true
        } else {
            val toast = Toast.makeText(this, resources.getString(R.string.tt_category_already_chosen), Toast.LENGTH_SHORT)
            toast.show()
            false
        }
    }

    // Message user on whether the selection was valid or not
    private fun messageUserAboutSelection(valid: Boolean) {
        if (valid) {
            val toast = Toast.makeText(this, resources.getString(R.string.tt_added_points), Toast.LENGTH_SHORT)
            toast.show()
        } else {
            val toast = Toast.makeText(this, resources.getString(R.string.tt_invalid_selection), Toast.LENGTH_SHORT)
            toast.show()
        }
    }

    // Start a new game
    private fun startNewGame() {
        gameLogic.newGame(diceViewModel)
        updateDiceButtonImages()
        updateThrowsLeft(diceViewModel.getThrowsLeft())
    }

    // Start a new round
    private fun startNewRound() {
        if (!gameLogic.isGameDone()) {
            Log.d(TAG, "Starting a new round")
            gameLogic.nextRound(diceViewModel)
            updateDiceButtonImages()
            updateThrowsLeft(diceViewModel.getThrowsLeft())
            throwButton.setText(R.string.btn_throw)
        } else {
            Log.d(TAG, "Booting result activity")
            // Game is now completed
            startCountPointsForResult.launch(Intent(this, GameResults::class.java).apply {
                putIntegerArrayListExtra(EXTRA_DICE_VALUES, gameLogic.getPoints())
                putExtra(EXTRA_POINT_SUM, gameLogic.getTotalPoints())
            })
        }
    }

    // Move to next phase
    private fun nextPhase() {
        gameLogic.nextPhase(diceViewModel)
        updateDiceButtonImages()
        updateThrowsLeft(diceViewModel.getThrowsLeft())
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
            val image = if (!gameLogic.getCountPhase()) {
                // Grey dice for the normal phases
                when(diceViewModel.getDieValue(index)) {
                    1 -> R.drawable.grey1
                    2 -> R.drawable.grey2
                    3 -> R.drawable.grey3
                    4 -> R.drawable.grey4
                    5 -> R.drawable.grey5
                    6 -> R.drawable.grey6
                    else -> throw Exception("Die value outside of 1-6")
                }
            } else {
                // Red dice for counting phase
                when(diceViewModel.getDieValue(index)) {
                    1 -> R.drawable.red1
                    2 -> R.drawable.red2
                    3 -> R.drawable.red3
                    4 -> R.drawable.red4
                    5 -> R.drawable.red5
                    6 -> R.drawable.red6
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
    private fun updateThrowsLeft(throwsLeft: Int) {
        Log.d(TAG, "Throws left $throwsLeft")
        var str: String = resources.getString(R.string.tv_throws_left)
        str += " $throwsLeft"
        throwCountText.text = str
    }
}