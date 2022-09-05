package robot;

import backen.utils.FileUtil;
import backen.utils.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import robot.entity.AppConfig;
import robot.entity.NewRecordBody;
import robot.entity.Response;
import robot.entity.ResponseType.NewRecordResult;
import robot.entity.ResponseType.RunStandard;
import robot.entity.ResponseType.SchoolBound;
import robot.entity.ResponseType.UserInfo;
import robot.run.Request;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;


public class Run {
    public static void main(String[] strArr) throws IOException {
        String token = "1243489ade4c457702e7c9c7fe2698a0";
        AppConfig config = new AppConfig() {{
            setAppVersion("1.8.3");     // APP版本，一般不做修改
            setBrand("realme");         // 手机品牌
            setMobileType("RMX2117");   // 型号
            setSysVersion("10");        // 系统版本
        }};
        if (config.getBrand().length() == 0) {
            System.out.println("请配置手机型号信息");
            System.exit(-1);
        }
        Request request = new Request(token, config);
        Scanner scanner = new Scanner(System.in);
        System.out.print("账号（手机）：");
        String next = scanner.next();
        System.out.print("密码：");
        String next2 = scanner.next();
        System.out.print("跑步路程(米)：");
        long nextLong = scanner.nextLong();
        System.out.print("跑步时间(分钟)：");
        int nextInt = scanner.nextInt();
        double d = ((((double) nextInt)) / ((double) nextLong)) * 1000.0d;
        if (d < 6.0d) {
            System.out.println("八分是治愈，七分是自娱，六分是养身，五分是自伤，四分是自残，三分是自毁。");
            System.out.printf("你的配速是：%.2f 分钟/公里, %s", Double.valueOf(d), new String[]{"我认为这种事情是不可能的", "太快了", "要死了", "你正在自毁", "你正在自残", "你得锻炼正造成身体上的损伤", "六分是养身", "七分是自娱", "八分是治愈"}[(int) d]);
            System.exit(-1);
        }
        System.out.printf("平均配速：%.2f\n", Double.valueOf(d));
        UserInfo login = request.login(next, next2).getResponse();
        long userId = login.getUserId();
        if (userId != -1) {
            RunStandard runStandard = request.getRunStandard();
            SchoolBound[] schoolBound = request.getSchoolBound();
            NewRecordBody newRecordBody = new NewRecordBody();
            newRecordBody.setUserId(userId);
            newRecordBody.setAppVersions(config.getAppVersion());
            newRecordBody.setBrand(config.getBrand());
            newRecordBody.setMobileType(config.getMobileType());
            newRecordBody.setSysVersions(config.getSysVersion());
            newRecordBody.setRunDistance(nextLong);
            newRecordBody.setRunTime(nextInt);
            newRecordBody.setYearSemester(runStandard.getSemesterYear());
            newRecordBody.setRealityTrackPoints(schoolBound[0].getSiteBound() + "--");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
            simpleDateFormat.applyPattern("yyyy-MM-dd");
            newRecordBody.setRecordDate(simpleDateFormat.format(new Date()));
            newRecordBody.setTrackPoints(genTack(nextLong));
            String recordNew = request.recordNew(newRecordBody);
            Response response = (Response) JsonUtil.string2Obj(recordNew, new TypeReference<Response<NewRecordResult>>() {
            });
            System.out.println(recordNew);
            return;
        }
        System.out.println("用户Id获取失败");
    }

    public static String genTack(long j) {
        String ReadFile = FileUtil.ReadFile("/map.json"));
        if (ReadFile.length() != 0) {
            return TrackUtils.gen(j, (Location[]) JsonUtils.string2Obj(ReadFile, Location[].class));
        }
        System.out.println("配置读取失败");
        return null;
    }
}
