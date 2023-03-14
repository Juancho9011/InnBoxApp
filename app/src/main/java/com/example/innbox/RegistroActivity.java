package com.example.innbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistroActivity extends AppCompatActivity {


    //Variables
    //Componentes
    EditText InputEmailR;
    EditText InputPassR;
    EditText InputPassRConfirma;
    Button btnRegistroR;
    TextView textViewRtaR , regresarAlLogin;

    ///Firebase
    FirebaseAuth mAuth;

    private String email, pass, passConfirma;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);


        InputEmailR = findViewById(R.id.InputEmailR);
        InputPassR = findViewById(R.id.InputPassR);
        InputPassRConfirma = findViewById(R.id.InputPassRConfirma);
        btnRegistroR = findViewById(R.id.btnRegistroR);
        textViewRtaR = findViewById(R.id.textViewRtaR);
        regresarAlLogin = findViewById(R.id.regresarAlLogin);

        regresarAlLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irMain();
            }
        });

        mAuth = FirebaseAuth.getInstance();

        btnRegistroR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = InputEmailR.getText().toString().trim();
                pass = InputPassR.getText().toString().trim();
                passConfirma = InputPassRConfirma.getText().toString().trim();

                Log.d("Email", email );
                Log.d("contraseña", pass );
                Log.d("contraseña 2", passConfirma );

                if(email.isEmpty() || pass.isEmpty() || passConfirma.isEmpty()){

                    textViewRtaR.setText("Ingrese valores");
                    textViewRtaR.setTextColor(Color.RED);


                }else if(!email.isEmpty() && !pass.isEmpty() && !passConfirma.isEmpty()){
                    //verificar qeu sea email


                    if(emailValidador(email)){//

                        if(pass.equals(passConfirma)){

                            if(passConfirma.length() > 6){

                                mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        if(task.isSuccessful()){
                                            //se creo  la cuenta bien

                                            textViewRtaR.setText("Bien!!!");
                                            textViewRtaR.setTextColor(Color.GREEN);

                                            irMain();

                                        }else{
                                            //la cuenta ya existe
                                            textViewRtaR.setText("La cuenta ya existe");
                                            textViewRtaR.setTextColor(Color.RED);
                                        }

                                    }
                                });

                            }else{
                                textViewRtaR.setText("La contraseña debe ser mayor a 6 caracteres");
                                textViewRtaR.setTextColor(Color.RED);
                            }


                        }else{
                            textViewRtaR.setText("Las contraseñas no son iguales");
                            textViewRtaR.setTextColor(Color.RED);
                        }

                    }else{
                        textViewRtaR.setText("Ingrese un email valido");
                        textViewRtaR.setTextColor(Color.RED);
                    }
                }

            }
        });

    }

    private void irMain() {

        Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean emailValidador(String email){
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();

    }



}