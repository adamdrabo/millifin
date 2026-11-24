package com.adamdrabo.millifin.glossary.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adamdrabo.millifin.content.data.ContentRepository
import com.adamdrabo.millifin.content.model.GlossaryTerm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.text.Normalizer
import javax.inject.Inject

data class GlossaryUiState(
    val query: String = "",
    val terms: List<GlossaryTerm> = emptyList()
)

@HiltViewModel
class GlossaryViewModel @Inject constructor(
    contentRepository: ContentRepository
) : ViewModel() {

    private val query = MutableStateFlow("")

    val uiState: StateFlow<GlossaryUiState> = combine(
        contentRepository.observeGlossary(),
        query
    ) { terms, currentQuery ->
        GlossaryUiState(
            query = currentQuery,
            terms = terms.filter { it.matches(currentQuery) }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = GlossaryUiState()
    )

    fun onQueryChange(newQuery: String) {
        query.value = newQuery
    }

    private fun GlossaryTerm.matches(query: String): Boolean {
        val needle = query.trim().simplified()
        if (needle.isEmpty()) return true
        return term.simplified().contains(needle) || definition.simplified().contains(needle)
    }

    private fun String.simplified(): String =
        Normalizer.normalize(this, Normalizer.Form.NFD)
            .replace(DIACRITICS, "")
            .lowercase()

    private companion object {
        val DIACRITICS = Regex("\\p{Mn}+")
    }
}
