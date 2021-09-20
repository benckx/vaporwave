package be.encelade.vaporwave.gui

import java.awt.event.MouseEvent
import javax.swing.JTable
import javax.swing.border.EmptyBorder
import javax.swing.table.TableColumn

internal object SwingExtensions {

    fun JTable.addTableHeaderClickListener(callback: (MouseEvent, TableColumn) -> Unit) {
        this.tableHeader.addMouseListener(MouseClickListener { mouseEvent ->
            findColumnByX(this, mouseEvent.x)?.let { column ->
                callback(mouseEvent, column)
            }
        })
    }

    private fun findColumnByX(table: JTable, x: Int): TableColumn? {
        table
                .listColumns()
                .reversed()
                .forEach { column ->
                    val startAtPosition = table
                            .listColumns()
                            .subList(0, column.modelIndex)
                            .sumOf { previousColumn -> previousColumn.width }

                    if (startAtPosition < x) {
                        return column
                    }
                }

        return null
    }

    fun JTable.listColumns(): List<TableColumn> {
        return (0 until columnCount).map { i -> columnModel.getColumn(i) }
    }

    /**
     * Convenience method with Kotlin named parameters
     */
    fun EmptyBorder.copy(top: Int? = null, left: Int? = null, bottom: Int? = null, right: Int? = null): EmptyBorder {
        val t = top ?: borderInsets.top
        val l = left ?: borderInsets.left
        val b = bottom ?: borderInsets.bottom
        val r = right ?: borderInsets.right
        return EmptyBorder(t, l, b, r)
    }

}
