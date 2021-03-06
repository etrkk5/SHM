package projects.etrkk5.shm;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignUpEmployeeActivity extends AppCompatActivity implements View.OnClickListener{

    EditText editTextName;
    EditText editTextSurname;
    EditText editTextBranch;
    EditText editTextEmail;
    EditText editTextPassword;
    TextView textViewSignUp;
    Button buttonSignUp;
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_employee);


        editTextName = (EditText)findViewById(R.id.editTextName);
        editTextSurname = (EditText)findViewById(R.id.editTextSurname);
        editTextBranch = (EditText)findViewById(R.id.editTextBranch);
        editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        editTextPassword = (EditText)findViewById(R.id.editTextPassword);
        textViewSignUp = (TextView)findViewById(R.id.textViewSignUp);
        buttonSignUp = (Button)findViewById(R.id.buttonSignUp);

        buttonSignUp.setOnClickListener(this);
        textViewSignUp.setOnClickListener(this);
    }

    public void RegisterUser(){
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String name = editTextName.getText().toString().trim();
        final String branch = editTextBranch.getText().toString().trim();

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        if(email.isEmpty()){
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        if(name.isEmpty()){
            editTextName.setError("Name is required");
            editTextName.requestFocus();
            return;
        }

        if(branch.isEmpty()){
            editTextBranch.setError("Branch id is required");
            editTextBranch.requestFocus();
            return;
        }

        if(password.length() < 6){
            editTextPassword.setError("Minimum length of password should be 6!");
            editTextPassword.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    String uid = currentUser.getUid();

                    HashMap<String, String> userMap = new HashMap<>();
                    userMap.put("email", email);
                    userMap.put("name", name);
                    userMap.put("branch", branch);
                    userMap.put("status", "true");
                    userMap.put("uid", uid);

                    db.collection("users").add(userMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                        }
                    });

                    Toast.makeText(getApplicationContext(), "User registered succesfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpEmployeeActivity.this, ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }else{
                    if(task.getException() instanceof FirebaseAuthUserCollisionException){
                        Toast.makeText(getApplicationContext(),"User is already registered!", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == buttonSignUp.getId()){
            RegisterUser();
            startActivity(new Intent(SignUpEmployeeActivity.this, ProfileActivity.class));
        }

        if(v.getId() == textViewSignUp.getId()){
            startActivity(new Intent(SignUpEmployeeActivity.this, LoginActivity.class));
        }


    }
}
