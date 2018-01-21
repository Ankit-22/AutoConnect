package com.ankit.autoconnect;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

public class ConfigServer extends AppCompatActivity {

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_server);
        editText = findViewById(R.id.server_ip);
        editText.setImeActionLabel("Connect", EditorInfo.IME_ACTION_GO);
        editText.setImeOptions(EditorInfo.IME_ACTION_GO);
        editText.setRawInputType(InputType.TYPE_CLASS_TEXT);
        editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_GO) {
                    findViewById(R.id.button).performClick();
                    Log.i("Debug", "Enter pressed");
                }
                return false;
            }
        });
    }

    public void startMouse(View view) {
        String server = editText.getText().toString();
        int port = 6000;
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("server", server);
        intent.putExtra("port", port);
        startActivity(intent);
        finish();
    }
}
