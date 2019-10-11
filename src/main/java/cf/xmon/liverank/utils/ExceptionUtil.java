package cf.xmon.liverank.utils;

import cf.xmon.liverank.LiveRank;
import com.github.kevinsawicki.http.HttpRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ExceptionUtil {
    /* Logger */
    private static Logger logger = Logger.getLogger("ExceptionUtil");
    private static String send = "";
    public static void exception(Exception e){
        error("\n[x-LiveRank] @ " + LiveRank.nickname);
        error(" Java: " + System.getProperty("java.version"));
        error(" Thread: " + Thread.currentThread());
        error(" {nickname$multiple = " + LiveRank.nickname$multiple + ", startup$bool = " + LiveRank.startup$bool + ", antypoke$bool = " + LiveRank.antypoke$bool + ", antypw$bool = " + LiveRank.antypw$bool + ", on$off$bool = " + LiveRank.on$off$bool + "}");
        error(" Błąd: " + e.toString());
        for (int i = 0; i < e.getStackTrace().length; i++){
            String[] splited = e.getStackTrace()[i].toString().split("\\(");
            if (splited[0].contains("cf.xmon")) {
                String line = splited[1];
                line = line.replace(":",  " | Linijka: ");
                line = line.replace("\\)", "");
                error(" Klasa: " + line);
            }
        }
        logger.warning(send);
        Map<String, String> data = new HashMap<String, String>();
        data.put("exception", send);
        logger.warning(HttpRequest.post("https://admin.playts.eu/manage/liveranks/backend.php").form(data).body());
        data.clear();
        send = "";
    }

    private static void error(String str){
        logger.warning(str);
        send += str + "\n";
    }
}
