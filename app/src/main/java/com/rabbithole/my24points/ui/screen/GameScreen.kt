package com.rabbithole.my24points.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rabbithole.my24points.R
import com.rabbithole.my24points.ui.component.AnswerCard
import com.rabbithole.my24points.ui.component.NumberCard
import com.rabbithole.my24points.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun GameScreen(
    viewModel: GameViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    // 初始化时生成题目
    LaunchedEffect(Unit) {
        if (state.numbers.isEmpty()) {
            viewModel.generateNewPuzzle()
        }
    }

    val interactionSource = remember { MutableInteractionSource() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_title),
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val cardSize = (maxWidth - 80.dp) / 2  // 2 cards + 16dp gap + 32dp padding each side
            val topSpacing = if (maxWidth > 480.dp) 32.dp else 16.dp

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .combinedClickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = { viewModel.resetAnswer() },
                        onLongClick = { viewModel.setShowAnswer(true) }
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(topSpacing))

                // 数字卡片网格
                if (state.numbers.isNotEmpty()) {
                    NumberGrid(
                        numbers = state.numbers,
                        cardSize = cardSize,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 提示或答案
                if (state.showAnswer) {
                    AnswerCard(
                        answer = state.answer ?: "",
                        hasSolution = state.solvable,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    Text(
                        text = stringResource(R.string.hint_long_press),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // 生成新题目按钮
                Button(
                    onClick = { viewModel.generateNewPuzzle() },
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text(
                        text = stringResource(R.string.button_new_puzzle),
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun NumberGrid(
    numbers: List<Int>,
    cardSize: androidx.compose.ui.unit.Dp = 120.dp,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            numbers.take(2).forEach { number ->
                NumberCard(number = number, size = cardSize)
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            numbers.drop(2).forEach { number ->
                NumberCard(number = number, size = cardSize)
            }
        }
    }
}
