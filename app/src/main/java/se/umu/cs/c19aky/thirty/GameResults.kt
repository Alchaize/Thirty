package se.umu.cs.c19aky.thirty

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import androidx.core.view.setPadding
import org.w3c.dom.Text

private const val TAG = "GameResults"

class GameResults : AppCompatActivity() {



    private fun returnResult(pointSum: Int) {
        val data = Intent()
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_results)

        val results: LinearLayout = findViewById(R.id.LL_results)

        Log.d(TAG, "Getting stored categories and values")
        val values: ArrayList<Int> = intent.getIntegerArrayListExtra(EXTRA_DICE_VALUES) as ArrayList<Int>

        val categories2: Array<String> = resources.getStringArray(R.array.categories_array)


        Log.d(TAG, "Adding categories and values to linear layout")
        for (x in 0 until categories2.size) {
            val textView = TextView(this)
            textView.gravity = Gravity.CENTER
            textView.textSize = 24.0f
            textView.text = "${categories2[x]} \t ${values[x]}"
            results.addView(textView)
        }
        Log.d(TAG, "Done")
    }


    companion object {
        const val EXTRA_DICE_VALUES = "Dice_values"
    }
}