package com.example.mask_timer;

import android.widget.ImageView;

public class JMask {

    //ATRIBUTOS

    //Para gestionar el CICLO DE VIDA de la MASK, defino posibles ESTADOS por los que pasa
    // (se definen como públicos y final (constantes) ya que da igual.
    // El tipo de mask, para todas es el mismo dato):
    public final static int EVTO_CREATE = 1; //El momento en el que se pone por primera vez. Me interesa el momento exacto para luego hacer cálculos)
    public final static int EVTO_PAUSE = 2;  //Momento en el que me quito la mask, para saber el momento exacto y hacer calculo del tiempo consumido
    public final static int EVTO_RESUME = 3; //Momento en el que me pongo otra vez la mask, para saber el momento exacto y hacer calculo del tiempo consumido
    public final static int EVTO_FINISH = 4; //El tiempo consumido es superior al tiempo de vida util, por eso se acaba la mask

    //Lo mismo para definir diferentes tipos de mask:
    public final static int TIPO_QUIR= 1;
    public final static int TIPO_FPP2= 2;

    //Definimos los atributos particulaes (variables que van cambiado según la mask)
    private int tipo;
    private long time_init;
    private long time_consumido;
    private long time_total;
    private int evto_actual;





    //PROPIEDADES
    public long getTimeInit() {
        return time_init;
    }

    public long getTimeConsumido() {
        return time_consumido;
    }

    public void addConsumido() {
        time_consumido++;
    }


    //Aquí metemos los SEGUNDOS que TIENEN de duración las MASKs:
    public long getTimeRestante() {
        //VIDA DE LA MASK en SEGUNDOS según el TIPO:
        // TIPO_FPP2= 2; = FFP2 = 8 horas = 28800 seg
        // TIPO_QUIR= 1; = SURGICAL = 4 horas = 14400 seg

        //Hay que hacer los tiempos de los 2 TIPOs de Mask. Long por ser numero largo, por 4 horas, por 3600seg/hora según Carlos es:
        //long time_total = tipo * 4 * 3600;      yo lo hago de otra manera, sin la fórmula, pongo los seg directamente

        // Para hacer las pruebas de funcionamiento pongo que solo dura 12 segundos, sin long por ser numero corto
     //   time_total = 12;
     //   long time_restante = time_total - time_consumido;
      //  return time_restante;


        if (tipo == 2) {
            time_total = 20;     // TIPO_FPP2= 2; = FFP2 = 8 horas = 28800 seg.   Poner:    long time_total = 28800;
            long time_restante = time_total - time_consumido;
            return time_restante;

        } else {
            time_total = 10;    //TIPO_QUIR= 1; = SURGICAL = 4 horas = 14400 seg.  Poner:    long time_total = 14400;
            long time_restante = time_total - time_consumido;
            return time_restante;
        }




    }

    public int getTipo() {
        return tipo;
    }


    public String getMaskInfo() {
        String info="";
        switch (tipo) {
            case TIPO_QUIR:
                info = String.format("Surgical, 4h");
                break;
            case TIPO_FPP2:
                info = String.format("FFP2, 8h");
                break;
        }
        return info;
    }

    public int getEvtoActual() {
        return evto_actual;
    }




    //CONSTRUCTOR   //FUNCIONES
    public JMask(int tipo, long time_init, long time_consumido, int evto_actual) {
        this.tipo = tipo;
        this.time_init = time_init;
        this.time_consumido = time_consumido;
        this.evto_actual = evto_actual;
    }

}
