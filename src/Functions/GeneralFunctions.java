package Functions;

import Model.Info;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class GeneralFunctions {

    public void applicationLogo(){
        System.out.println();
        System.out.println("ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ");
        System.out.println(" MegaDataHandler");
        System.out.println(" Application version 1.0.0");
        System.out.println("ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ");
        System.out.println();
    }

    public void introduction(){
        System.out.println("default                DB에 있는 삭제 테이블 조건대로 DB데이터 삭제 진행");
        System.out.println("-tbNm[테이블명]         삭제 대상 테이블 명");
        System.out.println("-dtNm[삭제조건컬럼명]    삭제 조건 컬럼 명");
        System.out.println("-delDay[보관일수]       보관 일수");
        System.out.println("limitCnt[삭제제한건수]  삭제제한건수");
        System.out.println("-frDt[삭제시작기간]     삭제시작기간");
        System.out.println("-toDt[삭제종료시간]     삭제종료시간");
    }



    public String getStartDate(){
        Date d = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(d.getTime()-60*60*24*1000);
    }

    public void sortingInfo(Info info){
        if(info.getBatchSize() == 0) info.setBatchSize(1000);
        if(info.getExecuteOption() == null) info.setExecuteOption("test");
    }

}
