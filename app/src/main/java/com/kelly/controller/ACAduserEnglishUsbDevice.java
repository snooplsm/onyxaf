package com.kelly.controller;

import android.hardware.usb.UsbDeviceConnection;

public class ACAduserEnglishUsbDevice {
    private int setTimeOut = 0;

    public void configUsbDeviceParamter(UsbDeviceConnection conn, int baudRate, int dataBits, int stopBits, int parity) {
        for (int i = 0; i < 1; i++) {
            conn.controlTransfer(64, 0, 0, i, null, 0, this.setTimeOut);
            conn.controlTransfer(64, 0, 1, i, null, 0, this.setTimeOut);
            conn.controlTransfer(64, 0, 2, i, null, 0, this.setTimeOut);
            conn.controlTransfer(64, 2, 0, i, null, 0, this.setTimeOut);
            conn.controlTransfer(64, 3, calcFT232bmBaudBaseToDiv(baudRate), i, null, 0, this.setTimeOut);
            conn.controlTransfer(64, 4, getDataStopParity(dataBits, stopBits, parity), i, null, 0, this.setTimeOut);
        }
    }

    public int calcFT232bmBaudBaseToDiv(int baudRate) {
        int i = 3000000 / baudRate;
        int j = 0;
        if (((24000000 / baudRate) & 4) != 0) {
            j = 16384;
        }
        if (((24000000 / baudRate) & 2) != 0) {
            j = 32768;
        }
        if (((24000000 / baudRate) & 1) != 0) {
            j = 49152;
        }
        return i | j;
    }

    public int getDataStopParity(int dataBits, int stopBits, int parity) {
        return (dataBits | (parity << 8)) | (stopBits << 11);
    }
}
