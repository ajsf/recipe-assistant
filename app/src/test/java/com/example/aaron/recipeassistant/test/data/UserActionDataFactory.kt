package com.example.aaron.recipeassistant.test.data

import com.example.aaron.recipeassistant.readrecipe.model.*


object UserActionDataFactory {

    fun randomUserActionList(): List<UserAction> = TestDataFactory.randomList(::randomUserAction)

    fun randomUserAction(): UserAction = when (TestDataFactory.randomInt(6)) {
        0 -> PlayIngredient
        1 -> NextIngredient
        2 -> PrevIngredient
        3 -> PlayDirection
        4 -> NextDirection
        5 -> NextIngredient
        else -> StopReading
    }

}