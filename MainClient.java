package cloud_netstore.client;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class MainClient {

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
        ClientMainForm clientMainForm = new ClientMainForm();
        clientMainForm.setContentPane(clientMainForm.getMainPane());
        clientMainForm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        clientMainForm.setSize(550,600);
        clientMainForm.setVisible(true);
        clientMainForm.setTitle("Client application");
        clientMainForm.pack();

    }

}
