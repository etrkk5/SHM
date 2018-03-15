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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignUpCompanyActivity extends AppCompatActivity implements View.OnClickListener{
    EditText editTextName;
    EditText editTextLocation;
    EditText editTextEmail;
    EditText editTextPassword;
    TextView textViewSignUp;
    Button buttonSignUp;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_company);

        editTextName = (EditText)findViewById(R.id.editTextName);
        editTextLocation = (EditText)findViewById(R.id.editTextLocation);
        editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        editTextPassword = (EditText)findViewById(R.id.editTextPassword);
        textViewSignUp = (TextView)findViewById(R.id.textViewSignUp);
        buttonSignUp = (Button)findViewById(R.id.buttonSignUp);

        buttonSignUp.setOnClickListener(this);
        textViewSignUp.setOnClickListener(this);


    }

    public void RegisterCompany(){

        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String name = editTextName.getText().toString().trim();
        final String location = editTextLocation.getText().toString().trim();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

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

        if(location.isEmpty()){
            editTextLocation.setError("Location is required");
            editTextLocation.requestFocus();
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
                    final String uid = currentUser.getUid();

                    final HashMap<String, String> companiesDetailsMap = new HashMap<>();

                    companiesDetailsMap.put("name", name);
                    companiesDetailsMap.put("location",location);
                    companiesDetailsMap.put("status", "true");
                    companiesDetailsMap.put("uid", uid);

                    db.collection("auth-companies-details").add(companiesDetailsMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            final String companyId = documentReference.getId();

                            final HashMap<String, String> branchesMap = new HashMap<>();

                            branchesMap.put("company", companyId);
                            branchesMap.put("status", "true");

                            db.collection("auth-branches").add(branchesMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    final String branchId = documentReference.getId();

                                    final HashMap<String, String> companiesMap = new HashMap<>();

                                    companiesMap.put("branch", branchId);
                                    companiesMap.put("status", "true");

                                    db.collection("auth-companies").add(companiesMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            final HashMap<String, String> branchesDetailsMap = new HashMap<>();

                                            branchesDetailsMap.put("company", companyId);
                                            branchesDetailsMap.put("branch", branchId);
                                            branchesDetailsMap.put("status", "true");

                                            db.collection("auth-branches-details").add(branchesDetailsMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    final HashMap<String, String> usersMap = new HashMap<>();

                                                    usersMap.put("company", companyId);
                                                    usersMap.put("branch", branchId);

                                                db.collection("auth-users").add(usersMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        final HashMap<String, String> usersDetailsMap = new HashMap<>();

                                                        usersDetailsMap.put("userId", uid);
                                                        usersDetailsMap.put("userStatus", "true");
                                                        usersDetailsMap.put("company", companyId);
                                                        usersDetailsMap.put("branch", branchId);


                                                        db.collection("auth-users-details").add(usersDetailsMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                            @Override
                                                            public void onSuccess(DocumentReference documentReference) {

                                                            }
                                                        });
                                                    }
                                                });
                                                }
                                            });
                                        }
                                    });

                                }
                            });
                        }
                    });

                    Toast.makeText(getApplicationContext(), "Company registered succesfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpCompanyActivity.this, ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }else{
                    if(task.getException() instanceof FirebaseAuthUserCollisionException){
                        Toast.makeText(getApplicationContext(),"Companty is already registered!", Toast.LENGTH_SHORT).show();
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
            RegisterCompany();
            startActivity(new Intent(SignUpCompanyActivity.this, ProfileActivity.class));
        }

        if(v.getId() == textViewSignUp.getId()){
            startActivity(new Intent(SignUpCompanyActivity.this, LoginActivity.class));
        }
    }
}
