package cloud_netstore.server;

import javax.swing.*;

public class MainServer {

    public final static int PORT = 7411;

    public static void main(String[] args) throws Exception {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }
        ServerMainForm serverMainForm = new ServerMainForm();
        serverMainForm.setContentPane(serverMainForm.getPanelMain());
        serverMainForm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        serverMainForm.setSize(350,400);
        serverMainForm.setVisible(true);
        serverMainForm.setTitle("Server application");
        serverMainForm.pack();

    }
}
