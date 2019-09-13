package cf.xmon.liverank;

import com.github.kevinsawicki.http.HttpRequest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private CheckboxMenuItem on$off$item = new CheckboxMenuItem("Stan: włączony/wyłączony", false);
    /* set item uruchom przy starcie */
    private CheckboxMenuItem on$startup$item = new CheckboxMenuItem("Uruchom przy starcie", false);
    /* set item close */
    private MenuItem close$item = new MenuItem("Zamknij");

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
            /* @TODO error */
        }
        String req = HttpRequest.get("https://admin.playts.eu/manage/liveranks/backend.php")
                .body();
        if (req.contains("Not connected")){
            errorBox("Włącz aplikacje TeamSpeak.", "x-LiveRank - Nie połączony!");
            System.exit(-1);
        }

        String nick = HttpRequest.get("https://admin.playts.eu/manage/liveranks/backend.php?nick")
                .body();
        this.logger.info("Nickname: " + nick);
        hello$item.setLabel("Witaj: " + nick);
        initSysTray();

        /* Close Event */
        close$item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

    }
    private void initSysTray(){
        this.logger.info("init system tray....");
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
        try{
            tray.add(trayIcon);
        }catch(Exception e){
            /* @TODO error */
        }
        this.logger.info("init completed.");
    }
    public static void infoBox(String infoMessage, String titleBar)
    {
        JOptionPane.showMessageDialog(null, infoMessage, titleBar, JOptionPane.INFORMATION_MESSAGE);
    }
    public static void errorBox(String infoMessage, String titleBar)
    {
        JOptionPane.showMessageDialog(null, infoMessage, titleBar, JOptionPane.ERROR_MESSAGE);
    }
}
