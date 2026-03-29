package com.rabbithole.my24points.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rabbithole.my24points.R

/**
 * 数字卡片组件
 * 显示单个数字的大卡片
 */
@Composable
fun NumberCard(
    number: Int,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp
) {
    val contentDesc = stringResource(R.string.number_content_description, number)
    Card(
        modifier = modifier
            .size(size)
            .semantics {
                contentDescription = contentDesc
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number.toString(),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = (size.value * 0.4).sp
                ),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}
