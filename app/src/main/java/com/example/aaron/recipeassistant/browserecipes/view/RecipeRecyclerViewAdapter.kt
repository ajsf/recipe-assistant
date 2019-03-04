package com.example.aaron.recipeassistant.browserecipes.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.aaron.recipeassistant.R
import com.example.aaron.recipeassistant.common.model.Recipe
import com.example.aaron.recipeassistant.readrecipe.view.ReadRecipeActivity
import com.squareup.picasso.Picasso

class RecipeRecyclerViewAdapter(private val activity: Activity, columnCount: Int) :
    RecyclerView.Adapter<RecipeRecyclerViewAdapter.RecipeViewHolder>() {

    private val context: Context = activity
    private var recipeList: List<Recipe> = listOf()
    private val picasso: Picasso = Picasso.with(context)
    private val imageSize: Int

    init {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        imageSize = displayMetrics.widthPixels / columnCount
    }

    internal fun swapMealList(newRecipeList: List<Recipe>) {
        recipeList = newRecipeList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.recipe_list_item, parent, false)
        view.isFocusable = true
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipeList[position]
        val mealName = recipe.title.toUpperCase().replace(" ", "\n")
        holder.recipe = recipe
        holder.recipeName.text = mealName
        picasso.load(recipe.imageUrl)
            .into(holder.recipePhoto)
    }

    override fun getItemCount() = recipeList.size

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        val recipePhoto: ImageView = itemView.findViewById(R.id.recipe_photo)
        val recipeName: TextView = itemView.findViewById(R.id.recipe_name)
        lateinit var recipe: Recipe

        init {
            val params = RelativeLayout.LayoutParams(imageSize, imageSize)
            recipeName.layoutParams = params
            recipePhoto.layoutParams = params
            itemView.setOnClickListener(this)
            activity.startPostponedEnterTransition()
        }

        override fun onClick(v: View) {
            val intent = Intent(context, ReadRecipeActivity::class.java)
            intent.putExtra("recipe", recipe)
            val transImageName = context.getString(R.string.trans_img)
            val transImage = Pair.create(recipePhoto as View, transImageName)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transImage)
            activity.startActivity(intent, options.toBundle())
        }
    }
}
