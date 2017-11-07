package eli.per.thread;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import eli.per.testlistview.ConnectActivity;

public class TestCommand {

    private static final String TAG = "TestCommand";
    private Socket clientSocket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private String msg = "";
    private int valueLength = 32;

    ConnectActivity.UIHandler uiHandler;

    public TestCommand(ConnectActivity.UIHandler uiHandler) {
        this.uiHandler = uiHandler;
    }

    public boolean isConnected() {
        if (clientSocket == null)
            return false;
        return clientSocket.isConnected();
    }

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
     * 建立连接，验证
     *
     * @param host
     * @param port
     */
    public void connect(final String host, final int port, final int commandData) {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            clientSocket = new Socket(host, port);
                            msg = "Connect To " + host + ":" + port;
                            Log.i(TAG, msg);
                            postMsg(msg);

                            inputStream = new DataInputStream(clientSocket.getInputStream());
                            outputStream = new DataOutputStream(clientSocket.getOutputStream());

                            byte buffers[];

                            //发送数据
                            outputStream.write(234);

                            //接收验证数据
                            buffers = readBytes();
                            String value = new String(buffers, 0, valueLength);
                            //转为long
                            long verifyData = Long.parseLong(value);
                            msg = "Get Verify Data: " + verifyData;
                            Log.i(TAG, msg);
                            postMsg(msg);

                            //乘以3
                            verifyData = verifyData * 3;
                            //发送验证数据
                            outputStream.write((verifyData + "").getBytes());
                            msg = "Send Verify Data: " + verifyData;
                            Log.i(TAG, msg);
                            postMsg(msg);

                            //接收验证结果
                            buffers = readBytes();
                            if (buffers != null) {
                                msg = "Get Msg: " + new String(buffers);
                                Log.i(TAG, msg);
                                postMsg(msg);
                            }

                            //发送控制命令
                            outputStream.write(intToByteArray(commandData));
                            msg = "Send Command Data: " + value;
                            Log.i(TAG, msg);
                            postMsg(msg);

                            //结束
                            if (inputStream != null)
                                inputStream.close();
                            if (outputStream != null)
                                outputStream.close();
                            if (clientSocket != null)
                                clientSocket.close();
                            Log.i(TAG, "Closed...");
                            postMsg("Closed...\n------------------------");
                        } catch (Exception e) {
                            Log.e(TAG, "", e);
                            postMsg("Connect Fail.");
                        }
                    }
                }
        ).start();
    }

    /**
     * 将Int型数据转为字节数组
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

    /**
     * 读取字节数据
     * @return
     */
    /**
     * Read Bytes
     *
     * @return
     */
    private byte[] readBytes() {
        byte buffer[] = new byte[10240];
        byte data[] = null;
        try {
            int length = inputStream.read(buffer);
            data = new byte[length];
            System.arraycopy(buffer, 0, data, 0, length);
            for (int i = 0; i < data.length; i++) {
                Log.i(TAG, "readBytes: " + data[i]);
                if (data[i] <= 0) {
                    valueLength = i;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return data;
        }
    }
}