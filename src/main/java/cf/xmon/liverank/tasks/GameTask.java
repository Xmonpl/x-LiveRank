package cf.xmon.liverank.tasks;

import cf.xmon.liverank.LiveRank;
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

public class GameTask {
    public static Timer timer;
    private static String s = "";

    public static void update() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (LiveRank.on$off$bool){
                    try {
                        Process p = Runtime.getRuntime().exec("tasklist");
                        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
                        Scanner scan = new Scanner(input);
                        while (scan.hasNextLine()) {
                            s += scan.nextLine();
                        }
                        Arrays.stream(LiveRank.processList).forEach(process ->{
                            if (s.contains(process)){
                                HttpRequest.get("https://admin.playts.eu/manage/liveranks/backend.php?play=" + process);
                                System.out.println("HttpReq " + process);
                            }
                        });
                        s = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, TimeUnit.SECONDS.toMillis(10), TimeUnit.SECONDS.toMillis(10));
    }
}
