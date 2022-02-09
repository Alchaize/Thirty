package se.umu.cs.c19aky.thirty

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Space
import android.widget.TextView

private const val TAG = "GameResults"

/* Activity for displaying the results of the game */
class GameResults : AppCompatActivity() {

    private var pointSum: Int = 0
    private lateinit var values: ArrayList<Int>

    private fun returnResult() {
        val data = Intent()
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_results)

        val results: LinearLayout = findViewById(R.id.LL_results)

        Log.d(TAG, "Getting stored categories and values")
        values = intent.getIntegerArrayListExtra(EXTRA_DICE_VALUES) as ArrayList<Int>
        pointSum = intent.getIntExtra(EXTRA_POINT_SUM, 0)

        val categories: Array<String> = resources.getStringArray(R.array.categories_array)


        Log.d(TAG, "Adding categories and values to linear layout")
        for (x in categories.indices) {
            results.addView(createTextView("${categories[x]}    ${values[x]}"))
            results.addView(createSpacing())
        }
        // Add sum
        results.addView(createTextView("Sum    $pointSum"))

        // Add button to go back
        val returnButton = Button(this)
        returnButton.setText(R.string.btn_done)
        returnButton.gravity = Gravity.CENTER_HORIZONTAL
        returnButton.textSize = 24.0f
        returnButton.setOnClickListener { returnResult() }
        results.addView(returnButton)

        Log.d(TAG, "Done")
    }


    companion object {
        const val EXTRA_DICE_VALUES = "Dice_values"
        const val EXTRA_POINT_SUM = "Point_sum"
    }

    private fun createTextView(text: String): TextView {
        val textView = TextView(this)
        textView.gravity = Gravity.CENTER
        textView.textSize = 24.0f
        textView.text = text
        return textView
    }

    private fun createSpacing(): Space {
        val space: Space = Space(this)
        space.minimumHeight = 24
        return space
    }
}