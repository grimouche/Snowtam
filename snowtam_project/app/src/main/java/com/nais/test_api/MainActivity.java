package com.nais.test_api;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity{

    private EditText code_oaci;
    private EditText code_oaci2;
    private EditText code_oaci3;
    private EditText code_oaci4;
    private Button valider;

    private final String EXTRA_OACI = "oaci";
    private final String EXTRA_OACI2 = "oaci2";
    private final String EXTRA_OACI3 = "oaci3";
    private final String EXTRA_OACI4 = "oaci4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        code_oaci = (EditText)findViewById(R.id.Oaci);
        code_oaci2 = (EditText)findViewById(R.id.Oaci2);
        code_oaci3 = (EditText)findViewById(R.id.Oaci3);
        code_oaci4 = (EditText)findViewById(R.id.Oaci4);
        valider = (Button)findViewById(R.id.BoutonValider);
        // basculer vers la 2nd application au moment du click
        valider.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RecupApiActivity.class);
                intent.putExtra(EXTRA_OACI, code_oaci.getText().toString());
                intent.putExtra(EXTRA_OACI2, code_oaci2.getText().toString());
                intent.putExtra(EXTRA_OACI3, code_oaci3.getText().toString());
                intent.putExtra(EXTRA_OACI4, code_oaci4.getText().toString());
                startActivity(intent);
            }
        });
    }
}
