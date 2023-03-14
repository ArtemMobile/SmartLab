package com.example.smartlab.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.smartlab.R
import com.example.smartlab.app.App
import com.example.smartlab.model.dto.OnboardingItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.mockito.kotlin.mock

internal class OnboardingViewModelTest{
    @OptIn(ExperimentalCoroutinesApi::class)
    class MainDispatcherRule (
        val dispatcher: TestDispatcher = StandardTestDispatcher()
    ): TestWatcher() {
        override fun starting(description: Description) {
            Dispatchers.setMain(dispatcher)
        }

        override fun finished(description: Description) {
            Dispatchers.resetMain()
        }
    }
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val application = mock<App>()

    private lateinit var viewModel : OnboardingViewModel

    @Before
    fun initViewModel(){
        viewModel = OnboardingViewModel(application)
    }

    @Test
    fun `correctly extracting elements from the queue`() {
        val firstElement = OnboardingItem("Анализы", "Экспресс сбор и получение проб", R.drawable.onboarding_01)
        val secondElement = OnboardingItem("Уведомления", "Вы быстро узнаете о результатах", R.drawable.onboarding_02)
        val thirdElement = OnboardingItem("Мониторинг", "Наши врачи всегда наблюдают\nза вашими показателями здоровья", R.drawable.onboarding_03)

        assertEquals(firstElement, viewModel.nextPage())
        assertEquals(secondElement, viewModel.nextPage())
        assertEquals(thirdElement, viewModel.nextPage())
    }

    @Test
    fun `should queue size decrease when nextPage() called`() {
        assertEquals(3, viewModel.onboardingItems.size)
        viewModel.nextPage()
        assertEquals(2, viewModel.onboardingItems.size)
        viewModel.nextPage()
        assertEquals(1, viewModel.onboardingItems.size)
        viewModel.nextPage()
        assertEquals(0, viewModel.onboardingItems.size)
    }

    @Test
    fun `should change button text when last page`() {
        viewModel.nextPage()
        viewModel.nextPage()
        viewModel.nextPage()
        assertEquals("Завершить", viewModel.buttonText.value)
    }

    @Test
    fun `should navigated to login screen when clicked navigate button`() {
        viewModel.navigateToLoginScreen()
        assertEquals(viewModel.isNavigatedToLoginScreen, true)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `should call onboarding passed`() = runTest {
        viewModel.navigateToLoginScreen()
        assertEquals(viewModel.onboardingPassedCallCount, 1)
    }
}