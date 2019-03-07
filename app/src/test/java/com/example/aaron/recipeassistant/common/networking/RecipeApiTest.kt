package com.example.aaron.recipeassistant.common.networking

import com.example.aaron.recipeassistant.common.mapper.Mapper
import com.example.aaron.recipeassistant.common.model.Recipe
import com.example.aaron.recipeassistant.common.model.RecipeListDTO
import com.example.aaron.recipeassistant.test.data.RecipeDataFactory
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import org.amshove.kluent.*
import org.junit.Before
import org.junit.Test

class RecipeApiTest {

    private lateinit var mockService: RecipeService
    private lateinit var mockMapper: Mapper<RecipeListDTO, List<Recipe>>
    private lateinit var mockNetworkHelper: NetworkHelper

    private lateinit var listDTO: RecipeListDTO
    private lateinit var recipeList: List<Recipe>

    private lateinit var api: RecipeApi

    @Before
    fun setup() {
        listDTO = RecipeDataFactory.randomRecipeListDTO()
        recipeList = RecipeDataFactory.randomRecipeList()

        mockService = mock()
        mockMapper = mock()
        mockNetworkHelper = mock()

        When calling mockNetworkHelper.observeNetworkConnectivity() itReturns Flowable.just(true)

        When calling mockService.randomSelection() itReturns Single.just(listDTO)
        When calling mockMapper.toModel(listDTO) itReturns recipeList

        api = RecipeApi(mockService, mockMapper, mockNetworkHelper)
    }

    @Test
    fun `when getRandomRecipes is subscribed to it calls randomSelection on the service`() {
        api.getRandomRecipes().test()

        Verify on mockService that mockService.randomSelection() was called
    }

    @Test
    fun `when getRandomRecipes is subscribed to, it calls toModel on the mapper with the dto returned by the service`() {
        api.getRandomRecipes().test()

        Verify on mockMapper that mockMapper.toModel(listDTO) was called
    }

    @Test
    fun `when getRandomRecipes is subscribed to, it returns the RecipeListDTO returned by the mapper, wrapped in a RecipeApiResponse`() {
        val testSubscriber = api.getRandomRecipes().test()

        testSubscriber.assertValue(RecipeApiResponse(recipeList))
    }

    @Test
    fun `if there is an error in the service it returns an error single`() {
        When calling mockService.randomSelection() itReturns Single.error(Throwable())

        val testSubscriber = api.getRandomRecipes().test()

        testSubscriber.assertError(Throwable::class.java)
    }

    @Test
    fun `if the mapper throws an exception it returns an error single`() {
        When calling mockMapper.toModel(listDTO) itThrows RuntimeException()

        val testSubscriber = api.getRandomRecipes().test()

        testSubscriber.assertError(Throwable::class.java)
    }

    @Test
    fun `when there is no network connectivity, it does not make an api call`() {
        When calling mockNetworkHelper.observeNetworkConnectivity() itReturns Flowable.just(false)

        api.getRandomRecipes().test()

        verifyZeroInteractions(mockService)
    }

    @Test
    fun `when the network connectivity changes from false to true, it makes an api call`() {
        val subject = BehaviorSubject.create<Boolean>()
        subject.onNext(false)

        When calling mockNetworkHelper
            .observeNetworkConnectivity() itReturns subject
            .toFlowable(BackpressureStrategy.DROP)

        api.getRandomRecipes().test()

        verifyZeroInteractions(mockService)

        subject.onNext(true)

        verify(mockService).randomSelection()
    }
}
