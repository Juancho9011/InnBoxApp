package com.example.innbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

///////////////GOOGLE


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


///////////FIN GOOGLE

public class MainActivity extends AppCompatActivity {


    //Componentes
    EditText InputEmail;
    EditText InputPass;
    Button btnIniciar;
    TextView textViewRta;
    TextView textViewIrRegistro;

    //Firebase
    FirebaseAuth mAuth;

    //Otro
    private String email;
    private String pass;


    /////GOOGLE
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;

    public String[] dataClient;

    SignInButton btnGoogle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InputEmail = findViewById(R.id.InputEmail);
        InputPass = findViewById(R.id.InputPass);
        btnIniciar = findViewById(R.id.btnIniciar);
        textViewRta = findViewById(R.id.textViewRta);

        textViewIrRegistro = findViewById(R.id.textViewIrRegistro);

        mAuth = FirebaseAuth.getInstance();

        textViewIrRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irRegistro();
            }
        });


        btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = InputEmail.getText().toString().trim();
                pass = InputPass.getText().toString().trim();

                if(email.isEmpty() || pass.isEmpty()){
                    textViewRta.setText("Ingrese correo y contraseña");
                    textViewRta.setTextColor(Color.RED);

                }else{

                    if(emailValidador(email)){//emailValidador(email)
                        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    textViewRta.setText("Correcto");
                                    textViewRta.setTextColor(Color.GREEN);

                                    irHome("",email);
                                    //Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                    //startActivity(intent);
                                }else{
                                    textViewRta.setText("User o pass Incorrecto");
                                    textViewRta.setTextColor(Color.RED);
                                }
                            }
                        });

                    }else{
                        textViewRta.setText("Ingrese correo valido");
                        textViewRta.setTextColor(Color.RED);
                    }

                }


            }
        });



        //////////GOOGLE


        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btnGoogle = findViewById(R.id.btnGoogle);

        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
        // [END config_signin]

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]



        ////FIN GOOGLE

    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser usuario = mAuth.getCurrentUser();
        if(usuario != null){
            irHome(usuario.getDisplayName(),usuario.getEmail());
        }


        /////GOOGLE

        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);


        ////FIN GOOGLE
    }

    private void irRegistro() {

        Intent intent = new Intent(MainActivity.this, RegistroActivity.class);
        startActivity(intent);
    }


    private void irHome(String nombre, String correo) {

        Intent intent = new Intent(MainActivity.this, HomeActivity.class);

        intent.putExtra("nombre", nombre);
        intent.putExtra("correo", correo);

        startActivity(intent);
        //com este metodo iniciamos y con
        finish();
        //evitamos el regreso a esta actividad

    }

    private boolean emailValidador(String email){
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        //"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@\"+ \"[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();

    }




    ////////////////AUTENTICACION CON GOOGLE


    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getDisplayName());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);

                textViewRta.setText(e.getMessage().toString());
                textViewRta.setTextColor(Color.RED);
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser usuario = mAuth.getCurrentUser();
                            irHome(usuario.getDisplayName(),usuario.getEmail());
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {

                            textViewRta.setText(task.getException().toString());
                            textViewRta.setTextColor(Color.RED);
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }
    // [END auth_with_google]

    // [START signin]
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

    private void updateUI(FirebaseUser user) {

        user = mAuth.getCurrentUser();

        if(user != null){

            irHome(mAuth.getCurrentUser().getDisplayName(), mAuth.getCurrentUser().getEmail());
        }


    }

    ///////////////FIN GOOGLE


}




