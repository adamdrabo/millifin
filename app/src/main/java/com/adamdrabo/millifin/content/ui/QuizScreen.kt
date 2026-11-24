package com.adamdrabo.millifin.content.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adamdrabo.millifin.R
import com.adamdrabo.millifin.content.model.Question
import com.adamdrabo.millifin.core.theme.LocalAnswerColors
import com.adamdrabo.millifin.core.theme.MillifinTheme
import com.adamdrabo.millifin.core.ui.MillifinTopBar
import com.adamdrabo.millifin.core.ui.PrimaryButton
import com.adamdrabo.millifin.core.ui.SecondaryButton

@Composable
fun QuizScreen(
    onBack: () -> Unit,
    onBackToLessons: () -> Unit,
    viewModel: QuizViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    QuizContent(
        uiState = uiState,
        onBack = onBack,
        onSelect = viewModel::selectAnswer,
        onNext = viewModel::next,
        onRestart = viewModel::restart,
        onBackToLessons = onBackToLessons
    )
}

@Composable
fun QuizContent(
    uiState: QuizUiState,
    onBack: () -> Unit,
    onSelect: (Int) -> Unit,
    onNext: () -> Unit,
    onRestart: () -> Unit,
    onBackToLessons: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = { MillifinTopBar(title = stringResource(R.string.quiz_title), onBack = onBack) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (uiState) {
                QuizUiState.Loading -> CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.Center)
                )

                QuizUiState.NoQuestions -> Text(
                    text = stringResource(R.string.quiz_no_questions),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Center)
                )

                is QuizUiState.Answering -> AnsweringContent(uiState, onSelect, onNext)

                is QuizUiState.Finished -> FinishedContent(uiState, onRestart, onBackToLessons)
            }
        }
    }
}

@Composable
private fun AnsweringContent(
    state: QuizUiState.Answering,
    onSelect: (Int) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.quiz_question_number, state.questionNumber, state.questionCount),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = state.question.text,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            state.question.choices.forEachIndexed { index, choice ->
                ChoiceCard(
                    text = choice,
                    outcome = choiceOutcome(state, index),
                    enabled = !state.isAnswered,
                    onClick = { onSelect(index) }
                )
            }
            if (state.isAnswered) {
                Explanation(
                    correct = state.selectedIndex == state.question.correctIndex,
                    explanation = state.question.explanation
                )
            }
        }
        PrimaryButton(
            text = stringResource(
                if (state.isLastQuestion) R.string.quiz_see_result else R.string.quiz_next_question
            ),
            onClick = onNext,
            enabled = state.isAnswered,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

enum class ChoiceOutcome { NEUTRAL, CORRECT, WRONG }

fun choiceOutcome(state: QuizUiState.Answering, index: Int): ChoiceOutcome = when {
    !state.isAnswered -> ChoiceOutcome.NEUTRAL
    index == state.question.correctIndex -> ChoiceOutcome.CORRECT
    index == state.selectedIndex -> ChoiceOutcome.WRONG
    else -> ChoiceOutcome.NEUTRAL
}

@Composable
private fun ChoiceCard(
    text: String,
    outcome: ChoiceOutcome,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val answerColors = LocalAnswerColors.current
    val borderColor: Color
    val containerColor: Color
    when (outcome) {
        ChoiceOutcome.CORRECT -> {
            borderColor = answerColors.success
            containerColor = answerColors.successContainer
        }
        ChoiceOutcome.WRONG -> {
            borderColor = answerColors.error
            containerColor = answerColors.errorContainer
        }
        ChoiceOutcome.NEUTRAL -> {
            borderColor = MaterialTheme.colorScheme.outline
            containerColor = MaterialTheme.colorScheme.surface
        }
    }
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        border = BorderStroke(if (outcome == ChoiceOutcome.NEUTRAL) 1.dp else 2.dp, borderColor),
        modifier = Modifier
            .fillMaxWidth()
            .semantics { role = Role.RadioButton }
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun Explanation(correct: Boolean, explanation: String) {
    val answerColors = LocalAnswerColors.current
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = stringResource(if (correct) R.string.quiz_correct else R.string.quiz_wrong),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (correct) answerColors.success else answerColors.error
        )
        Text(
            text = explanation,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun FinishedContent(
    state: QuizUiState.Finished,
    onRestart: () -> Unit,
    onBackToLessons: () -> Unit
) {
    val answerColors = LocalAnswerColors.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${state.scorePercent} %",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = if (state.passed) answerColors.success else answerColors.error
        )
        Text(
            text = stringResource(R.string.quiz_score, state.correctAnswers, state.questionCount),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = stringResource(if (state.passed) R.string.quiz_passed else R.string.quiz_failed),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        PrimaryButton(text = stringResource(R.string.quiz_back_to_lessons), onClick = onBackToLessons)
        SecondaryButton(text = stringResource(R.string.quiz_retry), onClick = onRestart)
    }
}

@Preview(showBackground = true)
@Composable
private fun QuizAnsweredPreview() {
    MillifinTheme {
        QuizContent(
            uiState = QuizUiState.Answering(
                question = Question(
                    id = "q1",
                    text = "Lequel de ces exemples est du riba ?",
                    choices = listOf("Prêter 1 000 $, exiger 1 100 $", "Épargner dans une tirelire", "Acheter du pain"),
                    correctIndex = 0,
                    explanation = "Exiger plus que la somme prêtée est un intérêt."
                ),
                questionNumber = 1,
                questionCount = 3,
                selectedIndex = 1
            ),
            onBack = {},
            onSelect = {},
            onNext = {},
            onRestart = {},
            onBackToLessons = {}
        )
    }
}
