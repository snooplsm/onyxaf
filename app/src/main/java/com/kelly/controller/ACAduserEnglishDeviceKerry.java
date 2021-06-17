package com.kelly.controller;

import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import androidx.core.internal.view.SupportMenu;
import us.wmwm.onyx.bluetooth.BluetoothManager;

public class ACAduserEnglishDeviceKerry {
    public static final int KELLY_DEV_PRODUCT = 24577;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final int STATE_CONNECTED = 3;
    private static final int STATE_NONE = 0;
    public static final int baudRate = 19200;
    public static final int dataBits = 8;
    private static ACAduserEnglishDeviceKerry instance = new ACAduserEnglishDeviceKerry();
    public static final int parity = 0;
    public static final int stopBits = 1;
    public int COMType = -1;
    public byte ETS_RX_BYTES = (byte) 0;
    public byte ETS_RX_CMD = (byte) 0;
    public byte[] ETS_RX_DATA = new byte[16];
    private boolean ETS_Rx_Error = false;
    private boolean ETS_Rx_Status = true;
    public byte ETS_TX_BYTES = (byte) 0;
    public byte ETS_TX_CMD = (byte) 0;
    public byte[] ETS_TX_DATA = new byte[16];
    private int Timeout = 0;
    private boolean bReadTheadEnable = false;
    private int bluetooth_mState = 0;
    private UsbDeviceConnection conn;
    private UsbEndpoint endPointIn;
    private UsbEndpoint endPointOut;
    private UsbInterface intf;
    public StringBuffer logTxtSb = new StringBuffer();
    private BluetoothAdapter mAdapter;
    private BluetoothDevice mmDevice;
    private InputStream mmInStream;
    private OutputStream mmOutStream;
    public BluetoothSocket mmSocket;
    public int readIndex;
    public byte[] readInfo = new byte[32];
    private int readTimeOut = 45;
    private int sendTimeOut = 45;
    private byte[] tempInfo = new byte[32];
    private long[] time = new long[32];
    private int[] MonitorDataValue = new int[50];
    private Map<Integer, Integer> monitorMap = new HashMap<>();
    private int[] MonitorDataValueBackup = new int[50];
    private String[] MonitorshowStr = new String[48];

    public class BluetoothReadThread extends Thread {
        public void run() {
            ACAduserEnglishDeviceKerry.this.readIndex = 0;
            ACAduserEnglishDeviceKerry.this.ETS_Rx_Error = false;
            ACAduserEnglishDeviceKerry.this.bReadTheadEnable = true;
            while (ACAduserEnglishDeviceKerry.this.bReadTheadEnable) {
                if (ACAduserEnglishDeviceKerry.this.bluetooth_mState == 3) {
                    try {
                        System.out.println("BluetoothReadThread");
                        int readcount = ACAduserEnglishDeviceKerry.this.mmInStream.read(ACAduserEnglishDeviceKerry.this.tempInfo);
                        System.out.println("READ " + readcount + " " + Arrays.toString(tempInfo));
                        if (readcount > 0) {
                            for (int i = 0; i < readcount; i++) {
                                ACAduserEnglishDeviceKerry.this.readInfo[ACAduserEnglishDeviceKerry.this.readIndex] = ACAduserEnglishDeviceKerry.this.tempInfo[i];
                                ACAduserEnglishDeviceKerry aCAduserEnglishDeviceKerry = ACAduserEnglishDeviceKerry.this;
                                aCAduserEnglishDeviceKerry.readIndex = aCAduserEnglishDeviceKerry.readIndex + 1;
                            }
                        }
                        if (ACAduserEnglishDeviceKerry.this.readInfo[1] > (byte) 16) {
                            ACAduserEnglishDeviceKerry.this.readInfo[1] = (byte) 16;
                        }
                        byte[] readInfo = ACAduserEnglishDeviceKerry.this.readInfo;
                        int readIndex = ACAduserEnglishDeviceKerry.this.readIndex;
                        int readInfo1 = readInfo[1];
                        int readInfo1plus2 = readInfo1+2;
                        int checkNumber = ACAduserEnglishDeviceKerry.calculateCheckNumber(readInfo, 0, readInfo1 + 2);
                        int readInfoPlus2 = ACAduserEnglishDeviceKerry.this.readInfo[readInfo1plus2];
                        if (readIndex >= readInfo1 + 3 && checkNumber == readInfoPlus2) {
                            ACAduserEnglishDeviceKerry.this.ETS_Rx_Status = true;
                            ACAduserEnglishDeviceKerry.this.readIndex = 0;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public class ReadThread extends Thread {
        public void run() {
            System.out.println("ReadThread");
            ACAduserEnglishDeviceKerry.this.readIndex = 0;
            ACAduserEnglishDeviceKerry.this.ETS_Rx_Error = false;
            ACAduserEnglishDeviceKerry.this.bReadTheadEnable = true;
            while (ACAduserEnglishDeviceKerry.this.bReadTheadEnable) {
                if (ACAduserEnglishDeviceKerry.this.conn != null) {
                    try {
                        int readcount = ACAduserEnglishDeviceKerry.this.conn.bulkTransfer(ACAduserEnglishDeviceKerry.this.endPointIn, ACAduserEnglishDeviceKerry.this.tempInfo, 32, 100);
                        if (readcount > 2) {
                            System.arraycopy(ACAduserEnglishDeviceKerry.this.tempInfo, 2, ACAduserEnglishDeviceKerry.this.readInfo, ACAduserEnglishDeviceKerry.this.readIndex, readcount - 2);
                            ACAduserEnglishDeviceKerry.this.readIndex = (ACAduserEnglishDeviceKerry.this.readIndex + readcount) - 2;
                        }
                        if (ACAduserEnglishDeviceKerry.this.readInfo[1] > (byte) 16) {
                            ACAduserEnglishDeviceKerry.this.readInfo[1] = (byte) 16;
                        }
                        if (ACAduserEnglishDeviceKerry.this.readIndex >= ACAduserEnglishDeviceKerry.this.readInfo[1] + 3 && ACAduserEnglishDeviceKerry.calculateCheckNumber(ACAduserEnglishDeviceKerry.this.readInfo, 0, ACAduserEnglishDeviceKerry.this.readInfo[1] + 2) == ACAduserEnglishDeviceKerry.this.readInfo[ACAduserEnglishDeviceKerry.this.readInfo[1] + 2]) {
                            ACAduserEnglishDeviceKerry.this.ETS_Rx_Status = true;
                            ACAduserEnglishDeviceKerry.this.readIndex = 0;
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    public void setSocket(BluetoothSocket socket) throws IOException {
        this.mmSocket = socket;
        this.mmInStream = socket.getInputStream();
        this.mmOutStream = socket.getOutputStream();
        COMType = 1;
        bluetooth_mState = socket.isConnected()?3:0;

    }

    public static ACAduserEnglishDeviceKerry getInstance() {
        return instance;
    }

    private ACAduserEnglishDeviceKerry() {
    }

    public void resetLogBuffer() {
        this.logTxtSb.delete(0, this.logTxtSb.length());
    }

    public String getLogBufferInfo() {
        String logTxt = this.logTxtSb.toString();
        resetLogBuffer();
        return logTxt;
    }

    public StringBuffer getlogTxtSb() {
        return this.logTxtSb;
    }

    public void closeReadThead() {
        this.bReadTheadEnable = false;
    }

    public boolean openUsbComDevice(Context context) {
        UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        UsbDevice usbComDev = null;
        Map<String,UsbDevice> usbDevMap = usbManager.getDeviceList();
        for (String keyStr : usbDevMap.keySet()) {
            UsbDevice usbDev = usbDevMap.get(keyStr);
            if (usbManager.hasPermission(usbDev)) {
                if (usbDev.getProductId() == KELLY_DEV_PRODUCT) {
                    usbComDev = usbDev;
                    break;
                }
            }
            usbManager.requestPermission(usbDev, PendingIntent.getBroadcast(context, 6902, new Intent(),    6902));
            this.logTxtSb.append("no permission to access device\n");
            return false;
        }
        if (usbComDev == null) {
            this.logTxtSb.append("not find usb device\n");
            return false;
        }
        this.conn = usbManager.openDevice(usbComDev);
        new ACAduserEnglishUsbDevice().configUsbDeviceParamter(this.conn, baudRate, 8, 1, 0);
        this.intf = usbComDev.getInterface(0);
        this.conn.claimInterface(this.intf, true);
        this.endPointIn = this.intf.getEndpoint(0);
        this.endPointOut = this.intf.getEndpoint(1);
        this.logTxtSb.append("open device success\n");
        return true;
    }

    public boolean openBluetoothComDevice(String Mac_address) {
        this.mAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mmDevice = this.mAdapter.getRemoteDevice(Mac_address);
        boolean result = this.mAdapter.cancelDiscovery();
        try {
            this.mmSocket = this.mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            this.mmSocket.connect();
            try {
                this.mmInStream = this.mmSocket.getInputStream();
                this.mmOutStream = this.mmSocket.getOutputStream();
            } catch (IOException e2) {
            }
            if (this.mmSocket.isConnected()) {
                this.bluetooth_mState = 3;
            }
            return true;
        } catch (IOException e3) {
            try {
                this.mmSocket.close();
            } catch (IOException e4) {
            }
            return false;
        }
    }

    public int sendcmd() {
        byte[] sendtmp = new byte[(this.ETS_TX_BYTES + 3)];
        sendtmp[0] = this.ETS_TX_CMD;
        sendtmp[1] = this.ETS_TX_BYTES;
        if (this.ETS_TX_BYTES == (byte) 0) {
            sendtmp[this.ETS_TX_BYTES + 2] = this.ETS_TX_CMD;
        } else {
            for (byte i = (byte) 0; i < this.ETS_TX_BYTES; i++) {
                sendtmp[i + 2] = this.ETS_TX_DATA[i];
            }
            sendtmp[this.ETS_TX_BYTES + 2] = calculateCheckNumber(sendtmp, 0, this.ETS_TX_BYTES + 2);
        }
        String sendCommand = ACAduserEnglishByteUtil.printByteArrayHex(sendtmp, 0, sendtmp.length);
        if (this.COMType == 1) {
            try {
                if (this.bluetooth_mState != 3) {
                    return 0;
                }
                write(sendtmp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                if (this.conn == null) {
                    return 0;
                }
                if (this.conn.bulkTransfer(this.endPointOut, sendtmp, sendtmp.length, 100) <= 0) {
                    return 0;
                }
            } catch (Exception e2) {
            }
        }
        return 1;
    }

    public int readcmd() {
        if (this.COMType == 1) {
            this.Timeout = 300;
        } else {
            this.Timeout = 100;
        }
        this.time[0] = System.currentTimeMillis();
        while (System.currentTimeMillis() - this.time[0] < ((long) this.Timeout)) {
            if (this.ETS_Rx_Status) {
                break;
            }
        }
        if (this.ETS_Rx_Status) {
            this.ETS_Rx_Status = false;
            byte[] buffer = new byte[(this.readInfo[1] + 3)];
            System.arraycopy(this.readInfo, 0, buffer, 0, buffer.length);
            if (buffer[0] != this.ETS_TX_CMD) {
                return 2;
            }
            if (this.ETS_Rx_Error) {
                this.ETS_Rx_Error = false;
                return 3;
            }
            String buffertemp = ACAduserEnglishByteUtil.printByteArrayHex(buffer, 0, buffer.length);
            this.ETS_RX_CMD = this.readInfo[0];
            this.ETS_RX_BYTES = this.readInfo[1];
            if (this.ETS_RX_BYTES <= (byte) 0) {
                return 1;
            }
            for (byte i = (byte) 0; i < this.ETS_RX_BYTES; i++) {
                this.ETS_RX_DATA[i] = this.readInfo[i + 2];
            }
            return 1;
        }
        this.readIndex = 0;
        this.readInfo[1] = (byte) 0;
        return 0;
    }

    public void write(byte[] buffer) {
        try {
            System.out.println("writing " + buffer.length + " " + Arrays.toString(buffer));
            this.mmOutStream.write(buffer);
        } catch (IOException e) {
        }
    }

    public void closeUsbComDevice() {
        try {
            if (this.conn != null) {
                this.conn.releaseInterface(this.intf);
                this.conn.close();
                this.conn = null;
            }
        } catch (Exception e) {
        }
    }

    public void closeBluetoothComDevice() {
        try {
            if (this.mmSocket != null) {
                this.mmSocket.close();
                this.mmSocket = null;
                this.bluetooth_mState = 0;
            }
        } catch (IOException e) {
        }
    }

    public static byte calculateCheckNumber(byte[] cmdInfo, int offset, int length) {
        int readDataSum = 0;
        int startreadIndex = offset;
        for (int i = startreadIndex; i < startreadIndex + length; i++) {
            readDataSum += cmdInfo[i];
        }
        return (byte) readDataSum;
    }

    public Thread readThread() {
        final Thread t;
        if (this.COMType == 1) {
            t = new BluetoothReadThread();
        } else {
            t=new ReadThread();
        }
        t.start();
        return t;
    }

    public MonitorThreader monitorThread(BluetoothManager m) {
        MonitorThreader t = new MonitorThreader(m);
        t.start();
        return t;
    }

    public class MonitorThreader extends Thread {

        BluetoothManager m;
        MonitorThreader(BluetoothManager m) {
            this.m = m;
        }

        boolean paused = false;
        boolean running = true;

        public void run() {
            int Error_count = 0;
            while (!paused) {
                try {
                    if (!paused) {
                        int i;
                        if (!paused) {
                            for (i = 0; i < 3; i++) {
                                if (i == 0) {
                                    try {
                                        ETS_TX_CMD = (byte) (i + 58);
                                    } catch (Exception e) {
                                    }
                                } else {
                                    ETS_TX_CMD = (byte) (i + 58);
                                }
                                ETS_TX_BYTES = (byte) 0;
                                System.out.println("MonitorThread");
                                running = true;
                                sendcmd();
                                int j;
                                int read = readcmd();
                                if (read == 1) {
                                    for (j = 0; j < 16; j++) {
                                        MonitorDataValue[(i * 16) + j] = ETS_RX_DATA[j];
                                        if (MonitorDataValue[(i * 16) + j] < 0) {
                                            int[] access$11 = MonitorDataValue;
                                            int i2 = (i * 16) + j;
                                            access$11[i2] = access$11[i2] + 256;
                                        }
                                    }
                                    Error_count = 0;
                                } else {
                                    Error_count++;
                                    if (Error_count >= 5) {
                                        for (j = 0; j < 48; j++) {
                                            MonitorDataValue[j] = 0;
                                        }
                                        Error_count = 0;
                                    }
                                }
                            }
                        }
                        running = false;
                        for (i = 0; i < ACAduserEnglishSetting.Monitor_Value_Array.length; i++) {
                            if (Integer.parseInt(ACAduserEnglishSetting.Monitor_Value_Array[i][7]) == 1) {
                                int value_offset = Integer.parseInt(ACAduserEnglishSetting.Monitor_Value_Array[i][0]);
                                if (Integer.parseInt(ACAduserEnglishSetting.Monitor_Value_Array[i][1]) != 2) {
                                    int temp = Integer.parseInt(ACAduserEnglishSetting.Monitor_Value_Array[i][0]);
                                    if(MonitorDataValueBackup[temp] != MonitorDataValue[temp]) {
                                        MonitorshowStr[i] = String.valueOf(MonitorDataValue[temp]);
                                        MonitorDataValueBackup[temp] = MonitorDataValue[temp];
                                        monitorMap.put(Integer.parseInt(ACAduserEnglishSetting.Monitor_Value_Array[i][0]), MonitorDataValue[temp]);
                                    }
                                } else if (Integer.parseInt(ACAduserEnglishSetting.Monitor_Value_Array[i][0]) == Integer.parseInt(ACAduserEnglishSetting.Monitor_Value_Array[0][0])) {
                                    if (MonitorDataValueBackup[value_offset] != MonitorDataValue[value_offset] || MonitorDataValueBackup[value_offset + 1] != MonitorDataValue[value_offset + 1]) {
                                        int Error_Struct = (MonitorDataValue[value_offset] * 256) + MonitorDataValue[value_offset + 1];
                                        if (Error_Struct == 0) {
                                            MonitorshowStr[i] = "";
                                            //ACAduserEnglishKellyPage.this.viewsetBackgroundColor(ACAduserEnglishKellyPage.this.Monitor_View_Array[i], -2894893);
                                            //ACAduserEnglishKellyPage.this.Monitor_View_Array[i].postInvalidate();
                                        } else if (Error_Struct > 0) {
                                            MonitorshowStr[i] = ACAduserEnglishByteUtil.InttoBinaryString(Error_Struct);
                                            //ACAduserEnglishKellyPage.this.viewsetBackgroundColor(ACAduserEnglishKellyPage.this.Monitor_View_Array[i], SupportMenu.CATEGORY_MASK);
                                            //ACAduserEnglishKellyPage.this.Monitor_View_Array[i].postInvalidate();
                                        }
                                        MonitorDataValueBackup[value_offset] = MonitorDataValue[value_offset];
                                        MonitorDataValueBackup[value_offset + 1] = MonitorDataValue[value_offset + 1];
                                    }
                                } else if (MonitorDataValueBackup[value_offset] != MonitorDataValue[value_offset] || MonitorDataValueBackup[value_offset + 1] != MonitorDataValue[value_offset + 1]) {
                                    MonitorshowStr[i] = String.valueOf((MonitorDataValue[value_offset] * 256) + MonitorDataValue[value_offset + 1]);
//                                    Monitor_View_Array[i].postInvalidate();
                                    MonitorDataValueBackup[value_offset] = MonitorDataValue[value_offset];
                                    MonitorDataValueBackup[value_offset + 1] = MonitorDataValue[value_offset + 1];
                                }
                            }
                        }
                        m.onMonitor(monitorMap);
                            Thread.sleep(100);
                    } else {
                        running = false;
                        Thread.sleep(100);
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                System.out.println(">>>>exit kelly monitor");
            }

        }

        public void pause() {
            paused = true;
        }

        public void resumed() {
            paused = false;
        }

        public boolean isRunning() {
            return running;
        }

    }
}
