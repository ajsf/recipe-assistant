package com.example.aaron.recipeassistant;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aaron.recipeassistant.Model.Meal;
import com.example.aaron.recipeassistant.Model.MealList;
import com.squareup.picasso.Picasso;

public class RecipeRecyclerViewAdapter extends RecyclerView.Adapter<RecipeRecyclerViewAdapter.RecipeViewHolder> {

    private Context context;
    private MealList mealList;
    private int imageSize;
    private Picasso picasso;

    public RecipeRecyclerViewAdapter(@NonNull Activity activity) {
        this.context = (Context) activity;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        imageSize = (displayMetrics.widthPixels / 2);
        picasso = Picasso.with(context);
        picasso.setIndicatorsEnabled(true);
    }

    void swapMealList(MealList newMealList) {
        mealList = newMealList;
        notifyDataSetChanged();
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recipe_card_view, parent, false);
        view.setFocusable(true);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        Meal meal = mealList.getMeals().get(position);
        String mealName = meal.getStrMeal();
        mealName = mealName.toUpperCase();
        mealName = mealName.replace(" ", "\n");
        //holder.recipeName.setText(mealName);
        picasso.load(meal.getStrMealThumb()).noFade().resize(imageSize, imageSize).centerCrop()
                .into(holder.recipePhoto);

    }

    @Override
    public int getItemCount() {
        if (null == mealList) return 0;
        return mealList.getMeals().size();
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView recipePhoto;
        private TextView recipeName;

        public RecipeViewHolder(View itemView) {
            super(itemView);

            recipePhoto = (ImageView) itemView.findViewById(R.id.recipe_photo);
            //recipeName = (TextView) itemView.findViewById(R.id.recipe_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
