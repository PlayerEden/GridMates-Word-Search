package com.playereden.gridmates.wordsearch.core

/**
 * The entire puzzle board is drawn at one uniform zoom with a fixed viewport.
 * Pan offsets use screen pixels and are limited to the genuinely enlarged
 * board extent. This helper has no Android dependencies, so geometry can be
 * tested against the exact magnifications used in owner screenshots.
 */
data class BoardViewportTransform(
    val zoom: Float,
    val panX: Float = 0f,
    val panY: Float = 0f
) {
    init { require(zoom in 1f..2.5f && zoom.isFinite()) }

    fun clamped(viewportPx: Float): BoardViewportTransform {
        require(viewportPx > 0f && viewportPx.isFinite())
        val limit = -(viewportPx * (zoom - 1f))
        return copy(panX = panX.coerceIn(limit, 0f), panY = panY.coerceIn(limit, 0f))
    }

    fun panned(dx: Float, dy: Float, viewportPx: Float): BoardViewportTransform =
        copy(panX = panX + dx, panY = panY + dy).clamped(viewportPx)

    /** Keep the same content point under viewport center when magnifying. */
    fun zoomed(requested: Float, viewportPx: Float): BoardViewportTransform {
        require(viewportPx > 0f && viewportPx.isFinite())
        val next = requested.coerceIn(1f, 2.5f)
        val focus = viewportPx / 2f
        val factor = next / zoom
        return BoardViewportTransform(
            zoom = next,
            panX = focus - (focus - panX) * factor,
            panY = focus - (focus - panY) * factor
        ).clamped(viewportPx)
    }

    /** Inverse transformation mirrors Compose graphics-layer hit testing. */
    fun cellAt(screenX: Float, screenY: Float, viewportPx: Float, cells: Int): GridPoint? {
        if (screenX !in 0f..<viewportPx || screenY !in 0f..<viewportPx || cells <= 0) return null
        val boardX = (screenX - panX) / zoom
        val boardY = (screenY - panY) / zoom
        if (boardX !in 0f..<viewportPx || boardY !in 0f..<viewportPx) return null
        return GridPoint(
            ((boardY / viewportPx) * cells).toInt().coerceIn(0, cells - 1),
            ((boardX / viewportPx) * cells).toInt().coerceIn(0, cells - 1)
        )
    }
}
