package be.encelade.vaporwave.gui

import java.awt.event.MouseEvent
import javax.swing.JTable
import javax.swing.table.TableColumn

/**
 * Syntactic sugar on top of <pre>javax.swing</pre>
 */
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

}
