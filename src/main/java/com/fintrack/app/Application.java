package com.fintrack.app;

import com.fintrack.ui.MainFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public final class Application {
    private Application() { }

    public static void launch() {
        try {
            AppContext context = new AppContext();
            SwingUtilities.invokeLater(() -> new MainFrame(context).setVisible(true));
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                    null,
                    "FinTrack could not start. Check the database setup and console output.\n" + ex.getMessage(),
                    "Startup error",
                    JOptionPane.ERROR_MESSAGE));
        }
    }
}
