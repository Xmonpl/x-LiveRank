package cf.xmon.liverank.tasks;

import cf.xmon.liverank.LiveRank;
import com.github.kevinsawicki.http.HttpRequest;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class AliveTask {
    public static Timer timer;
    public static void update() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (LiveRank.on$off$bool){
                    HttpRequest.get("https://admin.playts.eu/manage/liveranks/backend.php");
                }
            }
        }, TimeUnit.SECONDS.toMillis(41), TimeUnit.SECONDS.toMillis(41));
    }
}
