package cf.xmon.liverank.tasks;

import cf.xmon.liverank.LiveRank;
import cf.xmon.liverank.utils.DialogBoxUtil;
import cf.xmon.liverank.utils.ReqUtil;
import com.github.kevinsawicki.http.HttpRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author Xmon
 * @version 1.0
 */

public class GameTask {
    public static Timer timer;
    private static String s = "";
    private static Integer alive = 0;
    /* Logger */
    private static Logger logger = Logger.getLogger("GameTask");

    public static void update() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (LiveRank.on$off$bool){
                    try {
                        //tasklist /FI "Status eq Running" dodac lepsza ram
                        Process p = Runtime.getRuntime().exec("tasklist /FI \"Status eq Running\"");
                        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
                        Scanner scan = new Scanner(input);
                        while (scan.hasNextLine()) {
                            s += scan.nextLine();
                        }

                        Arrays.stream(LiveRank.processList).parallel().forEach(process ->{
                            if (s.contains(process)){
                                ReqUtil.req("https://admin.playts.eu/manage/liveranks/backend.php?play=" + process.replace(".exe", ""), LiveRank.antypoke$bool ,LiveRank.antypw$bool, LiveRank.nickname$multiple, LiveRank.nickname);
                                System.out.println("HttpReq " + process);
                                alive++;
                            }else{
                                logger.info(process);
                            }
                        });
                        if (alive == 0){
                            String body;
                            if (LiveRank.nickname$multiple){
                                body = HttpRequest.get("https://admin.playts.eu/manage/liveranks/backend.php?name=" + LiveRank.nickname).body();
                            }else {
                                body = HttpRequest.get("https://admin.playts.eu/manage/liveranks/backend.php").body();
                            }
                            if (body.contains("Error Code 1")){
                                DialogBoxUtil.errorBox("Skontaktuj się z Matisem lub Xmonem!", "x-LiveRank - Error");
                                logger.warning("Error Code 1");
                                System.exit(-1);
                            }
                            if (body.contains("Not connected")){
                                DialogBoxUtil.errorBox("Włącz aplikacje TeamSpeak.", "x-LiveRank - Nie połączony!");
                                System.exit(-1);
                            }
                            logger.info("Alive Task ");
                        }
                        alive = 0;
                        s = null;
                        input.close();
                        scan.close();
                        p.destroy();
                    } catch (IOException e) {
                        /* @TODO error exception*/
                    }
                }
            }
        }, TimeUnit.SECONDS.toMillis(15), TimeUnit.SECONDS.toMillis(15));
    }
}
