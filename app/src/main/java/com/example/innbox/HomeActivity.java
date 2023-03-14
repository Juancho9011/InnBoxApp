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
import android.widget.EditText;
import android.widget.ListView;
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
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class HomeActivity extends AppCompatActivity {

    ListView list;
    ArrayList<String> titles = new ArrayList<>();


    //Componentes
    TextView btncerrarsesion;

    //FireBase
    FirebaseAuth mAuth;

    TextView showNombre;
    TextView rol;

    //tabla servicios
    TableLayout tablaServicios;

    JSONArray dataService;
    Button showService;

    int idBtnServicio;

    String nombre;
    String correo;

    String urlServicios = "https://innboxservices.azurewebsites.net/api/Service/";
    String intServiceID;
    String strUserCode;
    String rolUsuario;
    String strRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

       // btncerrarsesion = findViewById(R.id.btncerrarsesion);

        mAuth = FirebaseAuth.getInstance();
       /*btncerrarsesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                irMain();
            }
        });*/


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser usuario = mAuth.getCurrentUser();
        if(usuario == null){
            irMain();
        }

         nombre = getIntent().getStringExtra("nombre");
         correo = getIntent().getStringExtra("correo");

        showNombre = findViewById(R.id.showNombre);
        rol = findViewById(R.id.rol);

        if(nombre.equals("")){
            showNombre.setText(correo);
        }

        getDataUser();
    }

    private void irMain() {

        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    private void leerWs(){

        //String url = "https://jsonplaceholder.typicode.com/posts";
        String url = "https://innboxservices.azurewebsites.net/api/Service/GetServicesByRole";


        StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);


                    //jsonObject.getString("values");
                    //Toast.makeText(this,)
                    Log.d("lo que llega del server", jsonObject.toString());
                    JSONArray data = jsonObject.getJSONArray("values");
                           //llenarTabla(data);


                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("Error en servicio", error.getMessage());

            }
        })
        {

            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();

                //params.put("title", "foo");
                //params.put("body", "bar");
                //params.put("userId", "1");
                params.put("Content-Type","application/json");
                params.put("User-Agent","PostmanRuntime/7.29.2");
                params.put("Accept","* / *");
                params.put("Accept-Encoding","gzip, deflate, br");
                params.put("Connection","keep-alive");
                params.put("Access-Control-Allow-Origin","*");
                params.put("strRole", "123");
                params.put("strDateTime", "233");
                return params;
            }
        };
        Volley.newRequestQueue(this).add(postRequest);




   }


    private void servicioGet(){

        String url = "https://pokeapi.co/api/v2/pokemon/ditto";
        StringRequest postRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    //Toast.makeText(this,)

                    JSONArray data = jsonObject.getJSONArray("abilities");
                    Log.d("lo que llega del server metodo GET", String.valueOf(data));
                    llenarTablaGET(data);


                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("Error en servicio", error.getMessage());

            }
        });
        Volley.newRequestQueue(this).add(postRequest);

    }

    private void getDataUser(){

        System.out.println("el valor de nombre  "+ nombre);

        HashMap<String, String> params = new HashMap<>();
        params.put("Content-Type","application/json");
        params.put("strUserName", correo);

        String url = urlServicios+"GetUserByUserName";

        JsonObjectRequest sol = new JsonObjectRequest(Request.Method.POST, url, new JSONObject((params)), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    JSONObject reg = response.getJSONObject("values");
                    strRole = reg.getString("roleCode");
                    strUserCode = reg.getString("code");

                    getRoll();
                    getServices(strRole);

                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

               // Log.d("Error en el response del servicio", error.getMessage());
            }
        });

        Volley.newRequestQueue(this).add(sol);

    }

    private void getServices(String strRole){


        HashMap<String, String> params = new HashMap<>();
        params.put("Content-Type","application/json");
        params.put("strRole", strRole);
        //params.put("strDateTime", fecha);//"2023/02/25"
        //return params;
        String url = urlServicios+"GetServicesByRole";

        JsonObjectRequest sol = new JsonObjectRequest(Request.Method.POST, url, new JSONObject((params)), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    dataService = response.getJSONArray("values");
                    llenarTablaPOST(dataService);

                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // Log.d("Error en el response del servicio", error.getMessage());
            }
        });

        Volley.newRequestQueue(this).add(sol);

    }

    private void aceptarServicio(String intServiceID, String strUserCode){

        HashMap<String, String> params = new HashMap<>();
        params.put("Content-Type","application/json");
        params.put("intServiceID", intServiceID);
        params.put("strUserCode", strUserCode);
        //return params;
        String url = urlServicios+"AcceptService";

        JsonObjectRequest sol = new JsonObjectRequest(Request.Method.POST, url, new JSONObject((params)), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Toast.makeText(getApplicationContext(),"Se Acepto el servicio",Toast.LENGTH_LONG).show();
                finish();
                startActivity(getIntent());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(),"No fue posible aceptar el servicio, intente nuevamente",Toast.LENGTH_LONG).show();


                // Log.d("Error en el response del servicio", error.getMessage());
            }
        });

        Volley.newRequestQueue(this).add(sol);

    }

    private void getRoll(){

        String url = "https://innboxservices.azurewebsites.net/api/Service/GetAllRoles";
        StringRequest postRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray data = jsonObject.getJSONArray("values");


                    System.out.println("la data "+ data);

                        try {

                            for (int i = 0; i < data.length(); i++) {
                                //System.out.println("codigo de rol " + data.getJSONObject(i).getString("roleCode") + " codigo de rol de usuario " + strRole);

                                //System.out.println(data.getJSONObject(i).getString("roleCode").equals(strRole));

                                if (data.getJSONObject(i).getString("roleCode").equals(strRole)) {
                                    rolUsuario = data.getJSONObject(i).getString("role");

                                    if(nombre.equals("")){
                                        showNombre.setText("Correo: "+correo );
                                        rol.setText(" Rol: "+rolUsuario);
                                    }else{
                                        showNombre.setText("Nombre: "+nombre );
                                        rol.setText(" Rol: "+rolUsuario);
                                    }

                                    break;
                                }
                            }

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }




                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("Error en servicio", error.getMessage());

            }
        });
        Volley.newRequestQueue(this).add(postRequest);

    }


    private void llenarTablaGET(JSONArray data ){

        tablaServicios = findViewById(R.id.tablaServicios);

        for (int i = 0; i < data.length(); i++){

            try {
                View registro = LayoutInflater.from(this).inflate(R.layout.table_row_innbox,null,false);
                TextView colNombre = registro.findViewById(R.id.colNombre);
                //TextView colEmail = registro.findViewById(R.id.colEmail);
                Button showService = registro.findViewById(R.id.showService);

                JSONObject reg = data.getJSONObject(i).getJSONObject("ability");

                colNombre.setText(reg.getString("name"));
                //colEmail.setText(reg.getString("url"));
                showService.setId(i);

                tablaServicios.addView(registro);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void llenarTablaPOST(JSONArray data ){

        tablaServicios = findViewById(R.id.tablaServicios);

            try {
                TextView serviciosDisponible = findViewById(R.id.serviciosDisponible);
                if(data.length() == 0){
                    serviciosDisponible.setText("No hay servicios");
                }else{

                    serviciosDisponible.setText("Servicios disponibles");

                    for (int i = 0; i < data.length(); i++) {
                        View registro = LayoutInflater.from(this).inflate(R.layout.table_row_innbox, null, false);
                        TextView colNombre = registro.findViewById(R.id.colNombre);
                        //TextView colEmail = registro.findViewById(R.id.colEmail);
                        showService = registro.findViewById(R.id.showService);

                        JSONObject reg = data.getJSONObject(i);

                        colNombre.setText(reg.getString("serviceType"));
                        //colEmail.setText(reg.getString("status"));
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


            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        TextView mostrarCom = findViewById(R.id.mostrarCom);

        mostrarCom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idBtnServicio = 0;//saberIdBtn(view);
                mostrarDialogo();
            }
        });

        TextView irAceptados = findViewById(R.id.irAceptados);

        irAceptados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irAceptados();
            }
        });

    }

    private void mostrarDialogo(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.dialogoservicio,null);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();

        JSONObject as = null;

        try {

            Button btnAceptar = view.findViewById(R.id.btnAceptar);
            Button btnRechazar = view.findViewById(R.id.btnRechazar);
            Button btnContraoferta = view.findViewById(R.id.btnContraoferta);
            EditText valorContraoferta = view.findViewById(R.id.valorContraoferta);
            valorContraoferta.getText().toString().trim();

            TextView dinero = view.findViewById(R.id.dinero);
            TextView calendario = view.findViewById(R.id.calendario);
            TextView horasdeTrabajo = view.findViewById(R.id.horasdeTrabajo);
            TextView direccion = view.findViewById(R.id.direccion);
            TextView horaEntrada = view.findViewById(R.id.horaEntrada);
            TextView tipoServicio = view.findViewById(R.id.tipoServicio);
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

            intServiceID = as.getString("code");

            btnAceptar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    aceptarServicio(intServiceID,strUserCode);


                    dialog.dismiss();
                }
            });

            btnRechazar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(),"Se rechazó el servicio",Toast.LENGTH_LONG).show();

                    dialog.dismiss();
                }
            });

            btnContraoferta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(),"Se envió la contraoferta",Toast.LENGTH_LONG).show();

                    dialog.dismiss();
                }
            });



        } catch (JSONException  e) {
            Log.d("***", "mostrarDialogo: error" + e.getMessage());
            throw new RuntimeException(e);
        } catch (ParseException e) {
            Log.d("***", "mostrarDialogo: error 2222" + e.getMessage());
            throw new RuntimeException(e);
        }


    }

    public  int saberIdBtn(View v){
        return v.getId();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.Aceptados:
                irAceptados();
                Toast.makeText(this, "Buscando servicios", Toast.LENGTH_LONG).show();
                break;

            case R.id.salir2:
                mAuth.signOut();
                irMain();
                Toast.makeText(this, "Cerrando Sesión ", Toast.LENGTH_LONG).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void irAceptados() {

        Intent intent = new Intent(HomeActivity.this, ServicioAceptado.class);

        intent.putExtra("nombre", nombre);
        intent.putExtra("correo", correo);
        intent.putExtra("strUserCode", strUserCode);


        startActivity(intent);
        //com este metodo iniciamos y con
        finish();
        //evitamos el regreso a esta actividad

    }


}