package se.umu.cs.c19aky.thirty

import android.os.Bundle
import android.util.Log

private const val TAG = "PointCalculator"

private const val KEY_POINTS = "se.umu.cs.c19aky.points"
private const val KEY_CATEGORIES = "se.umu.cs.c19aky.categories"
private const val KEY_SELECTED = "se.umu.cs.c19aky.selected_category"

/*
* Class for calculating and keeping track of points in categories*/
class PointCalculator(numberOfCategories: Int = 9) {

    private var selectedCategory: Int = -1
    private var categories: MutableList<Category> = mutableListOf()

    init {
        // Standard categories
        categories.add(Category("Low", -1))
        for (x in 1 until numberOfCategories + 1) {
            categories.add(Category((x+3).toString(), -1))
        }
    }

    // Store categories, for restoring when able to
    fun storeCategories(outState: Bundle) {
        val points: ArrayList<Int> = arrayListOf()
        for (x in this.categories) {
            points.add(x.points)
        }
        outState.putIntegerArrayList(KEY_POINTS, points)

        val categories: ArrayList<String> = arrayListOf()
        for (x in this.categories) {
            categories.add(x.categoryName)
        }
        outState.putStringArrayList(KEY_CATEGORIES, categories)

        outState.putInt(KEY_SELECTED, selectedCategory)
    }

    // Restore points, categories and selected category
    fun restoreCategories(savedInstanceState: Bundle) {
        categories.clear()
        val points: ArrayList<Int> = savedInstanceState.getIntegerArrayList(KEY_POINTS) as ArrayList<Int>
        val categories: ArrayList<String> = savedInstanceState.getStringArrayList(KEY_CATEGORIES) as ArrayList<String>

        for (x in 0 until categories.size) {
            this.categories.add(Category(categories[x], points[x]))
        }
        this.selectedCategory = savedInstanceState.getInt(KEY_SELECTED)
    }

    // Check if a category has been chosen, returns true it is
    fun checkIfCategoryIsChosen(): Boolean {
        return selectedCategory != -1
    }

    // Unselect category
    fun unselectCategory() {
        selectedCategory = -1
    }

    // Select category, returns true if category could be selected
    fun selectCategory(category: String): Boolean {
        for (x in categories.indices) {
            if (categories[x].categoryName == category) {
                if (categories[x].points == -1) {
                    selectedCategory = x
                    return true
                }
            }
        }
        return false
    }

    fun checkIfValidSelection(values: ArrayList<Int>): Boolean {
        return if (categories[selectedCategory].categoryName == "Low") {
            true
        } else {
            values.sum() == categories[selectedCategory].categoryName.toInt()
        }
    }

    // Calculate points using current category
    fun calculatePoints(values: ArrayList<Int>): Int {
        return if (categories[selectedCategory].categoryName == "Low") {
            calculatePointsLow(values)
        } else {
            val sum = values.sum()
            if (sum == categories[selectedCategory].categoryName.toInt()) {
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

    // Add points to currently selected category
    fun addPoints(points: Int) {
        categories[selectedCategory].points += if (categories[selectedCategory].points == -1) {
            points + 1
        } else {
            points
        }
    }

    // Get the total amount of points from all categories
    fun getTotalPoints(): Int {
        var sum = 0
        for (x in categories) {
            sum += x.points
        }
        return sum
    }

    // Get a list of points, each entry is the amount of points in that category
    fun getAllPoints(): ArrayList<Int> {
        val points: ArrayList<Int> = ArrayList()
        for (x in categories) {
            points.add(x.points)
        }
        return points
    }
}