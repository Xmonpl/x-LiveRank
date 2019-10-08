package cf.xmon.liverank.tasks;

import cf.xmon.liverank.LiveRank;
import cf.xmon.liverank.utils.ReqUtil;
import com.github.kevinsawicki.http.HttpRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

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
                            logger.info("Alive Task " + HttpRequest.get("https://admin.playts.eu/manage/liveranks/backend.php").body());
                            alive = 0;
                        }
                        s = null;
                        input.close();
                        scan.close();
                    } catch (IOException e) {
                        /* @TODO error exception*/
                    }
                }
            }
        }, TimeUnit.SECONDS.toMillis(15), TimeUnit.SECONDS.toMillis(15));
    }
}
