package com.adamdrabo.millifin.content.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adamdrabo.millifin.R
import com.adamdrabo.millifin.content.model.Lesson
import com.adamdrabo.millifin.content.model.LessonContent
import com.adamdrabo.millifin.content.model.Section
import com.adamdrabo.millifin.content.model.Source
import com.adamdrabo.millifin.core.theme.MillifinTheme
import com.adamdrabo.millifin.core.ui.MillifinTopBar
import com.adamdrabo.millifin.core.ui.PrimaryButton
import com.adamdrabo.millifin.core.ui.SecondaryButton

@Composable
fun LessonScreen(
    onBack: () -> Unit,
    onStartQuiz: (String) -> Unit,
    viewModel: LessonViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LessonContentScreen(
        uiState = uiState,
        onBack = onBack,
        onNext = viewModel::nextPage,
        onPrevious = viewModel::previousPage,
        onStartQuiz = { onStartQuiz(viewModel.lessonId) }
    )
}

@Composable
fun LessonContentScreen(
    uiState: LessonUiState,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onStartQuiz: () -> Unit
) {
    val title = (uiState as? LessonUiState.Reading)?.content?.lesson?.title.orEmpty()
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = { MillifinTopBar(title = title, onBack = onBack) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (uiState) {
                LessonUiState.Loading -> CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.Center)
                )

                LessonUiState.NotFound -> Text(
                    text = stringResource(R.string.lesson_not_found),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Center)
                )

                is LessonUiState.Reading -> ReadingContent(
                    state = uiState,
                    onNext = onNext,
                    onPrevious = onPrevious,
                    onStartQuiz = onStartQuiz
                )
            }
        }
    }
}

@Composable
private fun ReadingContent(
    state: LessonUiState.Reading,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onStartQuiz: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        LinearProgressIndicator(
            progress = { (state.pageIndex + 1).toFloat() / state.pageCount },
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = stringResource(R.string.lesson_page, state.pageIndex + 1, state.pageCount),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (state.content.lesson.isSensitive && state.pageIndex == 0) {
                Disclaimer()
            }
            if (state.isSummaryPage) {
                SummaryPage(content = state.content)
            } else {
                SectionPage(section = state.content.sections[state.pageIndex])
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (state.isSummaryPage) {
                PrimaryButton(
                    text = stringResource(R.string.lesson_start_quiz),
                    onClick = onStartQuiz
                )
            } else {
                PrimaryButton(text = stringResource(R.string.lesson_next), onClick = onNext)
            }
            if (state.pageIndex > 0) {
                SecondaryButton(text = stringResource(R.string.lesson_previous), onClick = onPrevious)
            }
        }
    }
}

@Composable
private fun SectionPage(section: Section) {
    Text(
        text = section.title,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
    Text(
        text = section.body,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun SummaryPage(content: LessonContent) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.lesson_key_takeaway),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = content.keyTakeaway,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
    if (content.sources.isNotEmpty()) {
        Text(
            text = stringResource(R.string.lesson_sources),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        content.sources.forEach { source -> SourceLink(source) }
    }
}

@Composable
private fun SourceLink(source: Source) {
    val uriHandler = LocalUriHandler.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { uriHandler.openUri(source.url) }
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = source.label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            textDecoration = TextDecoration.Underline
        )
    }
}

@Composable
private fun Disclaimer() {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.disclaimer),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(12.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LessonContentScreenPreview() {
    MillifinTheme {
        LessonContentScreen(
            uiState = LessonUiState.Reading(
                content = LessonContent(
                    lesson = Lesson("riba", 3, "Le riba", 5, true),
                    sections = listOf(Section("Définition", "Le riba désigne un surplus exigé dans un prêt.")),
                    keyTakeaway = "Un prêt ne doit pas rapporter d'intérêt.",
                    sources = listOf(Source("Source", "https://example.com"))
                ),
                pageIndex = 0
            ),
            onBack = {},
            onNext = {},
            onPrevious = {},
            onStartQuiz = {}
        )
    }
}
