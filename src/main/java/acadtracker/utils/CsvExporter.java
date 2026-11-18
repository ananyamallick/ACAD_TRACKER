
package acadtracker.utils;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import java.io.BufferedWriter;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class CsvExporter {

    private CsvExporter() {
    }

    public static void exportTable(JTable table, Path file)
            throws IOException {

        TableModel model = table.getModel();

        try (BufferedWriter writer = Files.newBufferedWriter(
                file,
                StandardCharsets.UTF_8
        )) {

            // UTF-8 BOM helps some spreadsheet programs
            // identify the file's text encoding.
            writer.write('\uFEFF');

            // Export column headings.
            for (int column = 0;
                 column < model.getColumnCount();
                 column++) {

                if (column > 0) {
                    writer.write(",");
                }

                writer.write(escape(
                        model.getColumnName(column)
                ));
            }

            writer.newLine();

            // Export table rows.
            for (int row = 0;
                 row < model.getRowCount();
                 row++) {

                for (int column = 0;
                     column < model.getColumnCount();
                     column++) {

                    if (column > 0) {
                        writer.write(",");
                    }

                    Object value =
                            model.getValueAt(row, column);

                    writer.write(escape(
                            value == null ? "" : value.toString()
                    ));
                }

                writer.newLine();
            }
        }
    }

    
private static String escape(String value) {

    String safeValue = value;

    if (!safeValue.isEmpty()) {
        char first = safeValue.charAt(0);

        if (first == '=' || first == '+'
                || first == '-' || first == '@'
                || first == '\t' || first == '\r') {

            safeValue = "'" + safeValue;
        }
    }

    String escaped = safeValue.replace("\"", "\"\"");

    return "\"" + escaped + "\"";
}

}
