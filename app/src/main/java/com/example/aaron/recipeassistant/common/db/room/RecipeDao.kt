package com.example.aaron.recipeassistant.common.db.room

import android.arch.paging.DataSource
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Single

@Dao
interface RecipeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(recipes: List<RecipeEntity>)

    @Query("SELECT * FROM recipe")
    fun getAllRecipes(): DataSource.Factory<Int, RecipeEntity>

    @Query("SELECT * FROM recipe WHERE id = :idString")
    fun getRecipeById(idString: String): Single<RecipeEntity>
}