package com.example.aaron.recipeassistant.readrecipe.view

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.view.View
import com.example.aaron.recipeassistant.R
import kotlinx.android.synthetic.main.recipe_card.view.*

private typealias Action = () -> Unit

class RecipeCard
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    private val selectedItemIndex: MutableLiveData<Int> = MutableLiveData()
    private var previousIndex = 0

    private var itemsTextViewList = listOf<RecipeCardItemText>()

    init {
        View.inflate(context, R.layout.recipe_card, this)
        radius = context.resources.getDimension(R.dimen.read_recipe_card_corner_radius)
        selectedItemIndex.observe(context as AppCompatActivity, Observer {
            it?.let { newIndex ->
                itemsTextViewList[previousIndex].unselectItem()
                itemsTextViewList[newIndex].selectItem()
                previousIndex = newIndex
            }
        })
    }

    fun setSelectedItem(itemIndex: Int) {
        selectedItemIndex.postValue(itemIndex)
    }

    fun initButtons(readAction: Action, nextAction: Action, prevAction: Action) {
        btn_play.setOnClickListener { readAction.invoke() }
        btn_next.setOnClickListener { nextAction.invoke() }
        btn_prev.setOnClickListener { prevAction.invoke() }
    }

    fun displayItems(items: List<String>, label: String, clickListener: (Int) -> Unit) {
        itemsTextViewList = buildTextViews(items, clickListener)
        itemsTextViewList.forEach { details_layout.addView(it) }
        tv_label.text = label
    }

    fun setPlaying(isPlaying: Boolean) {
        if (isPlaying) {
            btn_play.setImageResource(R.drawable.ic_stop_black_24dp)
        } else {
            btn_play.setImageResource(R.drawable.ic_play_arrow_black_24dp)
        }
    }

    private fun buildTextViews(
        items: List<String>,
        clickListener: (Int) -> Unit
    ): List<RecipeCardItemText> =
        items.mapIndexed { index, string ->
            IngredientCardItemText(context, text = string).apply {
                selected.value = false
                setOnClickListener { clickListener.invoke(index) }
            }
        }
}