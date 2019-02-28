package com.example.aaron.recipeassistant.readrecipe

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.aaron.recipeassistant.common.model.Recipe
import com.example.aaron.recipeassistant.common.readerservice.RecipeReader
import com.example.aaron.recipeassistant.readrecipe.viewmodel.ReadRecipeViewModel
import org.amshove.kluent.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ReadRecipeViewModelTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private lateinit var vm: ReadRecipeViewModel
    private lateinit var mockReader: RecipeReader

    @Before
    fun setup() {
        vm = ReadRecipeViewModel()
        mockReader = mock(RecipeReader::class)
        vm.recipeReader = mockReader
    }

    @Test
    fun `when first created the first ingredient is selected and read when called`() {
        //given
        vm.recipe.value = testRecipe
        //when
        vm.readIngredient()
        //then
        Verify on mockReader that mockReader.read(testRecipe.ingredients.first())
    }

    @Test
    fun `when first created the first direction is selected and read when called`() {
        //given
        vm.recipe.value = testRecipe
        //when
        vm.readDirection()
        //then
        Verify on mockReader that mockReader.read(testRecipe.directions.first())
    }

    @Test
    fun `next ingredient button works`() {
        //given
        vm.recipe.value = testRecipe
        //when
        vm.nextIngredient()
        vm.nextIngredient()
        vm.readIngredient()
        //then
        Verify on mockReader that mockReader.read(testRecipe.ingredients[2])
    }

    @Test
    fun `previous ingredient button works`() {
        //given
        vm.recipe.value = testRecipe
        //when
        vm.nextIngredient()
        vm.nextIngredient()
        vm.prevIngredient()
        vm.readIngredient()
        //then
        Verify on mockReader that mockReader.read(testRecipe.ingredients[1])
    }

    @Test
    fun `next direction button works`() {
        //given
        vm.recipe.value = testRecipe
        //when
        vm.nextDirection()
        vm.nextDirection()
        vm.readDirection()
        //then
        Verify on mockReader that mockReader.read(testRecipe.directions[2])
    }

    @Test
    fun `previous direction button works`() {
        //given
        vm.recipe.value = testRecipe
        //when
        vm.nextDirection()
        vm.nextDirection()
        vm.prevDirection()
        vm.readDirection()
        //then
        Verify on mockReader that mockReader.read(testRecipe.directions[1])
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