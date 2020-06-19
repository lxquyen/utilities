package com.steve.utilities.domain.model

import com.steve.utilities.core.extensions.Array2D

class Board(val matrix: Array2D<Cell?>?) {

    fun findWrongItem(x: Int, y: Int, value: Int): MutableList<Cell> {
        val result = mutableListOf<Cell>()

        matrix ?: return result

        (0 until 9).forEach {
            val cellVertical = matrix[x, it]
            val cellHorizontal = matrix[it, y]
            if (cellVertical?.value == value) {
                result.add(cellVertical)
            }

            if (cellHorizontal?.value == value) {
                result.add(cellHorizontal)
            }
        }

        val startX = x / 3 * 3
        val startY = y / 3 * 3
        (startX until startX + 3).forEach { indexX ->
            (startY until startY + 3).forEach { indexY ->
                val cellGroup = matrix[indexX, indexY]
                if (cellGroup?.value == value) {
                    result.add(cellGroup)
                }
            }
        }

        return result
    }
}