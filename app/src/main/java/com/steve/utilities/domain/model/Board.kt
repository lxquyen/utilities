package com.steve.utilities.domain.model

import com.steve.utilities.core.extensions.Array2D
import timber.log.Timber

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
        Timber.d("findWrongItem: ${result.size}")
        return result
    }
}