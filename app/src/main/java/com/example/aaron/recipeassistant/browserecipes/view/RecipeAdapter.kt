package com.example.aaron.recipeassistant.browserecipes.view

import android.arch.paging.PagedListAdapter
import android.content.Context
import android.content.Intent
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.aaron.recipeassistant.R
import com.example.aaron.recipeassistant.common.model.Recipe
import com.example.aaron.recipeassistant.readrecipe.view.RECIPE_ID_EXTRA
import com.example.aaron.recipeassistant.readrecipe.view.ReadRecipeActivity
import com.squareup.picasso.Picasso


class RecipeAdapter(private val activity: BrowseRecipesActivity) :
    PagedListAdapter<Recipe, RecipeAdapter.RecipeViewHolder>(RecipeDiffCallback) {

    private val context: Context = activity
    private val picasso: Picasso = Picasso.with(context)

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return currentList?.get(position).hashCode().toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.recipe_list_item, parent, false)
        view.isFocusable = true
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        getItem(position)?.let { recipe ->
            val mealName = recipe.title.toUpperCase().replace(" ", "\n")
            holder.recipe = recipe
            holder.recipeName.text = mealName
            picasso.load(recipe.imageUrl)
                .into(holder.recipePhoto)
        }
    }

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        val recipePhoto: ImageView = itemView.findViewById(R.id.recipe_photo)
        val recipeName: TextView = itemView.findViewById(R.id.recipe_name)
        lateinit var recipe: Recipe

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            val intent = Intent(context, ReadRecipeActivity::class.java)
            intent.putExtra(RECIPE_ID_EXTRA, recipe.id)
            val transImageName = context.getString(R.string.trans_img)
            val transImage = Pair.create(recipePhoto as View, transImageName)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transImage)
            activity.returnRecipeId = recipe.id
            activity.startActivity(intent, options.toBundle())
        }
    }

    companion object {
        val RecipeDiffCallback: DiffUtil.ItemCallback<Recipe> =
            object : DiffUtil.ItemCallback<Recipe>() {
                override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
