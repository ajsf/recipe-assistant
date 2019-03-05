package com.example.aaron.recipeassistant.readrecipe.view

import android.content.Context
import android.util.AttributeSet
import com.example.aaron.recipeassistant.R

class DirectionsCard
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecipeCard(context, attrs, defStyleAttr) {

    override val labelText: String = context.getString(R.string.directions_label)

    override fun buildTextView(string: String): RecipeCardItemText =
        DirectionCardItemText(context, text = string)
}