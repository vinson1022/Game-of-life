package com.vinson.gameoflife

data class DeviceProfile(val availableWidth: Float, var colAndRow: Int) {
    val cellWidth : Float
    val lineWidth : Float
    val gridWidth : Float

    init {
        cellWidth = availableWidth / colAndRow / 1.1f
        lineWidth = Math.max(0.1f * cellWidth, 1f)
        gridWidth = cellWidth + lineWidth
    }
}