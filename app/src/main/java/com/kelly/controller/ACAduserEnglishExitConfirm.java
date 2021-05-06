package com.kelly.controller;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Handler;
import android.os.Message;

public class ACAduserEnglishExitConfirm extends Handler {
    private Activity context;
    protected String noBtnTxt = "Cancel";
    protected String okBtnTxt = "OK";

    /* renamed from: com.kelly.ACAduserEnglish.ACAduserEnglishExitConfirm$1 */
    class C00661 implements OnClickListener {
        C00661() {
        }

        public void onClick(DialogInterface dialog, int whichButton) {
            if (BluetoothAdapter.getDefaultAdapter() != null) {
                BluetoothAdapter.getDefaultAdapter().disable();
                ACAduserEnglishDeviceKerry.getInstance().closeBluetoothComDevice();
            }
            ACAduserEnglishDeviceKerry.getInstance().closeUsbComDevice();
            ACAduserEnglishAppManager.getInstance().exitAndroidActivity();
        }
    }

    /* renamed from: com.kelly.ACAduserEnglish.ACAduserEnglishExitConfirm$2 */
    class C00672 implements OnClickListener {
        C00672() {
        }

        public void onClick(DialogInterface dialog, int whichButton) {
        }
    }

    public ACAduserEnglishExitConfirm(Activity context) {
        this.context = context;
    }

    public void handleMessage(Message msg) {
        Builder dialogBuilder = new Builder(this.context);
        dialogBuilder.setTitle("Confirm");
        dialogBuilder.setMessage("Confirm to Exit ?");
        dialogBuilder.setPositiveButton(this.okBtnTxt, new C00661());
        dialogBuilder.setNegativeButton(this.noBtnTxt, new C00672());
        Dialog dialog = dialogBuilder.create();
        dialog.setCancelable(false);
        dialog.show();
    }
}
