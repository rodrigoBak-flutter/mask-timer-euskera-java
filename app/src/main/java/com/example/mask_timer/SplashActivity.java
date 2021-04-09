package com.example.mask_timer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {

    private final String TAG = "Mask_timer_SplashActivity";
    private MediaPlayer mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //Reproduzco la musica:
        //Reproduzco sonido de acierto:
        mPlayer = MediaPlayer.create(SplashActivity.this, R.raw.splash_musica);
        mPlayer.start();

        //Vamos a esperar 3 segundos y automaticamente que salte a la pantalla MainActivity:
        Handler retardo = new Handler();
        retardo.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Saltar a la pantalla Main (La del juego)
                Intent nueva_pantalla = new Intent(SplashActivity.this, MainActivity.class);
                //Para ejecutar el salto de pantalla:
                startActivity(nueva_pantalla);
                //Y además cierro la pantalla actual, para que no se quede en espera:
                finish();
            }
        },3000);

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