package com.example.aaron.recipeassistant.common.model

sealed class UserAction

object FirstIngredient : UserAction()
object FinalIngredient : UserAction()

object PrevIngredient : UserAction()
object NextIngredient : UserAction()

object PlayIngredient : UserAction()

object FirstDirection : UserAction()
object FinalDirection : UserAction()

object PrevDirection : UserAction()
object NextDirection : UserAction()

object PlayDirection : UserAction()

object StopReading : UserAction()

object Listen : UserAction()

object StopListening : UserAction()

object ToggleListener : UserAction()

data class SetIngredient(val index: Int) : UserAction()

data class SetDirection(val index: Int) : UserAction()