package se.umu.cs.c19aky.thirty

import android.util.Log

private const val TAG = "PointCalculator"

class PointCalculator {

    private var chosenCategories: MutableList<String> = mutableListOf()


    fun calculatePoints(targetSum: Int, values: ArrayList<Int>): Int {
        if (targetSum.toString() in chosenCategories) {
            return -1
        }
        chosenCategories.add(targetSum.toString())
        var sum = 0
        for (value in values) {
            sum += value
        }
        return if (sum % targetSum == 0) {
            sum
        } else {
            -1
        }
    }

    // Sum all values from 0 to 3
    fun calculatePointsLow(values: ArrayList<Int>): Int {
        if ("Low" in chosenCategories){
            return -1
        }
        chosenCategories.add("Low")
        var sum = 0
        for (value in values) {
            sum += if (value <= 3) {
                value
            } else {
                0
            }
        }
        return sum
    }

}