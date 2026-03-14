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
     * 表达式结果，包含计算值和表达式字符串
     */
    private data class Expr(val value: Double, val str: String)

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

        val perms = mutableListOf<List<Int>>()
        generatePermutations(numbers.toMutableList(), 0, perms)

        for (perm in perms) {
            val expr = solveWithPermutation(perm)
            if (expr != null && abs(expr.value - TARGET) < EPSILON) {
                return Result(true, expr.str)
            }
        }

        return Result(false, null)
    }

    /**
     * 对特定排列尝试所有括号结构和运算符组合
     */
    private fun solveWithPermutation(nums: List<Int>): Expr? {
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
                                    return Expr(abcd.value, "((${nums[0]} $op1 ${nums[1]}) $op2 ${nums[2]}) $op3 ${nums[3]}")
                                }
                            }
                        }
                    }

                    // 结构2: (a op1 (b op2 c)) op3 d
                    compute(b, op2, c)?.let { bc ->
                        compute(a, op1, bc.value)?.let { abc ->
                            compute(abc.value, op3, d)?.let { abcd ->
                                if (abs(abcd.value - TARGET) < EPSILON) {
                                    return Expr(abcd.value, "(${nums[0]} $op1 (${nums[1]} $op2 ${nums[2]})) $op3 ${nums[3]}")
                                }
                            }
                        }
                    }

                    // 结构3: (a op1 b) op2 (c op3 d)
                    compute(a, op1, b)?.let { ab ->
                        compute(c, op3, d)?.let { cd ->
                            compute(ab.value, op2, cd.value)?.let { result ->
                                if (abs(result.value - TARGET) < EPSILON) {
                                    return Expr(result.value, "(${nums[0]} $op1 ${nums[1]}) $op2 (${nums[2]} $op3 ${nums[3]})")
                                }
                            }
                        }
                    }

                    // 结构4: a op1 ((b op2 c) op3 d)
                    compute(b, op2, c)?.let { bc ->
                        compute(bc.value, op3, d)?.let { bcd ->
                            compute(a, op1, bcd.value)?.let { result ->
                                if (abs(result.value - TARGET) < EPSILON) {
                                    return Expr(result.value, "${nums[0]} $op1 ((${nums[1]} $op2 ${nums[2]}) $op3 ${nums[3]})")
                                }
                            }
                        }
                    }

                    // 结构5: a op1 (b op2 (c op3 d))
                    compute(c, op3, d)?.let { cd ->
                        compute(b, op2, cd.value)?.let { bcd ->
                            compute(a, op1, bcd.value)?.let { result ->
                                if (abs(result.value - TARGET) < EPSILON) {
                                    return Expr(result.value, "${nums[0]} $op1 (${nums[1]} $op2 (${nums[2]} $op3 ${nums[3]}))")
                                }
                            }
                        }
                    }
                }
            }
        }
        return null
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
        return Expr(result, "")
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
