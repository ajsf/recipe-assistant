package com.example.aaron.recipeassistant.readrecipe

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.aaron.recipeassistant.model.Recipe
import org.amshove.kluent.`should equal`
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ReadRecipeViewModelTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private lateinit var vm: ReadRecipeViewModel

    @Before
    fun setup() {
        vm = ReadRecipeViewModel()
    }

    @Test
    fun `currentIngredientIndex can't be greater than ingredients list last index`() {
        //given
        vm.recipe.value = testRecipe
        //when
        for (i in 0..testRecipe.ingredients.size + 5) {
            vm.nextIngredient()
        }
        //then
        vm.currentIngredientIndex.value `should equal` testRecipe.ingredients.lastIndex
    }

    @Test
    fun `currentIngredientIndex can't be less than zero`() {
        //given
        vm.recipe.value = testRecipe
        //when
        for (i in 0..5) {
            vm.prevIngredient()
        }
        //then
        vm.currentIngredientIndex.value `should equal` 0
    }

    @Test
    fun `currentDirectionIndex can't be greater than directions list last index`() {
        //given
        vm.recipe.value = testRecipe
        //when
        for (i in 0..testRecipe.directions.size + 5) {
            vm.nextDirection()
        }
        //then
        vm.currentDirectionIndex.value `should equal` testRecipe.directions.lastIndex
    }

    @Test
    fun `currentDirectionIndex can't be less than zero`() {
        //given
        vm.recipe.value = testRecipe
        //when
        for (i in 0..5) {
            vm.prevDirection()
        }
        //then
        vm.currentDirectionIndex.value `should equal` 0
    }


}

private val testRecipe = Recipe("Title",
        listOf("1", "2", "3"),
        listOf("a", "b", "c", "d", "e"),
        "abc"
)