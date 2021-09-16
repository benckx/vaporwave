package be.encelade.vaporwave.gui

import java.awt.Color
import javax.swing.plaf.basic.BasicMenuItemUI

internal class CustomItemMenuUI : BasicMenuItemUI() {

    init {
        disabledForeground = Color.LIGHT_GRAY
    }

}
