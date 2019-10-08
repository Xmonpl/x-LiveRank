package cf.xmon.liverank.utils;

import javax.swing.*;

/**
 * @author Xmon
 * @version 1.0
 */

public class DialogBoxUtil {
    public static void infoBox(String infoMessage, String titleBar)
    {
        JOptionPane.showMessageDialog(null, infoMessage, titleBar, JOptionPane.INFORMATION_MESSAGE);
    }
    public static void errorBox(String infoMessage, String titleBar)
    {
        JOptionPane.showMessageDialog(null, infoMessage, titleBar, JOptionPane.ERROR_MESSAGE);
    }
}
