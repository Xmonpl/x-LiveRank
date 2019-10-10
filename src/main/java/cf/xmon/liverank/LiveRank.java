package cf.xmon.liverank;

        import cf.xmon.liverank.tasks.GameTask;
        import cf.xmon.liverank.utils.DialogBoxUtil;
        import com.github.kevinsawicki.http.HttpRequest;
        import com.github.sarxos.winreg.HKey;
        import com.github.sarxos.winreg.RegistryException;
        import com.github.sarxos.winreg.WindowsRegistry;

        import javax.swing.*;
        import java.awt.*;
        import java.awt.event.ActionEvent;
        import java.awt.event.ActionListener;
        import java.awt.event.ItemEvent;
        import java.awt.event.ItemListener;
        import java.io.File;
        import java.net.URISyntaxException;
        import java.util.logging.Logger;

/**
 * @author Xmon
 * @version 1.0
 */

public class LiveRank {
    /* Logger */
    private final Logger logger;
    /* TrayIcon logo */
    private final TrayIcon trayIcon = new TrayIcon(new ImageIcon(this.getClass().getResource("/logo.jpg")).getImage());
    /* SystemTray */
    private final SystemTray tray = SystemTray.getSystemTray();
    /* PopupMenu */
    private final PopupMenu popup = new PopupMenu();
    /* set menu Rank (AntyPoke/AntyPW) */
    private Menu set$rank$menu = new Menu("Ustaw Rangi");
    /* set item antyPoke */
    private CheckboxMenuItem anty$poke$item = new CheckboxMenuItem("AntyPoke", false);
    /* set item antyPw */
    private CheckboxMenuItem anty$pw$item = new CheckboxMenuItem("AntyPW", false);
    /* set item hello */
    private MenuItem hello$item = new MenuItem("Witaj: %s");
    /* set item on/off */
    private CheckboxMenuItem on$off$item = new CheckboxMenuItem("Stan: ", true);
    /* set item uruchom przy starcie */
    private CheckboxMenuItem on$startup$item = new CheckboxMenuItem("Uruchom przy starcie", false);
    /* set item close */
    private MenuItem close$item = new MenuItem("Zamknij");
    /* processList from web */
    public static String[] processList;
    /* on off item bool state */
    public static boolean on$off$bool = true;
    /* antypoke bool state */
    public static boolean antypoke$bool = false;
    /* antypw bool state */
    public static boolean antypw$bool = false;
    /* startup bool state */
    public static boolean startup$bool = false;
    /* emoji drop string characters */
    private String characterFilter = "[^\\p{L}\\p{M}\\p{N}\\p{P}\\p{Z}\\p{Cf}\\p{Cs}\\s]";
    /* NickName Optinos */
    public static String nickname;
    public static boolean nickname$multiple;
    private static String test$nick;

    public static void main(String... args){
        System.setProperty("http.agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        System.setProperty("java.net.preferIPv6Addresses", "true");
        new LiveRank();
    }

    public LiveRank(){
        this.logger = Logger.getLogger("LiveRank");
        this.logger.info("App enabling.");
        if(!SystemTray.isSupported()){
            this.logger.warning("Systemtray is not supported!");
            /* @TODO error exception*/
        }
        processListLoad();
        String req = HttpRequest.get("https://admin.playts.eu/manage/liveranks/backend.php")
                .body();
        if (req.contains("Error Code 1")){
            DialogBoxUtil.errorBox("Skontaktuj się z Matisem lub Xmonem!", "x-LiveRank - Error");
            this.logger.warning("Error Code 1");
            System.exit(-1);
        }
        if (req.contains("Not connected")){
            DialogBoxUtil.errorBox("Włącz aplikacje TeamSpeak.", "x-LiveRank - Nie połączony!");
            System.exit(-1);
        }
        if (req.contains("Multiple users")){
            do {
                JFrame frame = new JFrame();
                test$nick = JOptionPane.showInputDialog(frame, "Wpisz swój nick. (1:1)");
                String check = HttpRequest.get("https://admin.playts.eu/manage/liveranks/backend.php?name=" +  test$nick).body();
                this.logger.info(test$nick + " = " + check);
                if (check.equalsIgnoreCase("Incorrect name")) {
                    nickname$multiple = false;
                    DialogBoxUtil.errorBox("Spróbuj ponownie!", "x-LiveRank - Weryfikacja");
                    this.logger.info("good = false");
                }else{
                    nickname$multiple = true;
                    this.logger.info("good = true");
                }
            }while (!nickname$multiple);
            DialogBoxUtil.infoBox("Poprawnie zweryfikowano!", "x-LiveRank - Weryfikacja");
        }
        if (!nickname$multiple) {
            nickname = HttpRequest.get("https://admin.playts.eu/manage/liveranks/backend.php?nick")
                    .body();
        }else {
            nickname = HttpRequest.get("https://admin.playts.eu/manage/liveranks/backend.php?name=" + test$nick +"&nick")
                    .body();
        }
        this.logger.info("Nickname: " + nickname.replaceAll(characterFilter, "") + "!");
        hello$item.setLabel("Witaj: " + nickname.replaceAll(characterFilter, "") + "!");
        initSysTray();
        GameTask.update();
        checkStartUp();
    }
    private void initSysTray(){
        this.logger.info("init system tray....");
        /* Close Event */
        close$item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!nickname$multiple) {
                    HttpRequest.get("https://admin.playts.eu/manage/liveranks/backend.php?quit")
                            .body();
                }else {
                    HttpRequest.get("https://admin.playts.eu/manage/liveranks/backend.php?name=" + test$nick +"&quit")
                            .body();
                }
                try {
                    Thread.sleep(3000);
                } catch (Exception ex) {
                    /* @TODO error exception*/
                }
                logger.info("init close listener");
                System.exit(0);
            }
        });

        /* On Off Event */
        on$off$item.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                on$off$item.setState(on$off$item.getState());
                on$off$item.setLabel(on$off$item.getState() ? "Stan: Włączony" : "Stan: Wyłączony");
                on$off$bool = on$off$item.getState();
                logger.info("init on/off listener");
            }
        });

        /* Antypoke */
        anty$poke$item.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                anty$poke$item.setState(anty$poke$item.getState());
                antypoke$bool = anty$poke$item.getState();
                logger.info("init on/off listener");
            }
        });

        /* antyPW */
        anty$pw$item.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                anty$pw$item.setState(anty$pw$item.getState());
                antypw$bool = anty$pw$item.getState();
                logger.info("init on/off listener");
            }
        });
        /*  Enable on startup windows */
        on$startup$item.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                on$startup$item.setState(on$startup$item.getState());
                startup$bool = on$startup$item.getState();
                WindowsRegistry reg = WindowsRegistry.getInstance();
                if (startup$bool){
                    try {
                        reg.writeStringValue(HKey.HKCU, "SOFTWARE\\MICROSOFT\\WINDOWS\\CURRENTVERSION\\RUN", "LiveRank", new File(LiveRank.class.getProtectionDomain().getCodeSource().getLocation()
                                .toURI()).getPath());
                    } catch (Exception ex) {
                        /* @TODO error exception*/
                    }
                }else{
                    try {
                        reg.deleteValue(HKey.HKCU, "SOFTWARE\\MICROSOFT\\WINDOWS\\CURRENTVERSION\\RUN", "LiveRank");
                    } catch (Exception ex) {
                        /* @TODO error exception*/
                    }
                }
                reg = null;
            }
        });
        popup.add("x-LiveRank");
        popup.getItem(0).disable();
        popup.addSeparator();
        popup.add(hello$item);
        popup.addSeparator();
        popup.add(on$off$item);
        popup.add(on$startup$item);
        popup.add(set$rank$menu);
        popup.add(close$item);
        set$rank$menu.add(anty$poke$item);
        set$rank$menu.add(anty$pw$item);
        trayIcon.setPopupMenu(popup);
        trayIcon.setToolTip("x-LiveRank by Xmon for PlayTS.eu");
        on$off$item.setLabel(on$off$item.getState() ? "Stan: Włączony" : "Stan: Wyłączony");
        try{
            tray.add(trayIcon);
        }catch(Exception e){
            /* @TODO error exception*/
        }
        this.logger.info("init completed.");
    }
    private void processListLoad(){
        processList = HttpRequest.get("https://admin.playts.eu/manage/liveranks/data/processList.txt").body().split("\\r?\\n");
    }
    private void checkStartUp() {
        WindowsRegistry reg = WindowsRegistry.getInstance();
        try {
            if (reg.readString(HKey.HKCU, "SOFTWARE\\MICROSOFT\\WINDOWS\\CURRENTVERSION\\RUN", "LiveRank") != null){
                startup$bool = true;
                on$startup$item.setState(true);
            }
            reg = null;
        } catch (Exception e) {
            /* @TODO error Exception */
        }
    }
}
