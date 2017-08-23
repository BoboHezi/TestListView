package eli.per.thread;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

public class CommandThread extends Thread {

    private static final String TAG = "CommandThread";
    private final int port = 15231;
    private final String host = "192.168.2.115";
    private Socket clientSocket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    private String msg = "";

    @Override
    public void run() {
        try {
            clientSocket = new Socket(host, port);
            Log.i(TAG, "Connect To " + host + ":" + port);
            inputStream = new DataInputStream(clientSocket.getInputStream());
            outputStream = new DataOutputStream(clientSocket.getOutputStream());

            byte buffers[] = null;

            //接收验证数据
            buffers = readBytes();
            long verifyData = bytesToLong(buffers);
            Log.i(TAG, "Get Verify Data: " + verifyData);
            //乘以3
            verifyData = verifyData * 3;
            //发送验证数据
            outputStream.write(longToBytes(verifyData));
            Log.i(TAG, "Send Verify Data: " + verifyData);

            //接收验证结果
            buffers = readBytes();
            msg = new String(buffers);
            Log.i(TAG, "Get Msg: " + msg);
            //发送控制命令
            outputStream.write(longToBytes(854));
            Log.i(TAG, "Send Command Data: " + 854);
            //接收消息
            buffers = readBytes();
            msg = new String(buffers);
            Log.i(TAG, "Get Msg: " + msg);
            //结束
            inputStream.close();
            outputStream.close();
            clientSocket.close();
            Log.i(TAG, "Closed...");
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {

        }
    }

    /**
     * 读取字节数据
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
            Log.e(TAG, "", e);
        } finally {
            return data;
        }
    }

    private long bytesToLong(byte bytes[]) {
        long result = 0;
        int number = 1;
        for (int i = bytes.length - 1; i >= 0; i--) {
            if (bytes[i] < 48 || bytes[i] > 57) {
                return -1;
            }
            result = result + (bytes[i] - 48) * number;
            number *= 10;
        }
        return result;
    }

    private byte[] longToBytes(long values) {
        byte result[] = new byte[512];
        int length = 0;
        while (values >= 1) {
            result[length] = (byte) (values % 10 + 48);
            values /= 10;
            length++;
        }

        for (int i = 0; i < length / 2; i++) {
            byte value = result[i];
            result[i] = result[length - i - 1];
            result[length - i - 1] = value;
        }

        return result;
    }
}