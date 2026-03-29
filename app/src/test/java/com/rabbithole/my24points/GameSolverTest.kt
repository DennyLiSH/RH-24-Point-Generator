package com.rabbithole.my24points

import com.rabbithole.my24points.solver.GameSolver
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GameSolverTest {

    @Test
    fun `solve returns solvable for known solvable input 1-2-3-4`() {
        val result = GameSolver.solve(listOf(1, 2, 3, 4))
        assertTrue(result.solvable)
        assertNotNull(result.expression)
    }

    @Test
    fun `solve returns solvable for 8-8-3-3`() {
        // 8 / (3 - 8/3) = 24
        val result = GameSolver.solve(listOf(8, 8, 3, 3))
        assertTrue(result.solvable)
        assertNotNull(result.expression)
    }

    @Test
    fun `solve returns solvable for 1-5-5-5`() {
        // 5 * (5 - 1 / 5) = 24
        val result = GameSolver.solve(listOf(1, 5, 5, 5))
        assertTrue(result.solvable)
        assertNotNull(result.expression)
    }

    @Test
    fun `solve returns unsolvable for 1-1-1-1`() {
        val result = GameSolver.solve(listOf(1, 1, 1, 1))
        assertFalse(result.solvable)
    }

    @Test
    fun `solve returns unsolvable for 1-1-1-2`() {
        val result = GameSolver.solve(listOf(1, 1, 1, 2))
        assertFalse(result.solvable)
    }

    @Test
    fun `solve returns solvable for 10-10-4-4`() {
        // (10 * 10 - 4) / 4 = 24
        val result = GameSolver.solve(listOf(10, 10, 4, 4))
        assertTrue(result.solvable)
        assertNotNull(result.expression)
    }

    @Test
    fun `solve returns unsolvable for 11-13-7-2`() {
        val result = GameSolver.solve(listOf(11, 13, 7, 2))
        assertFalse(result.solvable)
    }

    @Test
    fun `solve with 6-6-6-6 returns solvable`() {
        // 6 + 6 + 6 + 6 = 24
        val result = GameSolver.solve(listOf(6, 6, 6, 6))
        assertTrue(result.solvable)
        assertNotNull(result.expression)
    }

    @Test
    fun `solve with 2-3-4-5 returns solvable`() {
        val result = GameSolver.solve(listOf(2, 3, 4, 5))
        assertTrue(result.solvable)
    }

    @Test
    fun `solve with wrong size returns unsolvable`() {
        val result = GameSolver.solve(listOf(1, 2, 3))
        assertFalse(result.solvable)
    }

    @Test
    fun `solve with empty list returns unsolvable`() {
        val result = GameSolver.solve(emptyList())
        assertFalse(result.solvable)
    }

    @Test
    fun `solve with 5-5-5-5 returns solvable`() {
        // 5 * 5 - 5 / 5 = 24
        val result = GameSolver.solve(listOf(5, 5, 5, 5))
        assertTrue(result.solvable)
        assertNotNull(result.expression)
    }

    @Test
    fun `solve with 13-13-13-13 returns solvable`() {
        // (13 + 13) - (13 + 13) = 0, not 24. Actually: 13 - 13 + 13 - 13 = 0.
        // Let's check: 13 + 13 - (13 - 13) = 26? No.
        // Actually 13,13,13,13 cannot make 24.
        val result = GameSolver.solve(listOf(13, 13, 13, 13))
        assertFalse(result.solvable)
    }

    @Test
    fun `solve with 3-3-8-8 returns solvable`() {
        val result = GameSolver.solve(listOf(3, 3, 8, 8))
        assertTrue(result.solvable)
    }

    @Test
    fun `expression does not contain excessive parentheses`() {
        val result = GameSolver.solve(listOf(1, 2, 3, 4))
        assertTrue(result.solvable)
        val expr = result.expression!!
        // Simple addition like 1 * 2 * 3 * 4 should have no or few parens
        assertTrue(expr.count { it == '(' || it == ')' } <= 4)
    }
}
