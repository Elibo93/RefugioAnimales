package es.refugio.common.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.function.Function;

/**
 * Utilidad genérica y desacoplada para la exportación de listados de datos a formato Microsoft Excel.
 * Esta clase no depende del Servlet API (HttpServletResponse), lo que permite su reutilización
 * en cualquier capa o microservicio (controladores web, servicios asíncronos, tareas programadas, etc.).
 */
public class ExcelExportHelper {

    /**
     * Genera un libro de Excel en memoria a partir de una lista de objetos y extractores de valores.
     *
     * @param sheetName        Nombre de la pestaña del reporte.
     * @param headers          Lista de títulos para las columnas.
     * @param data             Lista de elementos a exportar.
     * @param valueExtractors  Funciones para extraer el valor de cada columna a partir de un elemento.
     * @param <T>              Tipo del objeto de datos.
     * @return                 Array de bytes (byte[]) que representa el archivo Excel (.xlsx).
     * @throws Exception       Si ocurre algún error en la generación del archivo.
     */
    public static <T> byte[] exportToExcel(
            String sheetName,
            List<String> headers,
            List<T> data,
            List<Function<T, Object>> valueExtractors) throws Exception {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet(sheetName);

            // Fuente para la cabecera
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setFontHeightInPoints((short) 11);

            // Estilo para la cabecera (color corporativo TEAL)
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setFillForegroundColor(IndexedColors.TEAL.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setAlignment(HorizontalAlignment.LEFT);
            headerCellStyle.setBorderBottom(BorderStyle.MEDIUM);

            // Estilo para las celdas de datos
            CellStyle dataCellStyle = workbook.createCellStyle();
            dataCellStyle.setBorderBottom(BorderStyle.THIN);
            dataCellStyle.setBorderTop(BorderStyle.THIN);
            dataCellStyle.setBorderLeft(BorderStyle.THIN);
            dataCellStyle.setBorderRight(BorderStyle.THIN);

            // Crear fila de cabecera
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(headerCellStyle);
            }

            // Rellenar filas de datos
            int rowIdx = 1;
            for (T item : data) {
                Row row = sheet.createRow(rowIdx++);
                for (int colIdx = 0; colIdx < valueExtractors.size(); colIdx++) {
                    Cell cell = row.createCell(colIdx);
                    cell.setCellStyle(dataCellStyle);
                    
                    Object val = valueExtractors.get(colIdx).apply(item);
                    if (val == null) {
                        cell.setCellValue("");
                    } else if (val instanceof Number) {
                        cell.setCellValue(((Number) val).doubleValue());
                    } else if (val instanceof Boolean) {
                        cell.setCellValue((Boolean) val ? "Sí" : "No");
                    } else {
                        cell.setCellValue(String.valueOf(val));
                    }
                }
            }

            // Auto-ajustar tamaño de columnas para evitar recortes de texto
            for (int i = 0; i < headers.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }
}
