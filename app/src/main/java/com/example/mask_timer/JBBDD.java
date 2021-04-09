package com.example.mask_timer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class JBBDD extends SQLiteOpenHelper {
    //ATRIBUTOS
    //Definir variables con los nombres de los campos de la tabla:
    private final String TABLA="historial";
    private final String COL_ID = "id";
    private final String COL_TIPO = "tipo_mask";
    private final String COL_TIME = "timestamp";
    private final String COL_EVTO = "evento";


//CONSTRUCTOR
    //En el CONSTRUCTOR definimos el NOMBRE de la BBDD:
    public JBBDD(@Nullable Context context) {
        super(context, "app_mask", null, 1);

    }


    //Definir con el lenguaje SQL la TABLA(s) a crear junto con sus campos y sus tipos de datos.
    //Incluso podemos añadir datos iniciales (puedo ejecutar inserts)
    @Override
    public void onCreate(SQLiteDatabase db_conexion) {
        String sql = String.format("create table %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER NOT NULL, %s INTEGER NOT NULL, %s INTEGER NOT NULL)",
                TABLA,COL_ID,COL_TIPO,COL_TIME,COL_EVTO);
        db_conexion.execSQL(sql);

    }


    //Cuando haga ACTUALIZACIONES de la APP y requiera modificar o añadir campos o tablas en la BBDD lo haré aquí:
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }




    //FUNCIONES
    //A partir de aquí las FUNCIONES que necesito prara trabajar con la BBDD: insertar, listar, eliminar...


    public JMask getMaskActual(long time_actual) {
        //Esta función comprueba en la bbdd si tengo datos de una mask en activo y carga sus datos.
        // Si no devuelve mask nula.
        JMask mask_actual = null;
        SQLiteDatabase db_conexion = this.getReadableDatabase();
        //Obtenemos la última fila registrada en la bbdd para saber si hay mask y su estado:
        String consulta = String.format("Select * from %s order by %s Asc",TABLA,COL_ID);
        Cursor datos =  db_conexion.rawQuery( consulta, null );
        if (datos.moveToFirst()) {
            //En principio siempre es el mismo tipo de mask, con coger este dato una vez suficiente:
            int tipo_mask = datos.getInt(datos.getColumnIndex(COL_TIPO));
            //Variables para calcular el tiempo consumido según el historial de eventos:
            long time_init=0;
            long time_ultimo=0;
            long time_consumido=0;
            int evto;
            do {
                //Recogemos los datos:
                evto = datos.getInt(datos.getColumnIndex(COL_EVTO));
                long time = datos.getLong(datos.getColumnIndex(COL_TIME));
                //Según el tipo de evto:
                switch (evto) {
                    case JMask.EVTO_CREATE:
                        time_init = time;
                        time_ultimo = time;
                        break;
                    case JMask.EVTO_RESUME:
                        //Apuntamos cuando volvió a ponese en marcha
                        time_ultimo = time;
                        break;
                    case JMask.EVTO_PAUSE:
                        //Calculamos el tiempo consumido:
                        time_consumido = time_consumido + (time - time_ultimo);
                        break;
                    case JMask.EVTO_FINISH:

                        break;
                }

            } while (datos.moveToNext());
            //Si el estado actual es CREATE o RESUME, sumamos el tiempo consumido segun el time_actual:
            if (evto == JMask.EVTO_CREATE || evto == JMask.EVTO_RESUME) {
                time_consumido = time_consumido + (time_actual - time_ultimo);
            }
            //Creamos el objeto con el último estado y los datos obtenidos:
            mask_actual = new JMask(tipo_mask,time_init,time_consumido,evto);
        }
        return mask_actual;
    }


    public boolean isMaskActiva() {
        //Pedimos el número de filas de la tabla, si es mayor que 0 hay una mask activa
        if (totalDatosRegistrados()>0) {
            return true;
        } else {
            return false;
        }
    }


    //Para saber si tengo datos en la tabla:
    private int totalDatosRegistrados() {
        int total = 0;
        SQLiteDatabase db_conexion = this.getReadableDatabase();
        String consulta = String.format("Select count(*) as TOTAL from %s",TABLA);
        Cursor datos =  db_conexion.rawQuery( consulta, null );
        if (datos.moveToFirst()) {
            //La consulta me ha devuelto una fila con datos, si no hay datos devuelve un 0 y si no el numero de filas:
            total = datos.getInt(datos.getColumnIndex("TOTAL"));
        }
        return total;
    }


    public boolean insertarEvento(int tipo_mask, long timestamp, int evento) {
        SQLiteDatabase db_conexion = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_TIPO, tipo_mask);
        contentValues.put(COL_TIME, timestamp);
        contentValues.put(COL_EVTO, evento);
        //Aqui ejecutamos la instrucción insert:
        long filas_afectadas = db_conexion.insert(TABLA, null, contentValues);
        if (filas_afectadas>0) {
            return true;
        } else {
            return false;
        }
    }


    public long getTimeInicio() {
        long inicio = 0;
        SQLiteDatabase db_conexion = this.getReadableDatabase();
        String consulta = String.format("Select %s from %s where %s=%s",COL_TIME,TABLA,COL_EVTO,1);
        Cursor datos =  db_conexion.rawQuery( consulta, null );
        if (datos.moveToFirst()) {
            //La consulta me ha devuelto una fila con datos, si no hay alumnos devuelve un 0 y si no el numero de alumnos:
            inicio = datos.getLong(datos.getColumnIndex(COL_TIME));
        }
        return inicio;
    }


    public boolean finalizarMask() {
        SQLiteDatabase db_conexion = this.getWritableDatabase();
        //Borramos todas las filas:
        db_conexion.delete(TABLA, null,null);
        //Comprobamos si se ha borrado todos, si es así, devolvemos ok (no hay filas en la tabla de la bbdd)
        if (totalDatosRegistrados()==0) {
            return true;
        } else {
            return false;
        }
    }



    public int getMaskEstado() {
        int evto = 0;
        SQLiteDatabase db_conexion = this.getReadableDatabase();
        String consulta = String.format("Select %s from %s order by DESC limit 1",COL_EVTO,TABLA,COL_TIME,COL_EVTO,1);
        Cursor datos =  db_conexion.rawQuery( consulta, null );
        if (datos.moveToFirst()) {
            //La consulta me ha devuelto una fila con datos, si no hay alumnos devuelve un 0 y si no el numero de alumnos:
            evto = datos.getInt(datos.getColumnIndex(COL_EVTO));
        }
        return evto;
    }






}
