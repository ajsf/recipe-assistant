package com.example.aaron.recipeassistant.readrecipe.view

import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.TextView
import com.example.aaron.recipeassistant.R

abstract class RecipeCardItemText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    text: String = ""
) : TextView(context, attrs, defStyleAttr) {

    val selected = MutableLiveData<Boolean>()
    abstract val selectedPadding: Int
    abstract val unselectedPadding: Int

    init {
        setText(text)
    }

    fun selectItem() {
        setTypeface(null, Typeface.BOLD)
        setBackgroundColor(context.getColor(R.color.selectedItem))
        setPadding(8, selectedPadding, 8, selectedPadding)
    }

    fun unselectItem() {
        setTypeface(null, Typeface.NORMAL)
        setBackgroundColor(context.getColor(R.color.colorLightest))
        setPadding(8, unselectedPadding, 8, unselectedPadding)
    }
}

class IngredientCardItemText
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    text: String = ""
) : RecipeCardItemText(context, attrs, defStyleAttr, text) {
    override val selectedPadding = 28
    override val unselectedPadding = 4

    init {
        setLineSpacing(1.2f, 1f)
    }
}

class DirectionCardItemText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    text: String = ""
) :
    RecipeCardItemText(context, attrs, defStyleAttr, text) {
    override val selectedPadding = 28
    override val unselectedPadding = 12

    init {
        setLineSpacing(2f, 1f)
    }
}