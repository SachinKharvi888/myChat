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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    EditText name,email,password;
    Button registerbtn;
    TextView log;

    FirebaseAuth auth;
    DatabaseReference reference,newdata;
    ProgressBar prog1;
    FirebaseUser firebaseUser,fuser;
    String uniquename;

    @Override
    protected void onStart() {
        super.onStart();

        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser !=null){
            Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            // Toast.makeText(RegisterActivity.this,"Hello"+firebaseUser.getUid(),Toast.LENGTH_SHORT).show();

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        name=findViewById(R.id.usertxt);
        email=findViewById(R.id.emailtxt);
        password=findViewById(R.id.passwordtxt);
        registerbtn=findViewById(R.id.regiterbtn);
        prog1=findViewById(R.id.progress1);
        newdata=FirebaseDatabase.getInstance().getReference();
        auth=FirebaseAuth.getInstance();

        log=findViewById(R.id.log);

        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prog1.setVisibility(View.VISIBLE);
                String txtname= name.getText().toString();
                String txtemail= email.getText().toString();
                String txtpassword= password.getText().toString();

                if (!validateUsername()) {
                    prog1.setVisibility(View.GONE);

                    return;
                }
                else if (TextUtils.isEmpty(txtname)||TextUtils.isEmpty(txtemail)||TextUtils.isEmpty(txtpassword)) {
                    prog1.setVisibility(View.GONE);

                    Toast.makeText(RegisterActivity.this," all filed are reguired",Toast.LENGTH_SHORT).show();
                    return;

                }else  if (txtpassword.length()  < 6){
                    prog1.setVisibility(View.GONE);

                    Toast.makeText(RegisterActivity.this,"password must at 6 chara",Toast.LENGTH_SHORT).show();
                    return;

                }else {

                    register(txtname,txtemail,txtpassword);

                }
            }
        });


    }



    private void register(String username1,String email,String password){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    String userid =firebaseUser.getUid();
                    reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                    HashMap<String ,String> hashMap = new HashMap<>();
                    hashMap.put("id",userid);
                    hashMap.put("username",username1);
                    hashMap.put("imageURL","default");
                    hashMap.put("Bio","default");
                    hashMap.put("status","Offline");

                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                               // Personaldetails();
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("unique");
                                reference.child(username1).setValue(true);

                                Privacy();
                                //userBlock();
                                Toast.makeText(RegisterActivity.this,"suecss",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                            else {
                                //Toast.makeText(RegisterActivity.this,"3",Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }else{
                    prog1.setVisibility(View.INVISIBLE);
                    Toast.makeText(RegisterActivity.this,"you can't use email and password",Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    private void Personaldetails (){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("PersonalDetails");

        String gendertxt;
        FirebaseUser firebaseUser = auth.getCurrentUser();
        Date date = new Date();
        String details = reference.push().getKey();
        String userid=firebaseUser.getUid();
        HashMap<String ,String> hashMap = new HashMap<>();
        hashMap.put("DetailsID",details);
        hashMap.put("id",userid);
        hashMap.put("nickName","");
        hashMap.put("email","");
        hashMap.put("Nationality","");
        hashMap.put("DOB","");
        hashMap.put("qualification","");
        hashMap.put("gender","");
        reference.child(firebaseUser.getUid()).setValue(hashMap);

    }
    private void Privacy(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Privacy");
        FirebaseUser firebaseUser = auth.getCurrentUser();
        String details = reference.push().getKey();
        String userid=firebaseUser.getUid();
        HashMap<String ,String> hashMap = new HashMap<>();
        hashMap.put("Privacy","OFF");
        reference.child(firebaseUser.getUid()).setValue(hashMap);

        //block();

    }
    private Boolean validateUsername() {
        String val = name.getText().toString();
        String noWhiteSpace = "\\A\\w{4,20}\\z";
        if (val.isEmpty()) {
            name.setError("Field cannot be empty");
            return false;
        }
        else if (val.length() >= 15) {
            name.setError("Username too long");
            return false;
        } else if (!val.matches(noWhiteSpace)) {
            name.setError("Spaces are not allowed");
            return false;
        } else {
            name.setError(null);
            // username.setEnabled(false);
            return true;
        }
    }


}