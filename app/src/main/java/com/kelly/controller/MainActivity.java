package com.kelly.controller;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.widget.TextView;

import us.wmwm.onyx.R;

public class MainActivity extends Activity {
    private static final int REQUEST_CODE = 1;
    private int Bluetooth_Enable = 1;
    private int MotorNotRun = 0;
    private int mSingleChoiceID = 0;

    /* renamed from: com.kelly.ACAduserEnglish.MainActivity$1 */
    class C00871 implements OnClickListener {
        C00871() {
        }

        public void onClick(DialogInterface dialog, int whichButton) {
            MainActivity.this.MotorNotRun = 0;
            if (BluetoothAdapter.getDefaultAdapter() != null) {
                BluetoothAdapter.getDefaultAdapter().disable();
                ACAduserEnglishDeviceKerry.getInstance().closeBluetoothComDevice();
            }
            ACAduserEnglishDeviceKerry.getInstance().closeUsbComDevice();
            ACAduserEnglishAppManager.getInstance().exitAndroidActivity();
        }
    }

    /* renamed from: com.kelly.ACAduserEnglish.MainActivity$2 */
    class C00882 implements OnClickListener {
        C00882() {
        }

        public void onClick(DialogInterface dialog, int whichButton) {
            MainActivity.this.MotorNotRun = 1;
            MainActivity.this.showitem();
        }
    }

    /* renamed from: com.kelly.ACAduserEnglish.MainActivity$3 */
    class C00893 extends Thread {
        C00893() {
        }

        public void run() {
            Intent intent = new Intent();
            //intent.setClass(MainActivity.this, ACAduserEnglishKellyPage.class);
            intent.putExtra("comtype", "0");
            MainActivity.this.startActivityForResult(intent, 1);
        }
    }

    /* renamed from: com.kelly.ACAduserEnglish.MainActivity$4 */
    class C00904 implements OnClickListener {
        C00904() {
        }

        public void onClick(DialogInterface dialog, int whichButton) {
            MainActivity.this.mSingleChoiceID = whichButton;
        }
    }

    /* renamed from: com.kelly.ACAduserEnglish.MainActivity$5 */
    class C00925 implements OnClickListener {

        /* renamed from: com.kelly.ACAduserEnglish.MainActivity$5$1 */
        class C00911 extends Thread {
            C00911() {
            }

            public void run() {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, ACAduserEnglishKellyPage.class);
                intent.putExtra("comtype", new StringBuilder(String.valueOf(MainActivity.this.mSingleChoiceID)).toString());
                MainActivity.this.startActivityForResult(intent, 1);
            }
        }

        C00925() {
        }

        public void onClick(DialogInterface dialog, int whichButton) {
            new Handler().postDelayed(new C00911(), 100);
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        //setRequestedOrientation(0);
        Builder dialogBuilder = new Builder(this);
        dialogBuilder.setTitle("This is a Warning!");
        dialogBuilder.setMessage("Warning:Prohibit doing any configuration in user program while running motor.\r\n\r\n1.'Yes' to continue if the motor is not running.\r\n2.'No' if the motor is running. Please reconnect user program after stopping motor.");
        dialogBuilder.setPositiveButton("No", new C00871());
        dialogBuilder.setNegativeButton("Yes", new C00882());
        Dialog dialog = dialogBuilder.create();
        dialog.setCancelable(false);
        dialog.show();
        TextView txtView = new TextView(this);
        txtView.setBackgroundResource(R.drawable.mybackimg_hor);
        setContentView(txtView);
        ACAduserEnglishAppManager.getInstance().add(this);
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            new ACAduserEnglishExitConfirm(this).sendEmptyMessage(0);
        }
        return true;
    }

    private void showitem() {
        if (BluetoothAdapter.getDefaultAdapter() == null || this.Bluetooth_Enable == 0) {
            new Handler().postDelayed(new C00893(), 800);
            return;
        }
        String[] mItems = new String[]{"FT232 COM", "Bluetooth COM"};
        Builder builder = new Builder(this);
        builder.setTitle("Please select the type of COM:");
        builder.setSingleChoiceItems(mItems, 0, new C00904());
        builder.setPositiveButton("Yes", new C00925());
        builder.setCancelable(false);
        builder.create().show();
    }
}
