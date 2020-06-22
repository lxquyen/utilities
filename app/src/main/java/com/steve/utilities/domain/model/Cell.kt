package com.steve.utilities.domain.model

class Cell() {
    var x = 0
    var y = 0
    var value = 0
    var isEditable = false

    constructor(x: Int, y: Int, value: Int) : this() {
        this.x = x
        this.y = y
        this.value = value
    }

    constructor(cell: Cell) : this() {
        this.x = cell.x
        this.y = cell.y
        this.value = cell.value
    }

    override fun toString(): String {
        return "x: $x, y: $y, value: $value"
    }
}
