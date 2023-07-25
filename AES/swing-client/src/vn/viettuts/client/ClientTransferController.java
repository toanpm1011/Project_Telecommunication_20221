package vn.viettuts.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import javax.swing.JOptionPane;



public class ClientTransferController implements ActionListener {
    private ClientTransferView view;

    public ClientTransferController(ClientTransferView view) {
        this.view = view;
        view.getBtnBrowse().addActionListener(this);
        view.getBtnSendFile().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(view.getBtnBrowse().getText())) {
            view.chooseFile();
        }
        if (e.getActionCommand().equals(view.getBtnSendFile().getText())) {
            String host = view.getTextFieldHost().getText().trim();
            int port = Integer.parseInt(view.getTextFieldPort().getText().trim());
            String sourceFilePath = view.getTextFieldFilePath().getText();
            
            /* 
            //encrypt file
            File inputFile = new File(sourceFilePath);
            String key = "Test key ";
            File encryptedFile = new File();
            
            try{
                CryptoUtils.encrypt(key,inputFile,encryptedFile);
            } catch (CryptoException ex) {
                System.out.println(ex.getMessage());
                ex.printStackTrace();
            }*/

            if (host != "" && sourceFilePath != "") {
                String destinationDir = "D:\\server\\"; // định nghĩa thư mục đích trên server
                TCPClient tcpClient = new TCPClient(host, port, view.getTextAreaResult());
                tcpClient.connectServer();
                try {
                    tcpClient.sendFile(sourceFilePath, destinationDir);
                    
                } catch (CryptoException e1) {
                e1.printStackTrace();
                }
                    
                tcpClient.closeSocket();
            } else {
                JOptionPane.showMessageDialog(view, "Host, Port và FilePath phải khác rỗng.");
            }
        }
    }

    
}
