package com.example.mask_timer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MaskFinishedActivity extends AppCompatActivity {

    private MediaPlayer mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mask_finished);

        //Reproduzco la musica de CU CU:

        mPlayer = MediaPlayer.create(MaskFinishedActivity.this, R.raw.cucut);
        mPlayer.start();


        Button btnNewmask = findViewById(R.id.btnNewmask);

        btnNewmask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Me cierro, y vuelve a salir la pantalla Main, pero como la bbdd está vacía, me ofrece elegir tipo de mask:
                finish();


               // ANTES saltabamos a la Pantalla MainActivity para ekegir la nueva mask:
                //Aqui dentro programo la respuesta:
               // Intent nueva_pantalla = new Intent(MaskFinishedActivity.this, MainActivity.class);
                //Ejecuto el cambio de pantalla:
               // startActivity(nueva_pantalla);
            }
        });


    }


    @Override
    protected void onPause() {
        super.onPause();
        try {
            //Paro la musica, y por si hubiera un error, lo meto try - catch
            mPlayer.stop();
        } catch (IllegalStateException e) {
        }
    }

}