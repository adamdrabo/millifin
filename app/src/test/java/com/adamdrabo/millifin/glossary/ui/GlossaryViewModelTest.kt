package com.adamdrabo.millifin.glossary.ui

import com.adamdrabo.millifin.content.data.ContentRepository
import com.adamdrabo.millifin.content.data.FakeContentApi
import com.adamdrabo.millifin.content.data.FakeContentDao
import com.adamdrabo.millifin.content.data.FakeSeedReader
import com.adamdrabo.millifin.content.data.FakeVersionStore
import com.adamdrabo.millifin.content.data.GlossaryTermEntity
import com.adamdrabo.millifin.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GlossaryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val contentDao = FakeContentDao().apply {
        glossary.value = listOf(
            GlossaryTermEntity("Gharar", "Incertitude excessive dans un contrat."),
            GlossaryTermEntity("Riba", "Surplus exigé dans un prêt."),
            GlossaryTermEntity("Zakat", "Aumône obligatoire au-delà du nisab.")
        )
    }

    private fun createViewModel() = GlossaryViewModel(
        ContentRepository(contentDao, FakeContentApi(), FakeSeedReader(), FakeVersionStore())
    )

    @Test
    fun allTermsAreShownWithoutQuery() = runTest {
        val viewModel = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        assertEquals(3, viewModel.uiState.value.terms.size)
    }

    @Test
    fun searchIgnoresCaseAndAccents() = runTest {
        val viewModel = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        viewModel.onQueryChange("RIBA")
        assertEquals(listOf("Riba"), viewModel.uiState.value.terms.map { it.term })

        viewModel.onQueryChange("aumone")
        assertEquals(listOf("Zakat"), viewModel.uiState.value.terms.map { it.term })
    }
}
