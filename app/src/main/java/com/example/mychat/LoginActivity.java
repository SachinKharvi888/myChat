package com.example.mychat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class LoginActivity extends AppCompatActivity {
    EditText email,password;
    Button loginbtn;
    TextView reg;

    FirebaseAuth auth;
    DatabaseReference reference,newdata;
    ProgressBar prog1;
    FirebaseUser firebaseUser,fuser;
    ProgressBar pro2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email=findViewById(R.id.emailtxt1);
        password=findViewById(R.id.passwordtxt1);
        loginbtn=findViewById(R.id.loginbtn);
        pro2=findViewById(R.id.progress2);
        reg=findViewById(R.id.reg);
        auth=FirebaseAuth.getInstance();


        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });


        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               pro2.setVisibility(View.VISIBLE);
                String txtemail = email.getText().toString();
                String txtpassword = password.getText().toString();
                if (TextUtils.isEmpty(txtemail) || TextUtils.isEmpty(txtpassword)) {
                    Toast.makeText(LoginActivity.this, "all the fileds are required", Toast.LENGTH_SHORT).show();
                } else {
                    auth.signInWithEmailAndPassword(txtemail, txtpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                pro2.setVisibility(View.GONE);
                                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                Toast.makeText(LoginActivity.this, "Login sucessful", Toast.LENGTH_SHORT).show();
                                finish();

                            }else {
                                Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                pro2.setVisibility(View.GONE);

                            }
                        }
                    });
                }
            }
        });
    }
}