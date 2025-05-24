//package com.practice.autocare.MainActivitiesTests
//
//import androidx.arch.core.executor.testing.InstantTaskExecutorRule
//import androidx.fragment.app.testing.launchFragmentInContainer
//import androidx.lifecycle.Lifecycle
//import androidx.test.core.app.ApplicationProvider
//import androidx.test.espresso.Espresso.onView
//import androidx.test.espresso.assertion.ViewAssertions.matches
//import androidx.test.espresso.matcher.ViewMatchers.*
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import com.practice.autocare.R
//import com.practice.autocare.activities.main.HistoryFragment
//import com.practice.autocare.api.Api
//import com.practice.autocare.api.RetrofitClient
//import com.practice.autocare.models.service.ServiceEventResponse
//import com.practice.autocare.util.Constants
//import kotlinx.coroutines.runBlocking
//import okhttp3.MediaType
//import okhttp3.MediaType.Companion.toMediaType
//import okhttp3.ResponseBody
//import org.hamcrest.CoreMatchers.not
//import org.junit.After
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.mockito.Mockito.`when`
//import org.mockito.Mockito.mock
//import retrofit2.Response
//import retrofit2.http.GET
//import retrofit2.http.Query
//
//// Сначала определим интерфейс Api для тестов
//interface TestApi : Api {
//    @GET("service-events")
//    override suspend fun getServiceEvents(
//        @Query("email") email: String
//    ): Response<ArrayList<ServiceEventResponse>>
//}
//
//@RunWith(AndroidJUnit4::class)
//class HistoryFragmentTest {
//
//    @get:Rule
//    val instantTaskExecutorRule = InstantTaskExecutorRule()
//
//    private lateinit var originalApi: Any
//
//    @Before
//    fun setUp() {
//        // Сохраняем оригинальный api
//        originalApi = RetrofitClient.api
//
//        // Сохраняем тестовый email в SharedPreferences
//        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
//        Constants.saveUserEmail(context, "test@example.com")
//    }
//
//    @After
//    fun tearDown() {
//        // Восстанавливаем оригинальный api
//        RetrofitClient.api = originalApi as Api?
//
//        // Очищаем SharedPreferences после теста
//        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
//        context.getSharedPreferences(Constants.PREFS_NAME, android.content.Context.MODE_PRIVATE)
//            .edit()
//            .clear()
//            .apply()
//    }
//
//    @Test
//    fun testEmptyStateShownWhenNoServices() = runBlocking {
//        // Given
//        val mockApi = mock(TestApi::class.java)
//        `when`(mockApi.getServiceEvents("test@example.com")).thenReturn(
//            Response.success(ArrayList()) // Используем ArrayList вместо emptyList()
//        )
//        RetrofitClient.api = mockApi
//
//        // When
//        val scenario = launchFragmentInContainer<HistoryFragment>()
//        scenario.moveToState(Lifecycle.State.RESUMED)
//
//        // Даем время для обновления UI
//        Thread.sleep(1000)
//
//        // Then
//        onView(withId(R.id.emptyText)).check(matches(isDisplayed()))
//        onView(withId(R.id.rViewHistory)).check(matches(not(isDisplayed())))
//    }
//
//    @Test
//    fun testServicesListShownWhenDataAvailable() = runBlocking {
//        // Given
//        val testServices = arrayListOf( // Используем arrayListOf вместо listOf
//            ServiceEventResponse(
//                car_id = 1,
//                service_type = "Oil Change",
//                due_date = "2023-01-15",
//                due_mileage = 5000.0,
//                event_id = 1
//            )
//        )
//
//        val mockApi = mock(TestApi::class.java)
//        `when`(mockApi.getServiceEvents("test@example.com")).thenReturn(
//            Response.success(testServices)
//        )
//        RetrofitClient.api = mockApi
//
//        // When
//        val scenario = launchFragmentInContainer<HistoryFragment>()
//        scenario.moveToState(Lifecycle.State.RESUMED)
//
//        // Даем время для обновления UI
//        Thread.sleep(1000)
//
//        // Then
//        onView(withId(R.id.rViewHistory)).check(matches(isDisplayed()))
//        onView(withId(R.id.emptyText)).check(matches(not(isDisplayed())))
//    }
//
//    @Test
//    fun testErrorHandlingWhenApiFails() = runBlocking {
//        // Given
//        val mockApi = mock(TestApi::class.java)
//        `when`(mockApi.getServiceEvents("test@example.com")).thenReturn(
//            Response.error(400,
//                ResponseBody.create(
//                    "application/json".toMediaType(), // Используем toMediaType() вместо MediaType.get()
//                    "Error"
//                )
//            )
//        )
//        RetrofitClient.api = mockApi
//    }
//
//    @Test
//    fun testNetworkErrorHandling() = runBlocking {
//        // Given
//        val mockApi = mock(TestApi::class.java)
//        `when`(mockApi.getServiceEvents("test@example.com")).thenThrow(
//            java.net.ConnectException("No internet")
//        )
//        RetrofitClient.api = mockApi
//
//        // When
//        val scenario = launchFragmentInContainer<HistoryFragment>()
//        scenario.moveToState(Lifecycle.State.RESUMED)
//
//        // Даем время для обновления UI
//        Thread.sleep(1000)
//
//        // Then
//        onView(withId(R.id.emptyText)).check(matches(isDisplayed()))
//    }
//}