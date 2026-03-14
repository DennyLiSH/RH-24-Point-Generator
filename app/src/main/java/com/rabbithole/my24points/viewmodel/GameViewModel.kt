package com.rabbithole.my24points.viewmodel

import androidx.lifecycle.ViewModel
import com.rabbithole.my24points.solver.GameSolver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

/**
 * 游戏状态
 */
data class GameState(
    val numbers: List<Int> = emptyList(),   // 4个数字
    val answer: String? = null,              // 解法表达式（可能无解）
    val solvable: Boolean = false,           // 是否有解
    val showAnswer: Boolean = false,         // 是否显示答案
    val isGenerating: Boolean = false        // 加载状态
)

/**
 * 游戏ViewModel
 */
class GameViewModel : ViewModel() {
    private val _state = MutableStateFlow(GameState())
    val state = _state.asStateFlow()

    /**
     * 生成新题目
     */
    fun generateNewPuzzle() {
        _state.update { it.copy(isGenerating = true) }

        // 生成4个1-13的随机数
        val numbers = List(4) { Random.nextInt(1, 14) }

        // 求解
        val result = GameSolver.solve(numbers)

        _state.update {
            GameState(
                numbers = numbers,
                answer = result.expression,
                solvable = result.solvable,
                showAnswer = false,
                isGenerating = false
            )
        }
    }

    /**
     * 设置是否显示答案
     */
    fun setShowAnswer(show: Boolean) {
        _state.update { it.copy(showAnswer = show) }
    }

    /**
     * 重置答案显示状态
     */
    fun resetAnswer() {
        _state.update { it.copy(showAnswer = false) }
    }
}
