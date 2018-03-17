package com.example.aaron.recipeassistant.readrecipe.view

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.example.aaron.recipeassistant.R

abstract class RecipeCardItemText(activity: AppCompatActivity, text: String) : TextView(activity) {

    val selected = MutableLiveData<Boolean>()
    abstract val selectedPadding: Int
    abstract val unselectedPadding: Int

    init {
        selected.observe(activity, Observer {
            textSize = if (it == true) {
                setTypeface(null, Typeface.BOLD)
                setBackgroundColor(context.getColor(R.color.selectedItem))
                setPadding(8,selectedPadding,8,selectedPadding)
                16f
            } else {
                setTypeface(null, Typeface.NORMAL)
                setBackgroundColor(context.getColor(R.color.colorLightest))
                setPadding(8, unselectedPadding, 8, unselectedPadding)
                16f
            }
        })
        setText(text)
    }
}

class IngredientCardItemText(activity: AppCompatActivity, text: String) : RecipeCardItemText(activity, text) {
    override val selectedPadding = 28
    override val unselectedPadding = 4
    init {
        setLineSpacing(1.2f, 1f)
    }

}

class DirectionCardItemText(activity: AppCompatActivity, text: String) : RecipeCardItemText(activity, text) {
    override val selectedPadding = 28
    override val unselectedPadding = 12
    init {
        setLineSpacing(2f, 1f)

    }
}