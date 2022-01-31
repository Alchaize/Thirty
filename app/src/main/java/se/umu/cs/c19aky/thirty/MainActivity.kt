package se.umu.cs.c19aky.thirty

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val diceViewModel : DiceViewModel = ViewModelProvider(this).get(DiceViewModel::class.java)

        val throwButton : Button = findViewById(R.id.btn_throw)
        val categorySpinner : Spinner = findViewById(R.id.spinner_categories)

        val diceButtons = mutableListOf<ImageButton>()
        diceButtons.add(findViewById(R.id.die_one))
        diceButtons.add(findViewById(R.id.die_two))
        diceButtons.add(findViewById(R.id.die_three))
        diceButtons.add(findViewById(R.id.die_four))
        diceButtons.add(findViewById(R.id.die_five))
        diceButtons.add(findViewById(R.id.die_six))

        for (imgButton in diceButtons) {
            imgButton.setOnClickListener {
                val index = diceButtons.indexOf(imgButton)
                diceViewModel.setDieLocked(index, !diceViewModel.getDieLocked(index))
                updateDiceButtonImages(diceButtons, diceViewModel)
            }
        }

        throwButton.setOnClickListener {
            diceViewModel.throwDice()
            updateDiceButtonImages(diceButtons, diceViewModel)
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

    }

    private fun updateDiceButtonImages(diceButtons : MutableList<ImageButton>, diceViewModel : DiceViewModel) {
        for (button in diceButtons) {
            updateButtonImage(button, diceButtons.indexOf(button), diceViewModel)
        }
    }

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
}