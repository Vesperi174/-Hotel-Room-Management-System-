package com.hotel.view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class StatusColorRenderer implements TableCellRenderer {

    private final TableCellRenderer delegate;
    private final int statusColumn;
    private final Map<String, Color> colorMap;
    private final Color defaultColor;

    public StatusColorRenderer(TableCellRenderer delegate, int statusColumn,
                               Map<String, Color> colorMap, Color defaultColor) {
        this.delegate = delegate;
        this.statusColumn = statusColumn;
        this.colorMap = colorMap;
        this.defaultColor = defaultColor;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        Component c = delegate.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);

        if (!isSelected) {
            Object statusObj = table.getValueAt(row, statusColumn);
            String status = statusObj != null ? statusObj.toString() : "";
            Color bg = colorMap.getOrDefault(status, defaultColor);
            c.setBackground(bg);
        }
        return c;
    }

    public static void applyToTable(JTable table, int statusColumn,
                                     Map<String, Color> colorMap, Color defaultColor) {
        table.setDefaultRenderer(Object.class, new StatusColorRenderer(
                new DefaultTableCellRenderer(), statusColumn, colorMap, defaultColor));
    }
}