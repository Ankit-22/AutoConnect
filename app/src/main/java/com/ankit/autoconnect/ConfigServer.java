package com.ankit.autoconnect;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class ConfigServer extends AppCompatActivity {

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_server);
        editText = findViewById(R.id.server_ip);
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
