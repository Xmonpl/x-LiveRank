package cf.xmon.liverank.utils;

import com.github.kevinsawicki.http.HttpRequest;

import java.util.logging.Logger;

/**
 * @author Xmon
 * @version 1.0
 */

public class ReqUtil {
    /* Logger */
    private static Logger logger = Logger.getLogger("ReqUtil");
    public static void req(String req, boolean antypoke, boolean antypw, boolean multi, String nick){
        if (multi){
            if (antypoke && antypw) {
                HttpRequest.get(req + "&antypoke&antymessage&name=" + nick).body();
                logger.info("MULTI antypoke & antypw = true");
            } else if (antypoke) {
                HttpRequest.get(req + "&antypoke&name=" + nick).body();
                logger.info("MULTI antypoke = true");
            } else if (antypw) {
                HttpRequest.get(req + "&antymessage&name=" + nick).body();
                logger.info("MULTI antypw = true");
            } else {
                HttpRequest.get(req + "&name=" + nick).body();
                logger.info("MULTI antypoke & antypw = false");
            }
        }else {
            if (antypoke && antypw) {
                HttpRequest.get(req + "&antypoke&antymessage").body();
                logger.info("antypoke & antypw = true");
            } else if (antypoke) {
                HttpRequest.get(req + "&antypoke").body();
                logger.info("antypoke = true");
            } else if (antypw) {
                HttpRequest.get(req + "&antymessage").body();
                logger.info("antypw = true");
            } else {
                HttpRequest.get(req).body();
                logger.info("antypoke & antypw = false");
            }
        }
    }
}
