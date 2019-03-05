package com.example.aaron.recipeassistant.readrecipe.view

import android.content.Context
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.view.View
import com.example.aaron.recipeassistant.R
import kotlinx.android.synthetic.main.recipe_card.view.*

private typealias Action = () -> Unit
private typealias ClickListener = (Int) -> Unit

abstract class RecipeCard
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    private var selectedItemIndex = 0
    private var itemsTextViewList: List<RecipeCardItemText> = listOf()

    protected abstract val labelText: String

    protected abstract fun buildTextView(string: String): RecipeCardItemText

    fun setSelectedItem(itemIndex: Int) {
        itemsTextViewList[selectedItemIndex].unselectItem()
        itemsTextViewList[itemIndex].selectItem()
        selectedItemIndex = itemIndex
    }

    fun initButtons(readAction: Action, nextAction: Action, prevAction: Action) {
        btn_play.setOnClickListener { readAction.invoke() }
        btn_next.setOnClickListener { nextAction.invoke() }
        btn_prev.setOnClickListener { prevAction.invoke() }
    }

    fun displayItems(items: List<String>, clickListener: (Int) -> Unit) {
        radius = context.resources.getDimension(R.dimen.read_recipe_card_corner_radius)
        View.inflate(context, R.layout.recipe_card, this)
        tv_label.text = labelText
        buildTextViews(items, clickListener)
        itemsTextViewList.forEach { details_layout.addView(it) }
    }

    fun setPlaying(isPlaying: Boolean) {
        if (isPlaying) {
            btn_play.setImageResource(R.drawable.ic_stop_black_24dp)
        } else {
            btn_play.setImageResource(R.drawable.ic_play_arrow_black_24dp)
        }
    }

    private fun buildTextViews(items: List<String>, clickListener: ClickListener) {
        itemsTextViewList = items.mapIndexed { index, string ->
            buildTextView(string).apply {
                selected.value = false
                setOnClickListener { clickListener(index) }
            }
        }
    }
}