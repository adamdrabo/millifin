package com.adamdrabo.millifin.content.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adamdrabo.millifin.content.data.ContentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContentSyncViewModel @Inject constructor(
    private val contentRepository: ContentRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            contentRepository.loadBundledContentIfNewer()
            contentRepository.refresh()
        }
    }
}
