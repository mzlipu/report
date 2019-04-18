package com.example.report.DatabaseConnection;

import com.example.report.Model.AssetGroupBy;
import com.example.report.Model.AssetLocation;
import com.example.report.Model.AssetPlan;
import com.example.report.Model.AssetSubCategory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableAsync
public class PgConnection implements AsyncConfigurer {

/*    private final String url = "jdbc:postgresql://192.168.1.186/uzzal";
    private final String user = "postgres";
    private final String password = "1234";*/   /*Uzzal Vai PC*/

/*    private final String url = "jdbc:postgresql://172.16.0.15/asset-mgmt";
    private final String user = "openpg";
    private final String password = "openpgpwd";*/  /*Client PC*/

/*    private final String url = "jdbc:postgresql://192.168.1.190/lipu";
    private final String user = "postgres";
    private final String password = "1234";*/     /*Lipu`s PC*/

    private final String url = "jdbc:postgresql://182.160.124.14/asset-mgmt";
    private final String user = "openpg";
    private final String password = "openpgpwd";  /*Client PC(Real IP)*/

    Connection conn = null;

    List<AssetPlan> assetPlans;
    List<AssetLocation> assetLocations;
    List<AssetSubCategory> assetSubCategorys;
    List<AssetGroupBy> assetGroupBys;
    int countLocation;
    int countSubCategory;

    String planVsActualDate;

    String subCategory;
    String startDate;
    String endDate;
    int countAssetPlan;
    int countSubCategoryForGanttChart;

    public PgConnection() {
        super();
    }

    public PgConnection(String planVsActualDate) {
        this.planVsActualDate = planVsActualDate;
    }

    public PgConnection(String subCategory, String startDate, String endDate) {
        this.subCategory = subCategory;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public Executor getAsyncExecutor() {
        return null;
    }

    @Async
    public void myMethod(){
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (final SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("PostgreSQL server Not Connected.");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("PostgreSQL server Not Connected......................ClassNotFoundException");
        }

        if (conn==null){
            System.out.println("con not found");
        }
        else {
//            final String sqlAssetPlan = "select * from asset_plan where date= CAST(CURRENT_TIMESTAMP AS DATE)";
            final String sqlAssetPlan = "select * from asset_plan where date= "+planVsActualDate;

            assetPlans= new ArrayList<AssetPlan>();

            try  {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sqlAssetPlan);

                while (rs.next()) {
                    AssetPlan assetPlan = new AssetPlan();
                    assetPlan.setId(rs.getInt("id"));
                    assetPlan.setStyleNo(rs.getString("style_no"));
                    assetPlan.setLocationId(rs.getInt("location"));
                    assetPlan.setCategoryId(rs.getInt("category_id"));
                    assetPlan.setSubCategoryId(rs.getInt("sub_category"));
                    assetPlan.setNumberOfAsset(Integer.parseInt(rs.getString("number_of_asset")));

                    assetPlans.add(assetPlan);
                }
            } catch (final SQLException e) {
                System.out.println(e.getMessage());
            }

            final String sqlAssetLocation = "select * from bt_asset_location";

            assetLocations= new ArrayList<AssetLocation>();

            try  {
                Statement stmtLocation = conn.createStatement();
                ResultSet rsLocation = stmtLocation.executeQuery(sqlAssetLocation);

                countLocation= 0;
                while (rsLocation.next()) {
                    AssetLocation assetLocation = new AssetLocation();
                    assetLocation.setLocationId(rsLocation.getInt("id"));
                    assetLocation.setLocationName(rsLocation.getString("name"));

                    assetLocations.add(assetLocation);
                    countLocation++;
                }
            } catch (final SQLException e) {
                System.out.println(e.getMessage());
            }

            final String sqlAssetSubCategory = "select * from bt_asset_sub_category";

            assetSubCategorys= new ArrayList<AssetSubCategory>();

            try  {
                Statement stmtSubCategory = conn.createStatement();
                ResultSet rsSubCategory = stmtSubCategory.executeQuery(sqlAssetSubCategory);

                countSubCategory=0;
                while (rsSubCategory.next()) {
                    AssetSubCategory assetSubCategory = new AssetSubCategory();
                    assetSubCategory.setSubCategoryId(rsSubCategory.getInt("id"));
                    assetSubCategory.setSubCategoryName(rsSubCategory.getString("name"));

                    assetSubCategorys.add(assetSubCategory);
                    countSubCategory++;
                }
            } catch (final SQLException e) {
                System.out.println(e.getMessage());
            }

            final String sqlAssetGroupBy = "select count(name) as count, sub_category, current_loc_id from bt_asset\n" +
                    "group by sub_category, current_loc_id;";

            assetGroupBys= new ArrayList<AssetGroupBy>();

            try  {
                Statement stmtAssetGroupBy = conn.createStatement();
                ResultSet rsAssetGroupBy = stmtAssetGroupBy.executeQuery(sqlAssetGroupBy);

                while (rsAssetGroupBy.next()) {
                    AssetGroupBy assetGroupBy = new AssetGroupBy();
                    assetGroupBy.setCount(rsAssetGroupBy.getInt("count"));
                    assetGroupBy.setSubCategoryId(rsAssetGroupBy.getInt("sub_category"));
                    assetGroupBy.setLocationId(rsAssetGroupBy.getInt("current_loc_id"));

                    assetGroupBys.add(assetGroupBy);
                }
            } catch (final SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Async
    public void runSqlForGanttChart(){
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (final SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("PostgreSQL server Not Connected.");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("PostgreSQL server Not Connected......................ClassNotFoundException");
        }

        if (conn==null){
            System.out.println("con not found");
        }
        else {
            final String sqlAssetPlan = "select * from asset_plan WHERE sub_category= "+subCategory+" AND date BETWEEN '"+startDate+"' AND '"+endDate+"'";

            assetPlans= new ArrayList<AssetPlan>();

            try  {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sqlAssetPlan);

                countAssetPlan= 0;
                while (rs.next()) {
                    AssetPlan assetPlan = new AssetPlan();
                    assetPlan.setId(rs.getInt("id"));
                    assetPlan.setDate(rs.getString("date"));
                    assetPlan.setStyleNo(rs.getString("style_no"));
                    assetPlan.setLocationId(rs.getInt("location"));
                    assetPlan.setCategoryId(rs.getInt("category_id"));
                    assetPlan.setSubCategoryId(rs.getInt("sub_category"));
                    assetPlan.setNumberOfAsset(Integer.parseInt(rs.getString("number_of_asset")));

                    assetPlans.add(assetPlan);
                    countAssetPlan++;
                }
            } catch (final SQLException e) {
                System.out.println(e.getMessage());
            }

            final String sqlAssetLocation = "select * from bt_asset_location";

            assetLocations= new ArrayList<AssetLocation>();

            try  {
                Statement stmtLocation = conn.createStatement();
                ResultSet rsLocation = stmtLocation.executeQuery(sqlAssetLocation);

                countLocation= 0;
                while (rsLocation.next()) {
                    AssetLocation assetLocation = new AssetLocation();
                    assetLocation.setLocationId(rsLocation.getInt("id"));
                    assetLocation.setLocationName(rsLocation.getString("name"));

                    assetLocations.add(assetLocation);
                    countLocation++;
                }
            } catch (final SQLException e) {
                System.out.println(e.getMessage());
            }

            final String sqlAssetCountSubCategory = "select count(*) from bt_asset where sub_category= "+subCategory;

            try  {
                Statement stmtAssetCountSubCategory = conn.createStatement();
                ResultSet rsLocation = stmtAssetCountSubCategory.executeQuery(sqlAssetCountSubCategory);

                while (rsLocation.next()) {
                    countSubCategoryForGanttChart = rsLocation.getInt("count");
                }
            } catch (final SQLException e) {
                System.out.println(e.getMessage());
            }
        }

    }

    public List<AssetPlan> selectAllAssetPlans(){
        if (conn!=null){
            return assetPlans;
        }
        else
            assetPlans= new ArrayList<AssetPlan>();
        return assetPlans;
    }

    public List<AssetLocation> selectAllAssetLocations(){
        if (conn!=null){
            return assetLocations;
        }
        else
            assetLocations= new ArrayList<AssetLocation>();
        return assetLocations;
    }

    public int countLocations(){
        return countLocation;
    }

    public List<AssetSubCategory> selectAllAssetSubCategories(){
        if (conn!=null){
            return assetSubCategorys;
        }
        else
            assetSubCategorys= new ArrayList<AssetSubCategory>();
        return assetSubCategorys;
    }

    public List<AssetGroupBy> selectAllAssetGroupBy(){
        if (conn!=null){
            return assetGroupBys;
        }
        else
            assetGroupBys= new ArrayList<AssetGroupBy>();
        return assetGroupBys;
    }

    public int countSubCategory(){
        return countSubCategory;
    }

    public int countAssetPlans(){
        return countAssetPlan;
    }

    public int countSubCategoryForGanttChart(){
        return countSubCategoryForGanttChart;
    }
}
