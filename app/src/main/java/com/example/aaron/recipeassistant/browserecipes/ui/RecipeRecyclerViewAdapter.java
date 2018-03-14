package com.example.aaron.recipeassistant.browserecipes.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.aaron.recipeassistant.model.RecipeDTO;
import com.example.aaron.recipeassistant.model.RecipeList;
import com.example.aaron.recipeassistant.model.RecipeMapper;
import com.example.aaron.recipeassistant.model.Recipe;
import com.example.aaron.recipeassistant.R;
import com.example.aaron.recipeassistant.readrecipe.ReadRecipeActivity;
import com.squareup.picasso.Picasso;

public class RecipeRecyclerViewAdapter extends RecyclerView.Adapter<RecipeRecyclerViewAdapter.RecipeViewHolder> {

    private Context context;
    private Activity activity;
    private RecipeList recipeList;
    private int imageSize;
    private Picasso picasso;

    public RecipeRecyclerViewAdapter(@NonNull Activity activity, int columnCount) {
        this.context = activity;
        this.activity = activity;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        imageSize = (displayMetrics.widthPixels / columnCount);
        picasso = Picasso.with(context);
        //picasso.setIndicatorsEnabled(true);
    }

    void swapMealList(RecipeList newRecipeList) {
        recipeList = newRecipeList;
        notifyDataSetChanged();
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recipe_list_item, parent, false);
        view.setFocusable(true);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        RecipeDTO meal = recipeList.getMeals().get(position);
        holder.meal = meal;
        String mealName = meal.getStrMeal().trim().toUpperCase().replace(" ", "\n");
        holder.recipeName.setText(mealName);
        picasso.load(meal.getStrMealThumb())
                .into(holder.recipePhoto);


    }

    @Override
    public int getItemCount() {
        if (null == recipeList) return 0;
        return recipeList.getMeals().size();
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView recipePhoto;
        private final TextView recipeName;
        private RecipeDTO meal;

        public RecipeViewHolder(View itemView) {
            super(itemView);

            recipePhoto = (ImageView) itemView.findViewById(R.id.recipe_photo);
            recipeName = (TextView) itemView.findViewById(R.id.recipe_name);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(imageSize, imageSize);
            recipeName.setLayoutParams(params);
            recipePhoto.setLayoutParams(params);
            itemView.setOnClickListener(this);
            activity.startPostponedEnterTransition();
        }

        @Override
        public void onClick(View v) {
            RecipeMapper recipeMapper = new RecipeMapper(meal);
            Recipe recipe = recipeMapper.getRecipe();

            Intent intent = new Intent(context, ReadRecipeActivity.class);
            intent.putExtra("recipe", recipe);

            String transImageName = context.getString(R.string.trans_img);
            String transTitleName = context.getString(R.string.trans_title);

            Pair<View, String> transImage = Pair.create((View)recipePhoto, transImageName);
            //Pair<View, String> transTitle = Pair.create((View) recipeName, transTitleName);

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transImage);

            activity.startActivity(intent, options.toBundle());
        }
    }
}
