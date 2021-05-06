package com.kelly.controller;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Handler;
import android.os.Message;

public class ACAduserEnglishDialogMessage extends Handler {
    protected String btnTxt = "OK";
    protected Context context;
    protected String message;
    protected String title;

    /* renamed from: com.kelly.ACAduserEnglish.ACAduserEnglishDialogMessage$1 */
    class C00651 implements OnClickListener {
        C00651() {
        }

        public void onClick(DialogInterface dialog, int whichButton) {
        }
    }

    public ACAduserEnglishDialogMessage(Context context, String message) {
        this.message = message;
        this.title = "Message";
        this.context = context;
    }

    public void setButtonText(String btnTxt) {
        this.btnTxt = btnTxt;
    }

    public void handleMessage(Message msg) {
        Builder dialogBuilder = new Builder(this.context);
        dialogBuilder.setTitle(this.title);
        dialogBuilder.setMessage(this.message);
        dialogBuilder.setPositiveButton(this.btnTxt, new C00651());
        Dialog dialog = dialogBuilder.create();
        dialog.setCancelable(false);
        dialog.show();
    }
}
