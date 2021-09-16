package be.encelade.vaporwave.gui.components

import java.awt.Color
import javax.swing.plaf.basic.BasicMenuItemUI

internal class CustomMenuUI : BasicMenuItemUI() {

    init {
        disabledForeground = Color.LIGHT_GRAY
    }

}
