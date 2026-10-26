package com.fintrack.app;

import com.fintrack.auth.UserStore;
import com.fintrack.ui.LoginFrame;
import com.fintrack.ui.MainFrame;
import com.fintrack.ui.components.AppTheme;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/** Application entry flow: sign-in, user-specific workspace, and logout. */
public final class Application {
    private Application() { }

    public static void launch() {
        AppTheme.install();
        SwingUtilities.invokeLater(Application::showLogin);
    }

    private static void showLogin() {
        try {
            UserStore users = new UserStore();
            LoginFrame login = new LoginFrame(users, Application::openWorkspace);
            login.setVisible(true);
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "FinTrack could not initialize local user storage.\n" + ex.getMessage(),
                    "Startup error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void openWorkspace(String username) {
        try {
            AppContext context = new AppContext(username);
            MainFrame mainFrame = new MainFrame(context, username, Application::showLogin);
            mainFrame.setVisible(true);
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "FinTrack could not open this user's workspace.\n" + ex.getMessage(),
                    "Workspace error", JOptionPane.ERROR_MESSAGE);
            showLogin();
        }
    }
}
