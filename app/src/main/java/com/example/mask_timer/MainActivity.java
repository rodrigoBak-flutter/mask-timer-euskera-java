package com.example.mask_timer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;


//La pantalla Main, tiene que comprobar si en la bbdd hay alguna mask activa o no
//Si hay, muestra su estado (resume / pause), con las opciones de parar/ reiniciar)

public class MainActivity extends AppCompatActivity {

    //Tengo que trabajar mucho con la bbdd para cambiar estados y saber si hay mask activa:
    private JBBDD bd;
    //En todos momento trabajo con una mask:
    private JMask mask_actual;

    //Cuando la aplicación está en primer plano, me hace falta un objeto cronómetro, para que el usuario vea como baja el tiempo:
    private CountDownTimer countDownTimer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Enlazar los iv de la PARTE "Current mask"
        ImageView ivMaskEditTipoF = findViewById(R.id.ivMaskEditTipoF);
        ImageView ivMaskEditTipoQ = findViewById(R.id.ivMaskEditTipoQ);



        //Activity MAIN, PARTE "SELECT YOUR MASK" cuando NO HAY MASK EN CURSO:
        // Mask Tipo FFP2
        findViewById(R.id.btnMaskNewTipoF).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Confirmamos que quiere iniciar una mask de este tipo mostrando el diálogo:
                iniciarNuevaMask(JMask.TIPO_FPP2);

                //Que se vea ivMaskEditTipoF (imagen maskFFP2) y que no se vea ni ocupe espacio ivMaskEditTipoQ (imagen maskSURGICAL)
                ivMaskEditTipoF.setVisibility(View.VISIBLE);
                ivMaskEditTipoQ.setVisibility(View.GONE);
            }
        });




        // Mask Tipo SURGICAL (quirúrgica)
        findViewById(R.id.btnMaskNewTipoQ).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Confirmamos que quiere iniciar una mask de este tipo mostrando el diálogo:
                iniciarNuevaMask(JMask.TIPO_QUIR);

                //Que se vea ivMaskEditTipoQ (imagen maskSURGICAL) y que no se vea ni ocupe espacio ivMaskEditTipoF (imagen maskFFP2)
                ivMaskEditTipoF.setVisibility(View.GONE);
                ivMaskEditTipoQ.setVisibility(View.VISIBLE); //Hacemos invisibles los otros 2 no elegidos
            }
        });





        //Activity MAIN, PARTE "CURRENT MASK" cuando hay mask en curso:
        findViewById(R.id.btnMaskEdit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Gestionamos cambios de estado (pause/resume)
                if (mask_actual == null) {
                    //No debería ser nulo, mostramos error!!!
                    Toast.makeText(MainActivity.this, "MASK ACTUAL NULL???????", Toast.LENGTH_SHORT).show();
                } else {
                    //Obtenemos el timestamp actual:
                    long time_actual = System.currentTimeMillis()/1000;
                    //Comprobamos el estado de la mascara:
                    switch (mask_actual.getEvtoActual()) {
                        case JMask.EVTO_CREATE:
                        case JMask.EVTO_RESUME:
                            //Pasamos al estado pause:
                            bd.insertarEvento(mask_actual.getTipo(),time_actual,JMask.EVTO_PAUSE);
                            refrescarLayout();
                            //Actualizamos el texto con la acción del botón:
                            ((Button)findViewById(R.id.btnMaskEdit)).setText(R.string.txt_mask_edit_resume);
                            break;
                        case JMask.EVTO_PAUSE:
                            //Pasamos al estado resume:
                            bd.insertarEvento(mask_actual.getTipo(),time_actual,JMask.EVTO_RESUME);
                            refrescarLayout();
                            //Actualizamos el texto con la acción del botón:
                            ((Button)findViewById(R.id.btnMaskEdit)).setText(R.string.txt_mask_edit_pause);
                            break;
                        case JMask.EVTO_FINISH:
                            //TODO: Pdte pensar que hacer cuando se agota el tiempo de la mask:
                            // LA APP TIENE QUE PASAR A PRIMER PLANO desde el segundo plano donde está con la cuenta atrás de 8h de la mask


                            ((Button)findViewById(R.id.btnMaskEdit)).setText("NEW MASK");
                            break;
                    }
                }
            }
        });


        findViewById(R.id.btnMaskFinish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.dlg_mask_finish_title)
                        .setMessage(R.string.dlg_mask_finish_msg)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //El usuario confirma que descarta esta mask, elimino todos de la bbdd:
                                bd.finalizarMask();
                                //Cierro esta pantalla, y salto a la de Maskfinished:
                                Intent nueva_pantalla = new Intent(MainActivity.this, MaskFinishedActivity.class);
                                startActivity(nueva_pantalla);
                            }

                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });

    }



// FUNCION INICIARNUEVA MASK IMPORTANTE!!!!

    private void iniciarNuevaMask(final int tipo_mask) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.app_name);
        builder.setMessage(R.string.dlg_estrenar_mask);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

            //Después del Cuadro de Dialogo viene la FUNCION IMPORTANTE de iniciarNuevaMask
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Borramos la bbdd y creamos una nueva mascarilla en estado 1 (create)
                //Obtenemos el timestamp actual:
                long time_actual = System.currentTimeMillis()/1000;
                //String ts = tsLong.toString();
                bd.insertarEvento(tipo_mask,time_actual,JMask.EVTO_CREATE);
                refrescarLayout();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bd = new JBBDD(MainActivity.this);
        refrescarLayout();
    }

    private void refrescarLayout() {
        //Comprobamos si hay una mask activa:
        //Obtengo el timestamp actual en milisegundos y lo paso a segundos diviendo entre 1000:
        mask_actual = bd.getMaskActual(System.currentTimeMillis()/1000);
        if (mask_actual == null) {
            //No hay mask en activo, mostramos el layout de selección de iniciar una nueva
            findViewById(R.id.ll_mask_new).setVisibility(View.VISIBLE);
            findViewById(R.id.ll_mask_edit).setVisibility(View.GONE);
        } else {
            //Hay una mask en activo, mostramos el estado actual y el tiempo que le queda:
            TextView tvMaskTipo = findViewById(R.id.tvMaskActTipo);
            tvMaskTipo.setText(String.format("Mask type: %s",mask_actual.getMaskInfo())); //txt_mask_edit_tipo

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(mask_actual.getTimeInit() * 1000);
            String time_inicio = DateFormat.format("dd-MM-yyyy HH:mm", cal).toString();
            TextView tvMaskInicio = findViewById(R.id.tvMaskActInicio);
            tvMaskInicio.setText(String.format(getString(R.string.txt_mask_edit_inicio), time_inicio));

            mostrarConsumidoRestante();

            //Segun el estado actual, pintamos el botón de acción:
            switch (mask_actual.getEvtoActual()) {
                case JMask.EVTO_CREATE:
                case JMask.EVTO_RESUME:
                    ((Button)findViewById(R.id.btnMaskEdit)).setText(R.string.txt_mask_edit_pause);
                    iniciarCrono(mask_actual.getTimeRestante());
                    break;
                case JMask.EVTO_PAUSE:
                    pararCrono();
                    ((Button)findViewById(R.id.btnMaskEdit)).setText(R.string.txt_mask_edit_resume);
                    break;
            }

            findViewById(R.id.ll_mask_new).setVisibility(View.GONE);
            findViewById(R.id.ll_mask_edit).setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        pararCrono();
    }



    //Empieza el CRONÓMETRO

    private void iniciarCrono(long seconds) {
        countDownTimer = new CountDownTimer(seconds*1000, 1000) {

            public void onTick(long millisUntilFinished) {
                //mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
                mask_actual.addConsumido();
                mostrarConsumidoRestante();
            }

            public void onFinish() {
                //Quito el TOAST que me había puesto Carlos porque no lo veo necesario
               // Toast.makeText(MainActivity.this, "Se acabo la vida de la mascara!!!", Toast.LENGTH_SHORT).show();
                //Se acabo la vida de la mask, pasamos a la pantalla MaskFinished:
                bd.finalizarMask();
                //Cierro esta pantalla, y salto a la de Maskfinished:
                Intent nueva_pantalla = new Intent(MainActivity.this, MaskFinishedActivity.class);
                startActivity(nueva_pantalla);
            }
        }.start();
    }

    private void pararCrono() {
        try {
            if ( countDownTimer != null) countDownTimer.cancel();
        } catch (Exception e) { }
    }

    private void mostrarConsumidoRestante() {
        TextView tvMaskConsumido = findViewById(R.id.tvMaskActConsumido);
        //Obtenemos en formato String de horas:mm:ss el tiempo consumido de la mask
        String tiempo_consumido = formatearTiempo(mask_actual.getTimeConsumido());
        //Mostramos en el layout:
        tvMaskConsumido.setText(String.format(getString(R.string.txt_mask_edit_consumido),tiempo_consumido));

        TextView tvMaskRestante = findViewById(R.id.tvMaskActRestante);
        //Obtenemos en formato String de horas:mm:ss el tiempo restante de la mask
        String tiempo_restante = formatearTiempo(mask_actual.getTimeRestante());
        //Mostramos en el layout:
        tvMaskRestante.setText(String.format(getString(R.string.txt_mask_edit_restante),tiempo_restante));
    }

    private String formatearTiempo(long seconds) {
        int hours = (int) seconds / 3600;
        int remainder = (int) seconds - hours * 3600;
        int mins = remainder / 60;
        remainder = remainder - mins * 60;
        int secs = remainder;
        return String.format("%sh %02d' %02d''",hours,mins,secs);
    }





}