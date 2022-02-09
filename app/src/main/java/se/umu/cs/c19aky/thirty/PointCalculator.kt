package se.umu.cs.c19aky.thirty

import android.os.Bundle
import android.util.Log

private const val TAG = "PointCalculator"

private const val KEY_POINTS = "se.umu.cs.c19aky.points"
private const val KEY_CATEGORIES = "se.umu.cs.c19aky.categories"
private const val KEY_SELECTED = "se.umu.cs.c19aky.selected_category"

class PointCalculator(numberOfCategories: Int = 9) {

    private var selectedCategory: String = ""
    private var categories: MutableMap<String, Int> = mutableMapOf()

    init {
        // Standard categories
        categories["Low"] = -1
        for (x in 4 until numberOfCategories + 4) {
            categories[x.toString()] = -1
        }
    }

    // Store categories, for restoring when able to
    fun storeCategories(outState: Bundle) {
        val points: ArrayList<Int> = arrayListOf()
        points.addAll(categories.values)
        outState.putIntegerArrayList(KEY_POINTS, points)

        val categories: ArrayList<String> = arrayListOf()
        categories.addAll(this.categories.keys)
        outState.putStringArrayList(KEY_CATEGORIES, categories)

        outState.putString(KEY_SELECTED, selectedCategory)
    }

    // Restore points, categories and selected category
    fun restoreCategories(savedInstanceState: Bundle) {
        val points: ArrayList<Int> = savedInstanceState.getIntegerArrayList(KEY_POINTS) as ArrayList<Int>
        val categories: ArrayList<String> = savedInstanceState.getStringArrayList(KEY_CATEGORIES) as ArrayList<String>

        for (x in 0 until categories.size) {
            this.categories[categories[x]] = points[x]
        }
        selectedCategory = savedInstanceState.getString(KEY_SELECTED) as String
    }

    // Check if a category has been chosen, returns true it is
    fun checkIfCategoryIsChosen(): Boolean {
        return selectedCategory != ""
    }

    // Unselect category
    fun unselectCategory() {
        selectedCategory = ""
    }

    // Select category, returns true if category could be selected
    fun selectCategory(category: String): Boolean {
        if (categories[category] == -1) {
            selectedCategory = category
            return true
        }
        return false
    }

    fun checkIfValidSelection(values: ArrayList<Int>): Boolean {
        return if (selectedCategory == "Low") {
            true
        } else {
            values.sum() == selectedCategory.toInt()
        }
    }

    // Calculate points using current category
    fun calculatePoints(valuesLow: ArrayList<Int>, values: ArrayList<Int>): Int {
        val sum = values.sum()
        return if (selectedCategory == "Low") {
            calculatePointsLow(valuesLow)
        } else {
            if (sum == selectedCategory.toInt()) {
                sum
            } else {
                -1
            }
        }
    }

    // Sum all values from 0 to 3
    private fun calculatePointsLow(values: ArrayList<Int>): Int {
        var foundAny = false
        var sum = 0
        for (value in values) {
            sum += if (value <= 3) {
                foundAny = true
                value
            } else {
                0
            }
        }
        return if (foundAny) {
            sum
        } else {
            -1
        }
    }

    // Add points to some category
    fun addPoints(points: Int) {
        var current = categories[selectedCategory]
        if (current != null) {
            current += if (current == -1) {
                points + 1
            } else {
                points
            }
        } else {
            current = points
        }
        categories[selectedCategory] = current
    }

    // Get points in current category
    fun getPoints(): Int? {
        return categories[selectedCategory]
    }

    // Get the total amount of points from all categories
    fun getTotalPoints(): Int {
        var sum = 0
        for (x in categories.values) {
            sum += x
        }
        return sum
    }

    // Get a list of points, each entry is the amount of points in that category
    fun getAllPoints(): ArrayList<Int> {
        return ArrayList(categories.values)
    }
}