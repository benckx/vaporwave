package be.encelade.vaporwave.gui

import java.awt.event.MouseEvent
import javax.swing.JTable
import javax.swing.table.TableColumn

internal object ListenerExtensions {

    fun JTable.addTableHeaderClickListener(callback: (MouseEvent, TableColumn) -> Unit) {
        this.tableHeader.addMouseListener(MouseClickListener { mouseEvent ->
            findColumnByX(this, mouseEvent.x)?.let { column ->
                callback(mouseEvent, column)
            }
        })
    }

    private fun findColumnByX(table: JTable, x: Int): TableColumn? {
        val nbrOfColumns = table.columnCount

        (0 until nbrOfColumns)
                .map { i -> table.columnModel.getColumn(i) }
                .reversed()
                .forEach { column ->
                    val startAtPosition = (0 until nbrOfColumns)
                            .map { i -> table.columnModel.getColumn(i) }
                            .subList(0, column.modelIndex)
                            .sumOf { previousColumn -> previousColumn.width }

                    if (startAtPosition < x) {
                        return column
                    }
                }

        return null
    }

}
