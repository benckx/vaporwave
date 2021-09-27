package be.encelade.vaporwave.gui

import java.awt.event.MouseEvent
import java.awt.event.MouseListener

/**
 * Syntactic sugar on top of [MouseListener].
 */
internal class MouseClickListener(private val callback: (MouseEvent) -> Unit) : MouseListener {

    override fun mouseClicked(e: MouseEvent?) {
        e?.let { callback(it) }
    }

    override fun mousePressed(p0: MouseEvent?) {
        // do nothing
    }

    override fun mouseReleased(p0: MouseEvent?) {
        // do nothing
    }

    override fun mouseEntered(p0: MouseEvent?) {
        // do nothing
    }

    override fun mouseExited(p0: MouseEvent?) {
        // do nothing
    }

}
