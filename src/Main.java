import DBconnector.MariaDAO;
import Functions.ExcelBuilder;
import Functions.GeneralFunctions;
import Model.Info;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        String start = null;
        GeneralFunctions funcs = new GeneralFunctions();
        try {
            start = args[0];
        } catch (Exception e) {
            start = funcs.getStartDate();
        }
        funcs.applicationLogo();
        Gson gson = new Gson();
        String fileName = "./properties.json";
        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        List<Info> infos = gson.fromJson(reader, new TypeToken<List<Info>>() {
        }.getType());
        MariaDAO dao = new MariaDAO();
        ExcelBuilder excelBuilder = new ExcelBuilder();
        try {
            for (Info info : infos) {
                if(!info.getType().equals("excel")){
                    funcs.sortingInfo(info);
                    dao.select(info, start);
                }else{
                    funcs.sortingInfo(info);
                    List<String> datas = dao.selectExcelInfo(info,start);
                    excelBuilder.ExcelWriter(datas,info);
                }

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        long endTime = System.currentTimeMillis();
        System.out.println(endTime-startTime    );
    }
}
