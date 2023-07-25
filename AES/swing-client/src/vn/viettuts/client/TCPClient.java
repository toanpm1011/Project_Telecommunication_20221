package vn.viettuts.client;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;


import javax.swing.JTextArea;

import vn.viettuts.common.FileInfo;

public class TCPClient {
    // create Socket object
    private Socket client;
    private String host;
    private int port;
    private JTextArea textAreaLog;

    public TCPClient(String host, int port, JTextArea textAreaLog) {
        this.host = host;
        this.port = port;
        this.textAreaLog = textAreaLog;
    }
    
    /**
     * connect to server
     * 
     * @author viettuts.vn
     */
    public void connectServer() {
        try {
            client = new Socket(host, port);
            textAreaLog.append("connected to server.\n");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * send file to server
     * 
     * @param sourceFilePath
     * @param destinationDir
     * @throws CryptoException
     */
    public void sendFile(String sourceFilePath, String destinationDir) throws CryptoException {
        DataOutputStream outToServer = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;

        try {
            // make greeting
            outToServer = new DataOutputStream(client.getOutputStream());
            outToServer.writeUTF("Hello from " + client.getLocalSocketAddress());

            // get file info
            FileInfo fileInfo = getFileInfo(sourceFilePath, destinationDir);
            recoverFileInfo(sourceFilePath, destinationDir);
            // send file
            oos = new ObjectOutputStream(client.getOutputStream());
            oos.writeObject(fileInfo);

            // get confirmation
            ois = new ObjectInputStream(client.getInputStream());
            fileInfo = (FileInfo) ois.readObject();
            if (fileInfo != null) {
                textAreaLog.append("send file to server " + fileInfo.getStatus() + "\n");
            }
            //

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            // close all stream
            closeStream(oos);
            closeStream(ois);
            closeStream(outToServer);
        }
        
    }

    /**
     * get source file info
     * 
     * @author viettuts.vn
     * @param sourceFilePath
     * @param destinationDir
     * @return FileInfo
     * @throws CryptoException
     */
    private FileInfo getFileInfo(String sourceFilePath, String destinationDir) throws CryptoException {
        FileInfo fileInfo = null;
        BufferedInputStream bis = null;
        try {
            File sourceFile = new File(sourceFilePath);
            File encrytedFile = new File(sourceFilePath);

            CryptoUtils.encrypt("key test for aes", sourceFile, encrytedFile);
        

            bis = new BufferedInputStream(new FileInputStream(encrytedFile));
            fileInfo = new FileInfo();
            byte[] fileBytes = new byte[(int) encrytedFile.length()];
            // get file info
            bis.read(fileBytes, 0, fileBytes.length);
            fileInfo.setFilename(sourceFile.getName());
            fileInfo.setDataBytes(fileBytes);
            fileInfo.setDestinationDirectory(destinationDir);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            closeStream(bis);
        }
        return fileInfo;
    }
    public FileInfo recoverFileInfo(String sourceFilePath, String destinationDir) throws CryptoException {
        FileInfo fileInfo = null;
        BufferedInputStream bis = null;
        try {
            File sourceFile = new File(sourceFilePath);
            File decrytedFile = new File(sourceFilePath);

            CryptoUtils.decrypt("key test for aes", sourceFile, decrytedFile);
        

            bis = new BufferedInputStream(new FileInputStream(decrytedFile));
            fileInfo = new FileInfo();
            byte[] fileBytes = new byte[(int) decrytedFile.length()];
            // get file info
            bis.read(fileBytes, 0, fileBytes.length);
            fileInfo.setFilename(sourceFile.getName());
            fileInfo.setDataBytes(fileBytes);
            fileInfo.setDestinationDirectory(destinationDir);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStream(bis);
        }
        return fileInfo;
    }
    /**
     * close socket
     * 
     * @author viettuts.vn
     */
    public void closeSocket() {
        try {
            if (client != null) {
                client.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * close input stream
     * 
     * @author viettuts.vn
     */
    public void closeStream(InputStream inputStream) {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * close output stream
     * 
     * @author viettuts.vn
     */
    public void closeStream(OutputStream outputStream) {
        try {
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
    