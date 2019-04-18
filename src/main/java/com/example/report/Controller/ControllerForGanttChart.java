package com.example.report.Controller;

import com.example.report.DatabaseConnection.PgConnection;
import com.example.report.Model.AssetLocation;
import com.example.report.Model.AssetPlan;
import com.example.report.Model.DateModel;
import jxl.Cell;
import jxl.CellView;
import jxl.Workbook;
import jxl.write.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Muiduzzaman Lipu on 17-Feb-19.
 */
@Controller
public class ControllerForGanttChart {

    String subCategoryName;
    PgConnection pgConnection;

    List<AssetPlan> listAssetPlan;
    List<AssetLocation> listAssetLocation;
    List<DateModel> listDates;

    int countAssetPlan;
    int countLocation;
    int countDate;
    int countSubCategoryForGanttChart;

    @RequestMapping(value = { "/ganttchart" }, method = RequestMethod.GET)
    public RedirectView initGanttChart(@RequestParam("subCategory") String subCategory, @RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate, @RequestParam("subCategoryName") String subCategoryNameParameter) {
        deleteFile();
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        listDates = new ArrayList<DateModel>();
        countDate=0;
        while (!start.isAfter(end)) {
            DateModel dateModel = new DateModel();
            if(start.getDayOfWeek() == DayOfWeek.FRIDAY){
                dateModel.setFriday(true);
            }
            dateModel.setDate(String.valueOf(start));
            listDates.add(dateModel);
            start = start.plusDays(1);
            countDate++;
        }

        subCategoryName = subCategoryNameParameter;
        pgConnection = new PgConnection(subCategory, startDate, endDate);
        pgConnection.runSqlForGanttChart();

        listAssetPlan= pgConnection.selectAllAssetPlans();
        listAssetLocation= pgConnection.selectAllAssetLocations();

        countAssetPlan= pgConnection.countAssetPlans();
        countLocation= pgConnection.countLocations();
        countSubCategoryForGanttChart= pgConnection.countSubCategoryForGanttChart();

        createExcelSheet();

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("http://localhost:8095/files/GanttChart.xls");
        return redirectView;
    }

    public void deleteFile(){
        File file = new File("E:\\Export-Excel\\GanttChart.xls");
        file.delete();
    }

    private void createExcelSheet(){
//        String EXCEL_FILE_NAME = "E:\\GanttChart.xls";

        /*String EXCEL_FILE_NAME = "GanttChart.xls";
        File directory = new File (System.getProperty("user.home") + "/Desktop/ExportExcel");
        directory.mkdirs();
        File file = new File(directory, EXCEL_FILE_NAME);*/

        String EXCEL_FILE_NAME = "GanttChart.xls";
        File directory = new File ("E:\\Export-Excel");
        directory.mkdirs();
        File file = new File(directory, EXCEL_FILE_NAME);

        WritableWorkbook myFirstWbook = null;
        try {
            myFirstWbook = Workbook.createWorkbook(file);

//            myFirstWbook = Workbook.createWorkbook(new File(EXCEL_FILE_NAME));

            WritableSheet excelSheet = myFirstWbook.createSheet("Sheet 1", 0);

            WritableFont cellFontForHead = new WritableFont(WritableFont.TIMES, 20);
            cellFontForHead.setBoldStyle(WritableFont.BOLD);
            WritableCellFormat cellFormatForHead = new WritableCellFormat(cellFontForHead);
            cellFormatForHead.setAlignment(Alignment.CENTRE);
            cellFormatForHead.setVerticalAlignment(VerticalAlignment.CENTRE);

            WritableFont cellFontForSubHead = new WritableFont(WritableFont.TIMES, 12);
            WritableCellFormat cellFormatForSubHead = new WritableCellFormat(cellFontForSubHead);
            cellFormatForSubHead.setAlignment(Alignment.CENTRE);
            cellFormatForSubHead.setVerticalAlignment(VerticalAlignment.CENTRE);

            WritableCellFormat cellFormatForColor = new WritableCellFormat();
            cellFormatForColor.setBackground(Colour.LIGHT_GREEN);

            WritableCellFormat cellFormatForAlignment = new WritableCellFormat();
            cellFormatForAlignment.setAlignment(Alignment.CENTRE);
            cellFormatForAlignment.setVerticalAlignment(VerticalAlignment.CENTRE);

            WritableCellFormat cellFormatForFriday = new WritableCellFormat();
            cellFormatForFriday.setBackground(Colour.YELLOW);
            cellFormatForFriday.setAlignment(Alignment.CENTRE);
            cellFormatForFriday.setVerticalAlignment(VerticalAlignment.CENTRE);

            excelSheet.mergeCells(0, 0, countDate, 0);
            Label label = new Label(0, 0    , "FCI BD LTD.", cellFormatForHead);
            excelSheet.addCell(label);

            excelSheet.mergeCells(0, 1, countDate, 1);
            label = new Label(0, 1, "Gantt Chart", cellFormatForSubHead);
            excelSheet.addCell(label);

            excelSheet.mergeCells(0, 2, countDate, 2);
            label = new Label(0, 2, "Requirement of "+subCategoryName, cellFormatForSubHead);
            excelSheet.addCell(label);

            label = new Label(0, 3, "Location/Date");
            excelSheet.addCell(label);

            int row=4;
            for (AssetLocation assetLocation:listAssetLocation) {
                label = new Label(0, row, assetLocation.getLocationName());
                excelSheet.addCell(label);
                assetLocation.setRowNumber(row);
                row++;
            }

            int col=1;
            for (DateModel dateModel : listDates) {
                label = new Label(col, 3, dateModel.getDate(), cellFormatForAlignment);
                excelSheet.addCell(label);
                dateModel.setColNumber(col);
                col++;
            }

            for (AssetLocation assetLocation:listAssetLocation) {
                for (AssetPlan assetPlan:listAssetPlan) {
                    for (DateModel dateModel:listDates) {
                        if(assetLocation.getLocationId() == assetPlan.getLocationId() && assetPlan.getDate().equals(dateModel.getDate())){
                            label = new Label(dateModel.getColNumber(), assetLocation.getRowNumber(), String.valueOf(assetPlan.getNumberOfAsset()));
                            excelSheet.addCell(label);
                        }
                    }
                }
            }

            for(int i=1; i<=countDate; i++) {
                for (int j = 4; j <= 3 + countLocation; j++) {
                    Cell cell1 = excelSheet.getCell(i, j);
                    if (cell1.getContents() == "") {
                        label = new Label(i, j, String.valueOf(0));
                        excelSheet.addCell(label);
                    }
                }
            }

            label = new Label(0, 4+countLocation, "Requirement", cellFormatForAlignment);
            excelSheet.addCell(label);
            label = new Label(0, 4+countLocation+1, "Available", cellFormatForAlignment);
            excelSheet.addCell(label);
            label = new Label(0, 4+countLocation+2, "Excess/Short", cellFormatForAlignment);
            excelSheet.addCell(label);

            for(int i=1; i<=countDate; i++){
                label = new Label(i, 4+countLocation+1, String.valueOf(countSubCategoryForGanttChart), cellFormatForAlignment);
                excelSheet.addCell(label);
            }

            for(int i=1; i<=countDate; i++){
                long sum= 0;
                for(int j=4; j<=3+countLocation; j++){
                    Cell cell1= excelSheet.getCell(i, j);
                    if(cell1.getContents()!=""){
                        int value= Integer.parseInt(cell1.getContents());
                        sum= sum+value;
                    }
                }
                label = new Label(i, 4+countLocation, String.valueOf(sum), cellFormatForAlignment);
                excelSheet.addCell(label);

                Cell cell1= excelSheet.getCell(i, 4+countLocation+1);
                int value= Integer.parseInt(cell1.getContents());

                label = new Label(i, 4+countLocation+2, String.valueOf(value-sum), cellFormatForAlignment);
                excelSheet.addCell(label);
            }

            for(int i=0; i<=countDate; i++){
                for(int j=4; j<=3+countLocation; j++){
                    Cell cell= excelSheet.getCell(i, j);
                    if(j%2 == 0){
                        label = new Label(i, j, cell.getContents(), cellFormatForColor);
                        excelSheet.addCell(label);
                    }else{
                        label = new Label(i, j, cell.getContents());
                        excelSheet.addCell(label);
                    }
                }
            }

            for (DateModel dateModel:listDates) {
                if(dateModel.isFriday()){
                    excelSheet.mergeCells(dateModel.getColNumber(), 4, dateModel.getColNumber(), 4+countLocation+2);
                    label = new Label(dateModel.getColNumber(), 4, "Friday", cellFormatForFriday);
                    excelSheet.addCell(label);
                }
            }

            for(int i=0; i<=countDate; i++) {
                CellView cell = excelSheet.getColumnView(i);
                cell.setAutosize(true);
                excelSheet.setColumnView(i, cell);
            }

            myFirstWbook.write();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        } finally {

            if (myFirstWbook != null) {
                try {
                    myFirstWbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (WriteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
