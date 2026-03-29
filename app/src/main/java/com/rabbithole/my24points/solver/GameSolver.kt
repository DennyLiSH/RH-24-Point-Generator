package com.rabbithole.my24points.solver

import kotlin.math.abs

/**
 * 24点游戏求解器
 * 给定4个数字，找出是否能通过加减乘除运算得到24
 */
object GameSolver {
    private const val TARGET = 24.0
    private const val EPSILON = 1e-9
    private val operators = charArrayOf('+', '-', '*', '/')

    /**
     * 表达式AST节点
     */
    sealed class ExprNode {
        data class Number(val value: Int) : ExprNode()
        data class BinaryOp(
            val left: ExprNode,
            val operator: Char,
            val right: ExprNode
        ) : ExprNode()

        companion object {
            private val precedence = mapOf('+' to 1, '-' to 1, '*' to 2, '/' to 2)
            fun getPrecedence(op: Char): Int = precedence[op] ?: 0
        }

        /**
         * 生成最小括号表达式字符串
         */
        fun toMinimalString(parentOp: Char? = null, isRightChild: Boolean = false): String {
            return when (this) {
                is Number -> value.toString()
                is BinaryOp -> {
                    val leftStr = left.toMinimalString(operator, false)
                    val rightStr = right.toMinimalString(operator, true)

                    val needParens = if (parentOp == null) {
                        false
                    } else {
                        val parentPrec = getPrecedence(parentOp)
                        val myPrec = getPrecedence(operator)
                        when {
                            // 优先级低于父节点，需要括号
                            myPrec < parentPrec -> true
                            // 优先级相等且是右子节点，父节点是减法或除法时需要括号
                            myPrec == parentPrec && isRightChild &&
                                (parentOp == '-' || parentOp == '/') -> true
                            else -> false
                        }
                    }

                    val expr = "$leftStr $operator $rightStr"
                    if (needParens) "($expr)" else expr
                }
            }
        }

        /**
         * 生成友好的表达式字符串，避免中间结果为负数
         * 核心转换：a - (b - c) -> (c - b) + a
         */
        fun toFriendlyString(parentOp: Char? = null, isRightChild: Boolean = false): String {
            return when (this) {
                is Number -> value.toString()
                is BinaryOp -> {
                    // 检测 a - (b - c) 模式，转换为 (c - b) + a
                    if (operator == '-' && right is BinaryOp && right.operator == '-') {
                        val a = left
                        val b = right.left
                        val c = right.right

                        // 构建新的表达式：(c - b) + a
                        val newLeft = BinaryOp(c, '-', b)  // (c - b)
                        val newExpr = BinaryOp(newLeft, '+', a)  // (c - b) + a
                        return newExpr.toFriendlyString(parentOp, isRightChild)
                    }

                    // 默认处理逻辑
                    val leftStr = left.toFriendlyString(operator, false)
                    val rightStr = right.toFriendlyString(operator, true)

                    val needParens = if (parentOp == null) {
                        false
                    } else {
                        val parentPrec = getPrecedence(parentOp)
                        val myPrec = getPrecedence(operator)
                        when {
                            myPrec < parentPrec -> true
                            myPrec == parentPrec && isRightChild &&
                                (parentOp == '-' || parentOp == '/') -> true
                            else -> false
                        }
                    }

                    val expr = "$leftStr $operator $rightStr"
                    if (needParens) "($expr)" else expr
                }
            }
        }

        /**
         * 计算乘法优先得分
         * 当乘除的操作数是加减运算时，说明乘除优先执行，得分更高
         */
        fun multiplicationPriorityScore(): Int {
            return when (this) {
                is Number -> 0
                is BinaryOp -> {
                    val leftScore = left.multiplicationPriorityScore()
                    val rightScore = right.multiplicationPriorityScore()
                    val selfScore = when (operator) {
                        '*', '/' -> {
                            val leftBonus = if (left is BinaryOp &&
                                (left.operator == '+' || left.operator == '-')) 2 else 0
                            val rightBonus = if (right is BinaryOp &&
                                (right.operator == '+' || right.operator == '-')) 2 else 0
                            leftBonus + rightBonus
                        }
                        else -> 0
                    }
                    leftScore + rightScore + selfScore
                }
            }
        }
    }

    /**
     * 表达式结果，包含计算值和AST节点
     */
    private data class Expr(val value: Double, val node: ExprNode)

    /**
     * 求解结果
     * @param solvable 是否有解
     * @param expression 解法表达式（如 "(1 + 2) * (3 + 5)"）
     */
    data class Result(
        val solvable: Boolean,
        val expression: String?
    )

    /**
     * 求解24点
     * @param numbers 4个数字列表
     * @return 求解结果
     */
    fun solve(numbers: List<Int>): Result {
        if (numbers.size != 4) {
            return Result(false, null)
        }

        val allSolutions = mutableListOf<Expr>()
        val perms = mutableListOf<List<Int>>()
        generatePermutations(numbers.toMutableList(), 0, perms)

        for (perm in perms) {
            solveWithPermutation(perm, allSolutions)
        }

        if (allSolutions.isEmpty()) {
            return Result(false, null)
        }

        // 去重并排序：乘法优先得分降序 → 括号数量升序
        val uniqueSolutions = allSolutions
            .distinctBy { it.node.toFriendlyString() }

        // 缓存排序所需的计算结果
        data class ScoredSolution(
            val expr: Expr,
            val friendlyString: String,
            val mulPriority: Int,
            val parenCount: Int
        )

        val best = uniqueSolutions
            .map { expr ->
                val friendly = expr.node.toFriendlyString()
                ScoredSolution(
                    expr = expr,
                    friendlyString = friendly,
                    mulPriority = expr.node.multiplicationPriorityScore(),
                    parenCount = friendly.count { c -> c == '(' || c == ')' }
                )
            }
            .maxWithOrNull(compareBy<ScoredSolution> { it.mulPriority }.thenBy { it.parenCount })

        return Result(true, best?.friendlyString)
    }

    /**
     * 对特定排列尝试所有括号结构和运算符组合
     */
    private fun solveWithPermutation(nums: List<Int>, solutions: MutableList<Expr>) {
        val a = nums[0].toDouble()
        val b = nums[1].toDouble()
        val c = nums[2].toDouble()
        val d = nums[3].toDouble()

        for (op1 in operators) {
            for (op2 in operators) {
                for (op3 in operators) {
                    // 结构1: ((a op1 b) op2 c) op3 d
                    compute(a, op1, b)?.let { ab ->
                        compute(ab.value, op2, c)?.let { abc ->
                            compute(abc.value, op3, d)?.let { abcd ->
                                if (abs(abcd.value - TARGET) < EPSILON) {
                                    val node = ExprNode.BinaryOp(
                                        ExprNode.BinaryOp(
                                            ExprNode.BinaryOp(
                                                ExprNode.Number(nums[0]), op1, ExprNode.Number(nums[1])
                                            ), op2, ExprNode.Number(nums[2])
                                        ), op3, ExprNode.Number(nums[3])
                                    )
                                    solutions.add(Expr(abcd.value, node))
                                }
                            }
                        }
                    }

                    // 结构2: (a op1 (b op2 c)) op3 d
                    compute(b, op2, c)?.let { bc ->
                        compute(a, op1, bc.value)?.let { abc ->
                            compute(abc.value, op3, d)?.let { abcd ->
                                if (abs(abcd.value - TARGET) < EPSILON) {
                                    val node = ExprNode.BinaryOp(
                                        ExprNode.BinaryOp(
                                            ExprNode.Number(nums[0]), op1,
                                            ExprNode.BinaryOp(
                                                ExprNode.Number(nums[1]), op2, ExprNode.Number(nums[2])
                                            )
                                        ), op3, ExprNode.Number(nums[3])
                                    )
                                    solutions.add(Expr(abcd.value, node))
                                }
                            }
                        }
                    }

                    // 结构3: (a op1 b) op2 (c op3 d)
                    compute(a, op1, b)?.let { ab ->
                        compute(c, op3, d)?.let { cd ->
                            compute(ab.value, op2, cd.value)?.let { result ->
                                if (abs(result.value - TARGET) < EPSILON) {
                                    val node = ExprNode.BinaryOp(
                                        ExprNode.BinaryOp(
                                            ExprNode.Number(nums[0]), op1, ExprNode.Number(nums[1])
                                        ), op2,
                                        ExprNode.BinaryOp(
                                            ExprNode.Number(nums[2]), op3, ExprNode.Number(nums[3])
                                        )
                                    )
                                    solutions.add(Expr(result.value, node))
                                }
                            }
                        }
                    }

                    // 结构4: a op1 ((b op2 c) op3 d)
                    compute(b, op2, c)?.let { bc ->
                        compute(bc.value, op3, d)?.let { bcd ->
                            compute(a, op1, bcd.value)?.let { result ->
                                if (abs(result.value - TARGET) < EPSILON) {
                                    val node = ExprNode.BinaryOp(
                                        ExprNode.Number(nums[0]), op1,
                                        ExprNode.BinaryOp(
                                            ExprNode.BinaryOp(
                                                ExprNode.Number(nums[1]), op2, ExprNode.Number(nums[2])
                                            ), op3, ExprNode.Number(nums[3])
                                        )
                                    )
                                    solutions.add(Expr(result.value, node))
                                }
                            }
                        }
                    }

                    // 结构5: a op1 (b op2 (c op3 d))
                    compute(c, op3, d)?.let { cd ->
                        compute(b, op2, cd.value)?.let { bcd ->
                            compute(a, op1, bcd.value)?.let { result ->
                                if (abs(result.value - TARGET) < EPSILON) {
                                    val node = ExprNode.BinaryOp(
                                        ExprNode.Number(nums[0]), op1,
                                        ExprNode.BinaryOp(
                                            ExprNode.Number(nums[1]), op2,
                                            ExprNode.BinaryOp(
                                                ExprNode.Number(nums[2]), op3, ExprNode.Number(nums[3])
                                            )
                                        )
                                    )
                                    solutions.add(Expr(result.value, node))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 执行单次运算
     */
    private fun compute(a: Double, op: Char, b: Double): Expr? {
        val result = when (op) {
            '+' -> a + b
            '-' -> a - b
            '*' -> a * b
            '/' -> if (abs(b) < EPSILON) return null else a / b
            else -> return null
        }
        return Expr(result, ExprNode.Number(0)) // node 在上层构建
    }

    /**
     * 生成全排列
     */
    private fun generatePermutations(
        list: MutableList<Int>,
        start: Int,
        results: MutableList<List<Int>>
    ) {
        if (start == list.size - 1) {
            results.add(list.toList())
            return
        }
        for (i in start until list.size) {
            list.swap(start, i)
            generatePermutations(list, start + 1, results)
            list.swap(start, i)
        }
    }

    private fun <T> MutableList<T>.swap(i: Int, j: Int) {
        val temp = this[i]
        this[i] = this[j]
        this[j] = temp
    }
}
