package capstone.abang.com.Register;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import capstone.abang.com.Car_Owner.car_owner;
import capstone.abang.com.Models.UDFile;
import capstone.abang.com.Models.UHFile;
import capstone.abang.com.Models.USettings;
import capstone.abang.com.R;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    //Declaring all widgets
    private EditText etUserName;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnRegister;
    private ProgressDialog progressDialog;

    //firebase
    private DatabaseReference myRef;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Referencing widgets
        etUserName = findViewById(R.id.etLoginUsername);
        etPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        progressDialog = new ProgressDialog(this);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();

        //Methods
        setInit();

    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            Intent intent = new Intent(this, car_owner.class);
            startActivity(intent);
        }
    }

    private void setInit() {

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String holderUsername = etUserName.getText().toString();
                String holderPassword = etPassword.getText().toString();
                if(TextUtils.isEmpty(holderUsername) || TextUtils.isEmpty(holderPassword)) {
                    toastMethod("Input all fields!");
                }
                else {
                    progressDialog.setMessage("Logging in...");
                    progressDialog.show();
                    mAuth.signInWithEmailAndPassword(holderUsername, holderPassword)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        checkCurrentUser(user);
                                        Intent homeIntent = new Intent(getApplicationContext(), car_owner.class);
                                        startActivity(homeIntent);
                                        progressDialog.hide();
                                        finish();
                                    }
                                    else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        toastMethod("Authentication failed!");
                                        progressDialog.hide();
                                    }
                                }
                            });
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(registerIntent);
            }
        });
    }

    private void checkCurrentUser(FirebaseUser mUser) {
        mUser = mAuth.getCurrentUser();

    }

    private void toastMethod(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
