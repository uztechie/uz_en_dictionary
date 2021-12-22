package uz.techie.uzendictionary.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import uz.techie.uzendictionary.R;


public class InternetDialog extends Dialog {
    TextView tvTitle, tvMessage;
    Button btnConnect, btnCancel;

    public InternetDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_internet);
        setCanceledOnTouchOutside(false);
        setCancelable(false);

        tvTitle = findViewById(R.id.internet_title);
        tvMessage = findViewById(R.id.internet_text);
        btnConnect = findViewById(R.id.internet_btnConnect);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                dismiss();
            }
        });






    }
}
