package se.umu.cs.c19aky.thirty

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class PointCount : AppCompatActivity() {

    private fun returnResult(pointSum: Int) {
        val data = Intent()
        data.putExtra(EXTRA_POINT_SUM, pointSum)
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_point_count)



    }



    companion object {
        const val EXTRA_POINT_SUM = "se.umu.cs.c19aky.thirty.point_sum"
    }
}