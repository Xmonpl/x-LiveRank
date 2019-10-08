package cf.xmon.liverank.test;

import com.github.kevinsawicki.http.HttpRequest;

import java.util.Arrays;

public class ProcessListTest {
    private static String[] processList;
    public static void main(String... args){
        System.setProperty("http.agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        processListLoad();
    }
    private static void processListLoad(){
        String body = HttpRequest.get("https://admin.playts.eu/manage/liveranks/data/processList.txt").body();
        //System.out.println(body);
        processList = body.split("\\n");
        Arrays.stream(processList).forEach(System.out::println);
    }
}
