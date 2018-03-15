package com.example.aaron.recipeassistant.browserecipes.ui

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
import com.example.aaron.recipeassistant.model.Recipe
import com.example.aaron.recipeassistant.readrecipe.ReadRecipeActivity
import com.squareup.picasso.Picasso

class RecipeRecyclerViewAdapter internal constructor(activity: Activity, columnCount: Int) : RecyclerView.Adapter<RecipeRecyclerViewAdapter.RecipeViewHolder>() {

    private val context: Context
    private val activity: Activity
    private var recipeList: List<Recipe> = listOf()
    private val imageSize: Int
    private val picasso: Picasso

    init {
        this.context = activity
        this.activity = activity

        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        imageSize = displayMetrics.widthPixels / columnCount
        picasso = Picasso.with(context)
    }

    internal fun swapMealList(newRecipeList: List<Recipe>) {
        recipeList = newRecipeList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recipe_list_item, parent, false)
        view.isFocusable = true
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipeList[position]
        holder.recipe = recipe
        val mealName = recipe.title.trim { it <= ' ' }.toUpperCase().replace(" ", "\n")
        holder.recipeName.text = mealName
        picasso.load(recipe.imageUrl)
                .into(holder.recipePhoto)
    }

    override fun getItemCount() = recipeList.size

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

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
