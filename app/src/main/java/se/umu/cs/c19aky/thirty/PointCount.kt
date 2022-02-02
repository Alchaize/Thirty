package se.umu.cs.c19aky.thirty

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class PointCount : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_point_count)
    }



    companion object {
        const val EXTRA_POINT_SUM = "se.umu.cs.c19aky.thirty.point_sum"
    }
}