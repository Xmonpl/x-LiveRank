package cf.xmon.liverank.utils;

import cf.xmon.liverank.LiveRank;
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
        String body;
        if (multi){
            if (antypoke && antypw) {
                body = HttpRequest.get(req + "&antypoke&antymessage&name=" + nick).body();
                logger.info("MULTI antypoke & antypw = true");
            } else if (antypoke) {
                body = HttpRequest.get(req + "&antypoke&name=" + nick).body();
                logger.info("MULTI antypoke = true");
            } else if (antypw) {
                body = HttpRequest.get(req + "&antymessage&name=" + nick).body();
                logger.info("MULTI antypw = true");
            } else {
                body = HttpRequest.get(req + "&name=" + nick).body();
                logger.info("MULTI antypoke & antypw = false");
            }
        }else {
            if (antypoke && antypw) {
                body = HttpRequest.get(req + "&antypoke&antymessage").body();
                logger.info("antypoke & antypw = true");
            } else if (antypoke) {
                body = HttpRequest.get(req + "&antypoke").body();
                logger.info("antypoke = true");
            } else if (antypw) {
                body = HttpRequest.get(req + "&antymessage").body();
                logger.info("antypw = true");
            } else {
                body = HttpRequest.get(req).body();
                logger.info("antypoke & antypw = false");
            }
        }
        if (body.contains("Error Code 1")){
            DialogBoxUtil.errorBox("Skontaktuj się z Matisem lub Xmonem!", "x-LiveRank - Error");
            logger.warning("Error Code 1");
            System.exit(-1);
        }
        if (body.contains("Not connected")){
            DialogBoxUtil.errorBox("Włącz aplikacje TeamSpeak.", "x-LiveRank - Nie połączony!");
            LiveRank.on$off$item.setState(false);
            LiveRank.on$off$bool = false;
        }
        body = null;
    }
}
