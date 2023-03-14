package com.example.innbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ServicioAceptado extends AppCompatActivity {

    FirebaseAuth mAuth;
    JSONArray dataService;
    TableLayout tablaServicios;
    Button showService;
    int idBtnServicio;

    String nombre;
    String correo;

    String strUserCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_servicio_aceptado);

        nombre = getIntent().getStringExtra("nombre");
        correo = getIntent().getStringExtra("correo");
        strUserCode =getIntent().getStringExtra("strUserCode");

       // serviciPost();
        servicioGetAceptados();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.Aceptados:
                Toast.makeText(this, "Buscando servicios", Toast.LENGTH_LONG).show();
                break;

            case R.id.salir2:
                mAuth.signOut();
                irMain();
                Toast.makeText(this, "Cerrando Sesión ", Toast.LENGTH_LONG).show();
                break;

            case R.id.disponibles:
                irHome();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void irMain() {

        Intent intent = new Intent(ServicioAceptado.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void irHome() {

        Intent intent = new Intent(ServicioAceptado.this, HomeActivity.class);

        intent.putExtra("nombre", nombre);
        intent.putExtra("correo", correo);

        startActivity(intent);
        finish();
    }


    private void servicioGetAceptados(){

        String url = "https://innboxservices.azurewebsites.net/api/Service/GetAllServices";
        StringRequest postRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    dataService = jsonObject.getJSONArray("values");
                    llenarTablaAceptados(dataService);

                } catch (JSONException e) {

                    Toast.makeText(getApplicationContext(),"Error "+e.getMessage(),Toast.LENGTH_LONG).show();

                    e.printStackTrace();
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error "+error.getMessage(),Toast.LENGTH_LONG).show();

                Log.e("Error en servicio", error.getMessage());

            }
        });
        Volley.newRequestQueue(this).add(postRequest);

    }

    private void llenarTablaAceptados(JSONArray data ){


        tablaServicios = findViewById(R.id.tablaServiciosAceptados);

        try {
            if(data.length() == 0){
                System.out.println("No hay servicios");
                TextView regresarAlHomre = findViewById(R.id.regresarAlHomre);
                regresarAlHomre.setText("No tienes servicios");
            }else{


                for (int i = 0; i < data.length(); i++) {

                    View registro = LayoutInflater.from(this).inflate(R.layout.table_row_aceptado_innbox, null, false);
                    TextView colNombre = registro.findViewById(R.id.colNombreAceptado);
                    showService = registro.findViewById(R.id.showServiceAceptado);

                    JSONObject reg = data.getJSONObject(i);

                    if(reg.getString("user").equals(strUserCode) && reg.getString("status").equals("Asignado") ){

                        colNombre.setText(reg.getString("serviceType"));
                        showService.setId(i);

                        showService.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                idBtnServicio = saberIdBtn(view);
                                mostrarDialogo();
                            }
                        });


                        tablaServicios.addView(registro);

                    }

                }

            }


        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        TextView regresarAlHomre = findViewById(R.id.regresarAlHomre);

        regresarAlHomre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irHome();
            }
        });

    }
    public  int saberIdBtn(View v){
        return v.getId();
    }

    private void mostrarDialogo(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.dialogoserviaceptado,null);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();

        JSONObject as = null;

        try {

            System.out.println("dataService "+dataService);

            TextView dinero = view.findViewById(R.id.dineroAceptado);
            TextView calendario = view.findViewById(R.id.calendarioAceptado);
            TextView horasdeTrabajo = view.findViewById(R.id.horasdeTrabajoAceptado);
            TextView direccion = view.findViewById(R.id.direccionaceptado);
            TextView horaEntrada = view.findViewById(R.id.horaEntradaAceptado);
            TextView tipoServicio = view.findViewById(R.id.tipoServicioaceptado);
            as = dataService.getJSONObject(idBtnServicio);

            /***/


            String fechaEntera = as.getString("startDate");
            //transforma la cadena en un tipo date
            Date miFecha = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(fechaEntera);

            //creo un calendario
            Calendar calendarioa = Calendar.getInstance();
            //establezco mi fecha
            calendarioa.setTime(miFecha);

            //obtener el año
            int anio = calendarioa.get(Calendar.YEAR);
            //obtener el mes (0-11 ::: enero es 0 y diciembre es 11)
            int mes = calendarioa.get(Calendar.MONTH) + 1;
            //obtener el dia del mes (1-31)
            int dia = calendarioa.get(Calendar.DAY_OF_MONTH);
            //obtener el hora del dia (1-24)
            int hora = calendarioa.get(Calendar.HOUR_OF_DAY);
            //obtener el minuto
            int minuto = calendarioa.get(Calendar.MINUTE);
            //obtener el segundo
            int segundo = calendarioa.get(Calendar.SECOND);

            String fechaMostrar;
            String diaMostrar = dia < 10 ? "0" + dia : String.valueOf(dia); //+"-"+mes+"-"+anio;;
            String mesMostrar = mes < 10 ? "0" + mes : String.valueOf(mes);
            fechaMostrar = diaMostrar+"/"+mesMostrar+"/"+anio;


            String horaCompleta;
            String horaM = hora < 10 ? "0"+hora : String.valueOf(hora);
            String minM = minuto < 10 ? "0"+minuto : String.valueOf(minuto);
            String segM = segundo < 10 ? "0"+segundo : String.valueOf(segundo);
            horaCompleta = horaM+":"+minM+":"+segM;

            /****/

            /**diferencia de horas*/

            //Lo primero que tienes que hacer es establecer el formato que tiene tu fecha para que puedas obtener un objeto de tipo Date el cual es el que se utiliza para obtener la diferencia.
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

            //Parceas tus fechas en string a variables de tipo date se agrega un try catch porque si el formato declarado anteriormente no es igual a tu fecha obtendrás una excepción
            Date dateStart = dateFormat.parse(as.getString("startDate"));
            Date dateEnd = dateFormat.parse(as.getString("endDate"));

            //obtienes la diferencia de las fechas
            long difference = Math.abs(dateEnd.getTime() - dateStart.getTime());

            //obtienes la diferencia en horas ya que la diferencia anterior esta en milisegundos
            difference= difference / (60 * 60 * 1000);


            /**end dif**/
            //int pago = Integer.parseInt(as.getString("value"));
            dinero.setText(as.getString("value"));
            calendario.setText(fechaMostrar);//formattedDate
            horasdeTrabajo.setText(Long.toString(difference)+" Horas contratadas");
            direccion.setText(as.getString("address"));
            horaEntrada.setText(horaCompleta);
            tipoServicio.setText(as.getString("serviceType"));



        } catch (JSONException  e) {
            Log.d("***", "mostrarDialogo: error" + e.getMessage());
            throw new RuntimeException(e);
        } catch (ParseException e) {
            Log.d("***", "mostrarDialogo: error 2222" + e.getMessage());
            throw new RuntimeException(e);
        }


    }


}