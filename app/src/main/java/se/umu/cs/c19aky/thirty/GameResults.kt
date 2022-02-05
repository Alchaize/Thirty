package se.umu.cs.c19aky.thirty

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class GameResults : AppCompatActivity() {



    private fun returnResult(pointSum: Int) {
        val data = Intent()
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_results)
    }


    companion object {
        const val EXTRA_DICE_VALUES = "Dice_values"
        const val EXTRA_CATEGORIES = "Categories"
    }
}