package se.umu.cs.c19aky.thirty

import android.os.Bundle

private const val TAG = "PointCalculator"

private const val KEY_POINTS = "se.umu.cs.c19aky.categories"

class PointCalculator {

    private var categories: MutableMap<String, Int> = mutableMapOf()

    init {
        categories["Low"] = -1
        for (x in 4..13) {
            categories[x.toString()] = -1
        }
    }

    fun storeCategories(outState: Bundle) {
        val toSave: ArrayList<Int> = arrayListOf()
        toSave.addAll(categories.values)
        outState.putIntegerArrayList(KEY_POINTS, toSave)
    }

    fun restoreCategories(outState: Bundle) {
        val savedCategories: ArrayList<Int> = outState.getIntegerArrayList(KEY_POINTS) as ArrayList<Int>
        categories["Low"] = savedCategories[0]
        for (x in 4..13) {
            categories[x.toString()] = savedCategories[x-3]
        }
    }

    // Calculate points
    fun calculatePoints(targetSum: Int, values: ArrayList<Int>): Int {
        if (categories[targetSum.toString()] != -1) {
            return -1
        }

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
        if (categories["Low"] != -1) {
            return -1
        }

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


    fun addPoints(points: Int, category: String) {
        categories[category] = points
    }

    fun getPoints(category: String): Int? {
        return categories[category]
    }
}