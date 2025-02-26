package com.ndm.stotyreading.dialogs;



import android.app.Dialog;
import android.content.Context;
import android.widget.Button;
import android.widget.TextView;

import com.ndm.stotyreading.R;


public class DialogUtils {

    public static void showErrorDialog(Context context, String message) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_error);
        dialog.setCancelable(true);

        TextView txtMessage = dialog.findViewById(R.id.txtErrorMessage);
        Button btnDismiss = dialog.findViewById(R.id.btnDismiss);

        txtMessage.setText(message);
        btnDismiss.setOnClickListener(view -> dialog.dismiss());

        dialog.show();
    }

//    public static void showSuccessDialog(Context context, String message) {
//        Dialog dialog = new Dialog(context);
//        dialog.setContentView(R.layout.dialog_success);
//        dialog.setCancelable(true);
//
//        TextView txtMessage = dialog.findViewById(R.id.txtSuccessMessage);
//        Button btnDismiss = dialog.findViewById(R.id.btnDismiss);
//
//        txtMessage.setText(message);
//        btnDismiss.setOnClickListener(view -> dialog.dismiss());
//
//        dialog.show();
//    }
}
