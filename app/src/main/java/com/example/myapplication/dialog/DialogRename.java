package com.example.myapplication.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;

public class DialogRename {
    private Context mContext;
    OnClickListener listener;

    public DialogRename(Context context) {
        this.mContext = context;
    }

    public void show(String name) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View alertView = inflater.inflate(R.layout.dialog_rename, null);
        TextView titleView = alertView.findViewById(R.id.text_title);
        titleView.setText("Rename grocery list");
        builder.setView(alertView);
        final EditText editView = alertView.findViewById(R.id.edit_name);
        editView.setText(name);

        builder.setPositiveButton("Rename", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String newName = editView.getText().toString().trim();
                listener.onClickPositiveButton(dialog, newName);
            }
        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
        Button btnNegative = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        btnNegative.setTextColor(mContext.getResources().getColor(R.color.btn_negative));
        Button btnPositive = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        btnPositive.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
    }

    public interface OnClickListener {
        void onClickPositiveButton(DialogInterface dialog, String name);
    }

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }
}
