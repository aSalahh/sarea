package com.qsilver.sarea.view.authcycle;

import static com.qsilver.sarea.helper.HelperMethod.isNetworkAvailable;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.qsilver.sarea.helper.HelperMethod;
import com.qsilver.sarea.view.MainActivity;
import com.qsilver.sarea.R;
import com.qsilver.sarea.view.WhichLoginActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout email, password;
    private TextView forgetPassword;
    private ImageView btnLogin;
    private TextView signUpTv;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.login_out_et_email);
        password = findViewById(R.id.login_out_et_password);
        forgetPassword = findViewById(R.id.forget_password);
        btnLogin = findViewById(R.id.login_btn_login);
        signUpTv = findViewById(R.id.tv_back_sign_up);
        progressBar = findViewById(R.id.login_progressbar);
        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidation();

            }
        });
        signUpTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, WhichLoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                overridePendingTransition(R.anim.left_out, R.anim.right_enter);


            }
        });
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, ForgetPassword.class);
                startActivity(i);
                overridePendingTransition(R.anim.left_enter, R.anim.right_out);

            }
        });

    }

    private void checkValidation() {
        String getEmail = email.getEditText().getText().toString();
        String getPassword = password.getEditText().getText().toString();

        Pattern p = Pattern.compile(HelperMethod.EMAIL_PATTERN);
        Matcher m = p.matcher(getEmail);

        if (getEmail.equals("") || getEmail.length() == 0 ||
                getPassword.equals("") || getPassword.length() == 0) {
            Toast.makeText(LoginActivity.this, "من فضلك إملئ جميع الحقول", Toast.LENGTH_LONG).show();

     }
//        else if (!m.matches()) {
//            Toast.makeText(LoginActivity.this, "من فضلك أدخل بريد إلكترونى صحيح", Toast.LENGTH_LONG).show();
//        }
        else if (!isNetworkAvailable(LoginActivity.this)) {
            Toast.makeText(LoginActivity.this, "من فضلك تأكد من الإتصال بالإنترنت", Toast.LENGTH_LONG).show();

        } else {
            progressBar.setVisibility(View.VISIBLE);
            logInUser(getEmail, getPassword);

        }


    }

    private void logInUser(String user_email, String password) {


        mAuth.signInWithEmailAndPassword(user_email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            progressBar.setVisibility(View.GONE);
                            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(mainIntent);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            String massage = task.getException().getMessage();
                            Toast.makeText(LoginActivity.this, massage, Toast.LENGTH_LONG).show();
                        }

                        // ...
                    }
                });












    }
}