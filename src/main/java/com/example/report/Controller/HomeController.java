package com.example.report.Controller;

import com.example.report.DatabaseConnection.PgConnection;
import com.example.report.Model.AssetGroupBy;
import com.example.report.Model.AssetLocation;
import com.example.report.Model.AssetPlan;
import com.example.report.Model.AssetSubCategory;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.*;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Muiduzzaman Lipu on 07-Feb-19.
 */
@Controller
public class HomeController {
    String planVsActualDate;
    PgConnection pgConnection;
    List<AssetPlan> listAssetPlan;
    List<AssetLocation> listAssetLocation;
    List<AssetSubCategory> listAssetSubCategory;
    List<AssetGroupBy> listAssetGroupBy;
    int countLocation;
    int countSubCategory;
    int countLocationForMarge;

    @RequestMapping(value = { "/planvsactual" }, method = RequestMethod.GET)
    public RedirectView index(@RequestParam("date") String date){
        planVsActualDate= date;
        deleteFile();
        init(date);
        createExcelSheet();
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("http://localhost:8095/files/planvsactual.xls");
        return redirectView;
    }

    public void init(String date){
        pgConnection = new PgConnection(date);
        pgConnection.myMethod();

        listAssetPlan= pgConnection.selectAllAssetPlans();
        listAssetLocation= pgConnection.selectAllAssetLocations();

        countLocation= pgConnection.countLocations();
        countLocationForMarge= (countLocation*3);

        countSubCategory= pgConnection.countSubCategory();

        listAssetSubCategory= pgConnection.selectAllAssetSubCategories();
        listAssetGroupBy= pgConnection.selectAllAssetGroupBy();
    }

    public void deleteFile(){
        File file = new File("E:\\Export-Excel\\planvsactual.xls");
        file.delete();
    }

    @GetMapping("/files/{filename}")
    @ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = loadFile(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    private final Path rootLocation = Paths.get("E:\\Export-Excel");

    public Resource loadFile(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if(resource.exists() || resource.isReadable()) {
                return resource;
            }else{
                throw new RuntimeException("FAIL!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("FAIL!");
        }
    }

    private void createExcelSheet(){
//        String EXCEL_FILE_LOCATION = "E:\\PlanVsActual.xls";

        /*String EXCEL_FILE_NAME = "PlanVsActual.xls";
        File directory = new File (System.getProperty("user.home") + "/Desktop/ExportExcel");
        directory.mkdirs();
        File file = new File(directory, EXCEL_FILE_NAME);*/

        String EXCEL_FILE_NAME = "planvsactual.xls";
        File directory = new File ("E:\\Export-Excel");
        directory.mkdirs();
        File file = new File(directory, EXCEL_FILE_NAME);

        WritableWorkbook myFirstWbook = null;
        try {
            myFirstWbook = Workbook.createWorkbook(file);

//            myFirstWbook = Workbook.createWorkbook(new File(EXCEL_FILE_LOCATION));

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

            excelSheet.mergeCells(0, 0, countLocationForMarge+3, 0);
            Label label = new Label(0, 0    , "FCI BD LTD.", cellFormatForHead);
            excelSheet.addCell(label);

            excelSheet.mergeCells(0, 1, countLocationForMarge+3, 1);
            label = new Label(0, 1, "PLAN Vs ACTUAL", cellFormatForSubHead);
            excelSheet.addCell(label);

            /*DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = new Date();*/
            excelSheet.mergeCells(0, 2, countLocationForMarge+3, 2);
            label = new Label(0, 2, "Date: "+planVsActualDate, cellFormatForSubHead);
            excelSheet.addCell(label);

            label = new Label(0, 3, "Location");
            excelSheet.addCell(label);
            label = new Label(0, 4, "Style No");
            excelSheet.addCell(label);

            int col=1;
            int margeColForLocation= col;
            int margeColForStyleNo= col;
            for (AssetLocation assetLocation:listAssetLocation) {
                excelSheet.mergeCells(margeColForLocation, 3, margeColForLocation+2, 3);
                label = new Label(col, 3, assetLocation.getLocationName(), cellFormatForAlignment);
                excelSheet.addCell(label);
                assetLocation.setColNumber(col);

                for (AssetPlan assetPlan:listAssetPlan) {
                    if(assetLocation.getLocationId() == assetPlan.getLocationId()){
                        excelSheet.mergeCells(margeColForStyleNo, 4, margeColForStyleNo+2, 4);
                        label = new Label(col, 4, assetPlan.getStyleNo(), cellFormatForAlignment);
                        excelSheet.addCell(label);
                        assetPlan.setColNumber(col);
                    }
                }
                col= col+3;
                margeColForLocation= col;
                margeColForStyleNo= col;
            }

            int colForPlanActuBala=1;
            for(int i=1; i<=countLocation+1; i++){
                label = new Label(colForPlanActuBala++, 5, "Plan");
                excelSheet.addCell(label);
                label = new Label(colForPlanActuBala++, 5, "Actual");
                excelSheet.addCell(label);
                label = new Label(colForPlanActuBala++, 5, "Balance");
                excelSheet.addCell(label);
            }

            excelSheet.mergeCells(countLocationForMarge+1, 3, countLocationForMarge+3, 4);
            label = new Label(countLocationForMarge+1, 3, "Grand Total", cellFormatForAlignment);
            excelSheet.addCell(label);

            int row=6;
            for (AssetSubCategory assetSubCategory:listAssetSubCategory) {
                label = new Label(0, row, assetSubCategory.getSubCategoryName());
                excelSheet.addCell(label);
                assetSubCategory.setRowNumber(row);
                row++;
            }

            label = new Label(0, countSubCategory+6, "Grand Total", cellFormatForAlignment);
            excelSheet.addCell(label);

            for (AssetSubCategory assetSubCategory:listAssetSubCategory) {
                for (AssetPlan assetPlan:listAssetPlan) {
                    if(assetPlan.getSubCategoryId() == assetSubCategory.getSubCategoryId()){
                        label = new Label(assetPlan.getColNumber(), assetSubCategory.getRowNumber(), String.valueOf(assetPlan.getNumberOfAsset()));
                        excelSheet.addCell(label);
                    }
                }
                for (AssetLocation assetLocation:listAssetLocation) {
                    for (AssetGroupBy assetGroupBy:listAssetGroupBy) {
                        if(assetSubCategory.getSubCategoryId() == assetGroupBy.getSubCategoryId() && assetLocation.getLocationId() == assetGroupBy.getLocationId()){
                            label = new Label(assetLocation.getColNumber()+1, assetSubCategory.getRowNumber(), String.valueOf(assetGroupBy.getCount()));
                            excelSheet.addCell(label);
                        }
                    }
                }
            }

            long grandGrandPlan=0;
            long grandGrandActual=0;
            long grandGrandBalance=0;
            for (AssetSubCategory assetSubCategory:listAssetSubCategory) {
                long grandPlan=0;
                long grandActual=0;
                long grandBalance=0;
                for (AssetLocation assetLocation:listAssetLocation) {
                    Cell cell1= excelSheet.getCell(assetLocation.getColNumber(), assetSubCategory.getRowNumber());
                    Cell cell2= excelSheet.getCell(assetLocation.getColNumber()+1, assetSubCategory.getRowNumber());
                    if(cell1.getContents()!= "" && cell2.getContents()!= ""){
                        int plan= Integer.parseInt(cell1.getContents());
                        int actual= Integer.parseInt(cell2.getContents());
                        int balance= actual-plan;
                        grandPlan= grandPlan+plan;
                        grandActual= grandActual+actual;
                        grandBalance= grandBalance+balance;

                        label = new Label(assetLocation.getColNumber()+2, assetSubCategory.getRowNumber(), String.valueOf(balance));
                        excelSheet.addCell(label);
                    }else if(cell1.getContents()!= "" && cell2.getContents()== ""){
                        int plan= Integer.parseInt(cell1.getContents());
                        int balance= -plan;
                        grandPlan= grandPlan+plan;
                        grandBalance= grandBalance+balance;

                        label = new Label(assetLocation.getColNumber()+2, assetSubCategory.getRowNumber(), String.valueOf(balance));
                        excelSheet.addCell(label);
                    }else if(cell1.getContents()== "" && cell2.getContents()!= ""){
                        int actual= Integer.parseInt(cell2.getContents());
                        int balance= actual;
                        grandActual= grandActual+actual;
                        grandBalance= grandBalance+balance;

                        label = new Label(assetLocation.getColNumber()+2, assetSubCategory.getRowNumber(), String.valueOf(balance));
                        excelSheet.addCell(label);
                    }else{
                        label = new Label(assetLocation.getColNumber()+2, assetSubCategory.getRowNumber(), String.valueOf(0));
                        excelSheet.addCell(label);
                    }
                }
                label = new Label(countLocationForMarge+1, assetSubCategory.getRowNumber(), String.valueOf(grandPlan));
                excelSheet.addCell(label);
                label = new Label(countLocationForMarge+2, assetSubCategory.getRowNumber(), String.valueOf(grandActual));
                excelSheet.addCell(label);
                label = new Label(countLocationForMarge+3, assetSubCategory.getRowNumber(), String.valueOf(grandBalance));
                excelSheet.addCell(label);

                grandGrandPlan= grandGrandPlan+grandPlan;
                grandGrandActual= grandGrandActual+grandActual;
                grandGrandBalance= grandGrandBalance+grandBalance;
            }

            label = new Label(countLocationForMarge+1, countSubCategory+6, String.valueOf(grandGrandPlan));
            excelSheet.addCell(label);
            label = new Label(countLocationForMarge+2, countSubCategory+6, String.valueOf(grandGrandActual));
            excelSheet.addCell(label);
            label = new Label(countLocationForMarge+3, countSubCategory+6, String.valueOf(grandGrandBalance));
            excelSheet.addCell(label);

            for (AssetLocation assetLocation:listAssetLocation) {
                long secondGrandPlan=0;
                long secondGrandActual=0;
                long secondGrandBalance=0;
                for(int i=6; i<=countSubCategory+5; i++){
                    Cell cell1= excelSheet.getCell(assetLocation.getColNumber(), i);
                    if(cell1.getContents()!=""){
                        int value= Integer.parseInt(cell1.getContents());
                        secondGrandPlan= secondGrandPlan+value;
                    }
                    Cell cell2= excelSheet.getCell(assetLocation.getColNumber()+1, i);
                    if(cell2.getContents()!=""){
                        int value= Integer.parseInt(cell2.getContents());
                        secondGrandActual= secondGrandActual+value;
                    }
                    Cell cell3= excelSheet.getCell(assetLocation.getColNumber()+2, i);
                    if(cell3.getContents()!=""){
                        int value= Integer.parseInt(cell3.getContents());
                        secondGrandBalance= secondGrandBalance+value;
                    }
                    label = new Label(assetLocation.getColNumber(), countSubCategory+6, String.valueOf(secondGrandPlan));
                    excelSheet.addCell(label);
                    label = new Label(assetLocation.getColNumber()+1, countSubCategory+6, String.valueOf(secondGrandActual));
                    excelSheet.addCell(label);
                    label = new Label(assetLocation.getColNumber()+2, countSubCategory+6, String.valueOf(secondGrandBalance));
                    excelSheet.addCell(label);
                }
            }

            for(int i=6; i<=countSubCategory+5; i++){
                for(int j=0; j<=countLocationForMarge; j++){
                    Cell cell= excelSheet.getCell(j, i);
                    if(i%2 == 0){
                        label = new Label(j, i, cell.getContents(), cellFormatForColor);
                        excelSheet.addCell(label);
                    }else{
                        label = new Label(j, i, cell.getContents());
                        excelSheet.addCell(label);
                    }
                }
            }

            for(int i=0; i<=countLocationForMarge+3; i++) {
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
