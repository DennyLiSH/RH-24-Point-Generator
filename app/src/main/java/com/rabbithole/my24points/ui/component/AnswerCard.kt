package com.rabbithole.my24points.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.rabbithole.my24points.R

/**
 * 答案卡片组件
 * 显示24点解法的卡片
 */
@Composable
fun AnswerCard(
    answer: String,
    hasSolution: Boolean,
    modifier: Modifier = Modifier
) {
    val answerLabel = stringResource(R.string.answer_label)
    val noSolutionLabel = stringResource(R.string.no_solution_label)
    val noSolutionMessage = stringResource(R.string.no_solution_message)

    Card(
        modifier = modifier
            .fillMaxWidth(0.9f)
            .semantics {
                contentDescription = if (hasSolution) "$answerLabel: $answer = 24" else noSolutionLabel
            },
        colors = CardDefaults.cardColors(
            containerColor = if (hasSolution)
                MaterialTheme.colorScheme.secondaryContainer
            else
                MaterialTheme.colorScheme.errorContainer
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (hasSolution) answerLabel else noSolutionLabel,
                style = MaterialTheme.typography.labelLarge,
                color = if (hasSolution)
                    MaterialTheme.colorScheme.onSecondaryContainer
                else
                    MaterialTheme.colorScheme.onErrorContainer
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (hasSolution) {
                Text(
                    text = answer,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "= 24",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            } else {
                Text(
                    text = noSolutionMessage,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}
