package eli.per.thread;

import android.os.Bundle;
import android.os.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import eli.per.testlistview.ConnectActivity;

/**
 * 控制数据发送类
 */
public class CommandServer {

    private static final String TAG = "CommandServer";
    private final String host = "192.168.2.200";
    private final int port = 15231;
    private Socket clientSocket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private ConnectActivity.UIHandler uiHandler;

    public CommandServer(ConnectActivity.UIHandler uiHandler) {
        this.uiHandler = uiHandler;
    }

    /**
     * 更新信息
     *
     * @param message
     */
    private void postMsg(String message) {
        if (uiHandler == null)
            return;

        Message msg = uiHandler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString("info", message);
        msg.setData(bundle);
        msg.arg1 = 0;
        uiHandler.sendMessage(msg);
    }

    /**
     * 连接到Server
     */
    public void connect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    postMsg("try to connect server...");
                    clientSocket = new Socket();
                    clientSocket.connect(new InetSocketAddress(host, port), 3000);
                    postMsg("connect to server...");

                    inputStream = new DataInputStream(clientSocket.getInputStream());
                    outputStream = new DataOutputStream(clientSocket.getOutputStream());

                    verify();
                } catch (Exception e) {
                    postMsg(e.getMessage());
                }
            }
        }).start();
    }

    /**
     * 是否处于连接状态
     *
     * @return
     */
    public boolean isConnected() {
        if (clientSocket == null)
            return false;
        return clientSocket.isConnected();
    }

    /**
     * 验证设备
     */
    private boolean verify() {
        try {
            byte buffers[];

            //receive verify data
            buffers = readBytes();
            //parse to long type
            int verifyData = bytesToInt(buffers);
            postMsg("receive verify data: " + verifyData);

            //3 times the data
            verifyData = verifyData * 3;
            //send verify data
            outputStream.write(intToByteArray(verifyData));
            postMsg("send verify data: " + verifyData);

            //receive verify result
            buffers = readBytes();
            postMsg(new String(buffers));
            outputStream.writeInt(0);
            return true;
        } catch (Exception e) {
            postMsg("verify failed");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 发送控制数据
     */
    public void sendData(final int value) {
        if (!isConnected())
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //send command data
                    outputStream.write(intToByteArray(value));
                    postMsg("send command data: " + value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        try {
            if (inputStream != null)
                inputStream.close();
            if (outputStream != null)
                outputStream.close();
            if (clientSocket != null)
                clientSocket.close();
            postMsg("disconnected");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取字节数组
     *
     * @return
     */
    public byte[] readBytes() {
        byte buffer[] = new byte[10240];
        byte data[] = null;
        try {
            int length = inputStream.read(buffer);
            data = new byte[length];
            System.arraycopy(buffer, 0, data, 0, length);
        } catch (IOException e) {
        } finally {
            return data;
        }
    }

    /**
     * 将字节数组转为整形
     *
     * @return
     */
    private int bytesToInt(byte bytes[]) {
        int time = 1;
        int result = 0;
        for (int i = bytes.length - 1; i >= 0; i--) {
            short temp = bytes[i];
            if (temp < 0) {
                temp = (short) (temp + 256);
            }
            result += temp * time;
            time *= 256;
        }
        return result;
    }

    /**
     * int to array
     *
     * @param value
     * @return
     */
    private byte[] intToByteArray(int value) {
        byte[] result = new byte[4];
        //由高位到低位
        result[0] = (byte) ((value >> 24) & 0xFF);
        result[1] = (byte) ((value >> 16) & 0xFF);
        result[2] = (byte) ((value >> 8) & 0xFF);
        result[3] = (byte) (value & 0xFF);
        return result;
    }
}
