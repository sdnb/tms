package cn.snzo.utils;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by LiXiaolan on 2017/4/5.
 */
public class ExcelUtils {
    /**
     * 对外提供读取Excel的方法
     */
    public static List<List<Object>> readExcel(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        String extension = fileName.lastIndexOf(".") == -1 ? "" : fileName
                .substring(fileName.lastIndexOf(".") + 1);
        if ("xls".equals(extension)) {
            return read2003Excel(file.getInputStream());
        } else if ("xlsx".equals(extension)) {
            return read2007Excel(file.getInputStream());
        } else {
            throw new IOException("不支持此文件类型");
        }

    }

    /**
     * 读取offce 2003 Excel
     */
    private static List<List<Object>> read2003Excel(InputStream inputStream) throws IOException {
        List<List<Object>> lists = new LinkedList<List<Object>>();
        HSSFWorkbook hwb = new HSSFWorkbook(inputStream);
        HSSFSheet sheet = hwb.getSheetAt(0);
        Object value = null;
        HSSFRow row = null;
        HSSFCell cell = null;
        int counter = 0;
        for (int i = sheet.getFirstRowNum(); counter < sheet.getPhysicalNumberOfRows(); i++) {
            row = sheet.getRow(i);
            if (row == null) {
                continue;
            } else {
                counter++;
            }
            List<Object> link = new LinkedList<Object>();
            for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
                cell = row.getCell(j);
                if (cell == null) {
                    link.add(null);
                    continue;
                }
                DecimalFormat df = new DecimalFormat("0");//格式化 number string
                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//格式化日期字符串
                DecimalFormat nf = new DecimalFormat("0");//格式化数字
                switch (cell.getCellType()) {
                    case XSSFCell.CELL_TYPE_STRING:
                        System.out.println(i + "行" + j + "列 is String type");
                        value = cell.getStringCellValue();
                        break;
                    case XSSFCell.CELL_TYPE_NUMERIC:
                        System.out.println(i + "行" + j + "列 is Number type DateFormat:" + cell.getCellStyle().getDataFormatString());
                        if ("@".equals(cell.getCellStyle().getDataFormatString())) {
                            value = df.format(cell.getNumericCellValue());
                        } else if ("General".equals(cell.getCellStyle().getDataFormatString())) {
                            value = nf.format(cell.getNumericCellValue());
                        } else {
                            value = sf.format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue()));
                        }
                        break;
                    case XSSFCell.CELL_TYPE_BOOLEAN:
                        System.out.println(i + "行" + j + "列 is boolean type");
                        value = cell.getBooleanCellValue();
                        break;
                    case XSSFCell.CELL_TYPE_BLANK:
                        System.out.println(i + "行" + j + "列 is blank type");
                        value = "";
                        break;
                    default:
                        System.out.println(i+"行"+j+"列 is default type");
                        value=cell.toString();
                }
                if (value==null||value.equals("")){
                    continue;
                }
                link.add(value);

            }
            lists.add(link);
        }
        return lists;
    }

    /**
     *读取offce 2007 Excel
     */
    private static List<List<Object>> read2007Excel(InputStream inputStream) throws IOException {
        List<List<Object>> lists = new LinkedList<List<Object>>();
        //构造XSSFWorkbook对象，strPath传入文件路径
        XSSFWorkbook xwb = new XSSFWorkbook(inputStream);
        //读取第一章表格内容
        XSSFSheet sheet = xwb.getSheetAt(0);
        Object value = null;
        XSSFRow row = null;
        XSSFCell cell = null;
        int counter = 0;
        for (int i = sheet.getFirstRowNum(); counter < sheet.getPhysicalNumberOfRows(); i++) {
            row = sheet.getRow(i);
            if (row == null) {
                continue;
            } else {
                counter++;
            }
            List<Object> linked = new LinkedList<Object>();
            for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
                cell = row.getCell(j);
                if (cell == null) {
                    continue;
                }
                DecimalFormat df = new DecimalFormat("0");//格式化 number string
                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//格式化日期字符串
                DecimalFormat nf = new DecimalFormat("0");//格式化数字
                switch (cell.getCellType()) {
                    case XSSFCell.CELL_TYPE_STRING:
                        System.out.println(i + "行" + j + " 列 is String type");
                        value = cell.getStringCellValue();
                        break;

                    case XSSFCell.CELL_TYPE_NUMERIC:
                        System.out.println(i + "行" + j
                                + " 列 is Number type ; DateFormt:"
                                + cell.getCellStyle().getDataFormatString());
                        if ("@".equals(cell.getCellStyle().getDataFormatString())) {
                            value = df.format(cell.getNumericCellValue());
                        } else if ("General".equals(cell.getCellStyle()
                                .getDataFormatString())) {
                            value = nf.format(cell.getNumericCellValue());
                        } else {
                            value = sf.format(HSSFDateUtil.getJavaDate(cell
                                    .getNumericCellValue()));
                        }
                        break;
                    case XSSFCell.CELL_TYPE_BOOLEAN:
                        System.out.println(i + "行" + j + " 列 is Boolean type");
                        value = cell.getBooleanCellValue();
                        break;
                    case XSSFCell.CELL_TYPE_BLANK:
                        System.out.println(i + "行" + j + " 列 is Blank type");
                        value = "";
                        break;
                    default:
                        System.out.println(i + "行" + j + " 列 is default type");
                        value = cell.toString();
                }
                if (value == null || "".equals(value)) {
                    continue;
                }
                linked.add(value);
            }
            lists.add(linked);
        }
        return lists;
    }

}
