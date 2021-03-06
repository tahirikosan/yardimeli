package com.tahirabuzetoglu.yardimeli.ui.login;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.tahirabuzetoglu.yardimeli.data.entity.User;
import com.tahirabuzetoglu.yardimeli.ui.main.FeedActivity;
import com.tahirabuzetoglu.yardimeli.R;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;
    private EditText etPhoneNumber;
    private LoginViewModel loginViewModel;
    ProgressDialog progressDialog;

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);

        initLoginViewModel();

        btnLogin = findViewById(R.id.btnLogin);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);

        progressDialog = new ProgressDialog(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = etPhoneNumber.getText().toString();
                if (phoneNumber.isEmpty())
                    Toast.makeText(LoginActivity.this, "Enter your phone number", Toast.LENGTH_SHORT).show();
                else {
                    //verify phone number
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            "+90"+phoneNumber, 60, TimeUnit.SECONDS, LoginActivity.this,
                            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                                    signInUser(phoneAuthCredential);
                                }

                                @Override
                                public void onVerificationFailed(FirebaseException e) {
                                    Log.d(TAG, "onVerificationFailed:"+e.getLocalizedMessage());
                                }

                                @Override
                                public void onCodeSent(final String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                    super.onCodeSent(verificationId, forceResendingToken);
                                    //
                                    Dialog dialog = new Dialog(LoginActivity.this);
                                    dialog.setContentView(R.layout.verify_popup);

                                    final EditText etVerifyCode = dialog.findViewById(R.id.etVerifyCode);
                                    Button btnVerifyCode = dialog.findViewById(R.id.btnVerifyOTP);
                                    btnVerifyCode.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String verificationCode = etVerifyCode.getText().toString();
                                            if(verificationId.isEmpty()) return;
                                            //create a credential
                                            PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verificationId,verificationCode);
                                            signInUser(credential);

                                            progressDialog.setMessage("Lütfen bekleyiniz");
                                            progressDialog.show();
                                        }
                                    });

                                    dialog.show();
                                }
                            });
                }
            }
        });

    }

    //sign user with auth phone
    private void signInUser(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            createUserInDB(etPhoneNumber.getText().toString().trim());
                        }else {
                            Log.d(TAG, "onComplete:"+task.getException().getLocalizedMessage());
                        }
                    }
                });
    }

    private void createUserInDB(String phoneNumber){
        loginViewModel.createUserInDB(phoneNumber);
        loginViewModel.createdUserLive.observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if(user.isCreated() ||user.isLogedIn()){
                    Intent intent = new Intent(LoginActivity.this, FeedActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                progressDialog.cancel();
            }
        });
    }

    private void initLoginViewModel() {
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
    }
}