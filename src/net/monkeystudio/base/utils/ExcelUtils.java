package net.monkeystudio.base.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaxin
 */
public class ExcelUtils {
    /**
     * 从导入的Excel文件中读取导入的信息
     *
     * @param
     * @param is2007
     * @throws IOException
     */
    public static List<Object[]> getExcelData(MultipartFile fileItem, boolean is2007)  {
        Workbook workbook = null;
        if (is2007) {
            try {
                workbook = new XSSFWorkbook(fileItem.getInputStream());
            } catch (IOException e) {
                Log.e(e);
            }
        } else {
            try {
                workbook = new HSSFWorkbook(fileItem.getInputStream());
            } catch (IOException e) {
                Log.e(e);
            }
        }
        Sheet sheet = workbook.getSheetAt(0);
        int lrn = sheet.getLastRowNum() + 1;//获取行数 Excel表格中的行和列的下标均是由0开始
        // 表头列 获取excel的数据的列数
        Row columnHeadRow = sheet.getRow(0);
        int columnNum = columnHeadRow.getLastCellNum();

        List<Object[]> data = new ArrayList<Object[]>();
        for (int i = 0; i < lrn; i++) {
            Row columnRow = sheet.getRow(i);
            if (columnRow != null) {// 测试中发现columnRow
                // 有可能为空，Excel使用delete键删除整行数据后出现
                columnRow.getLastCellNum();
                if (columnNum < columnRow.getLastCellNum()) {
                    columnNum = columnRow.getLastCellNum();
                }
                Object[] o = new Object[columnNum];
                boolean all_not_null = false;// 标记是否为空白行
                for (int n = 0; n < columnNum; n++) {
                    String celltext = getStringValue(columnRow, n);
                    o[n] = celltext;
                    all_not_null = all_not_null || StringUtils.isNotBlank(celltext);
                }
                if (all_not_null) {
                    data.add(o);
                }
            }
        }
        return data;
    }

    /**
     * 获得excel单元格信息
     *
     * @param row
     * @param cellIndex
     * @return
     */
    public static String getStringValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                RichTextString text = cell.getRichStringCellValue();
                if (text == null) {
                    return null;
                } else {
                    return text.getString().trim();
                }
            case Cell.CELL_TYPE_NUMERIC:
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue()));
                }
                double d = cell.getNumericCellValue();
                if (d == (long) d) {
                    return String.valueOf((long) d);
                } else {
                    return String.valueOf(d);
                }
            default:
                return null;
        }
    }
}
