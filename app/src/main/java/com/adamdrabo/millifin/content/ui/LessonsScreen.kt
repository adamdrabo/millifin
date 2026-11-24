package com.adamdrabo.millifin.content.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adamdrabo.millifin.R
import com.adamdrabo.millifin.content.model.Lesson
import com.adamdrabo.millifin.core.theme.LocalAnswerColors
import com.adamdrabo.millifin.core.theme.MillifinTheme
import com.adamdrabo.millifin.progress.model.LessonStatus

@Composable
fun LessonsScreen(
    onOpenLesson: (String) -> Unit,
    viewModel: LessonsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LessonsContent(uiState = uiState, onOpenLesson = onOpenLesson)
}

@Composable
fun LessonsContent(
    uiState: LessonsUiState,
    onOpenLesson: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        LessonsUiState.Loading -> Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }

        is LessonsUiState.Ready -> LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.lessons_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            items(uiState.items, key = { it.lesson.id }) { item ->
                LessonCard(item = item, onClick = { onOpenLesson(item.lesson.id) })
            }
        }
    }
}

@Composable
private fun LessonCard(item: LessonItem, onClick: () -> Unit) {
    val lesson = item.lesson
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = lesson.position.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.lesson_reading_time, lesson.readingMinutes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            StatusChip(status = item.status)
        }
    }
}

@Composable
private fun StatusChip(status: LessonStatus) {
    val answerColors = LocalAnswerColors.current
    val (label, background, content) = when (status) {
        LessonStatus.TO_DO -> Triple(
            R.string.status_to_do,
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.onSurfaceVariant
        )
        LessonStatus.IN_PROGRESS -> Triple(
            R.string.status_in_progress,
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.primary
        )
        LessonStatus.DONE -> Triple(
            R.string.status_done,
            answerColors.successContainer,
            answerColors.success
        )
    }
    Surface(shape = RoundedCornerShape(50), color = background) {
        Text(
            text = stringResource(label),
            style = MaterialTheme.typography.labelMedium,
            color = content,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LessonsContentPreview() {
    MillifinTheme {
        LessonsContent(
            uiState = LessonsUiState.Ready(
                listOf(
                    LessonItem(Lesson("money-basics", 1, "Qu'est-ce que l'argent ?", 4, false), LessonStatus.DONE),
                    LessonItem(Lesson("inflation", 2, "L'inflation", 5, false), LessonStatus.IN_PROGRESS),
                    LessonItem(Lesson("riba", 3, "Le riba", 5, true), LessonStatus.TO_DO)
                )
            ),
            onOpenLesson = {}
        )
    }
}
