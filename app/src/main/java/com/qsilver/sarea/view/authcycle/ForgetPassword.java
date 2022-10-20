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
import com.google.firebase.auth.FirebaseAuth;
import com.qsilver.sarea.helper.HelperMethod;
import com.qsilver.sarea.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgetPassword extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextInputLayout email;
    private TextView tv_back_sign_up;
    private ImageView Done;
    private ProgressBar forgetpass_progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        email = findViewById(R.id.forgetpassword_out_et_email);
        tv_back_sign_up = findViewById(R.id.tv_back_sign_up);
        Done = findViewById(R.id.forgetpassword_btn_send);
        forgetpass_progressbar = findViewById(R.id.forgetpass_progressbar);
        mAuth=FirebaseAuth.getInstance();

        Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitButtonTask();

            }
        });


    }

    private void submitButtonTask() {
        forgetpass_progressbar.setVisibility(View.VISIBLE);
        String getEmail = email.getEditText().getText().toString();
        Pattern p = Pattern.compile(HelperMethod.EMAIL_PATTERN);
        Matcher m = p.matcher(getEmail);


        if (getEmail.equals("") || getEmail.length() == 0) {
            Toast.makeText(ForgetPassword.this, "من فضلك أدخل البريد الإلكترونى", Toast.LENGTH_LONG).show();


        } else if (!m.find()) {
            Toast.makeText(ForgetPassword.this, "من فضلك أدخل بريد إلكترونى صحيح", Toast.LENGTH_LONG).show();

        } else if (!isNetworkAvailable(ForgetPassword.this)) {
            Toast.makeText(ForgetPassword.this, "من فضلك تأكد من الإتصال بالإنترنت", Toast.LENGTH_LONG).show();


        } else {
            mAuth.sendPasswordResetEmail(getEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {
                        forgetpass_progressbar.setVisibility(View.GONE);
                        Toast.makeText(ForgetPassword.this, "يمكنك إعادة تعيين كلمة المرور عن طريق البريد الإلكترونى", Toast.LENGTH_LONG).show();
                        Intent mainIntent = new Intent(ForgetPassword.this, LoginActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(mainIntent);
                        overridePendingTransition(R.anim.left_enter, R.anim.right_out);

                    } else {
                        forgetpass_progressbar.setVisibility(View.GONE);
                        String massage = task.getException().getMessage();
                        Toast.makeText(getApplicationContext(), massage + "حدث خطأ ما , حاول لاحقاً - ", Toast.LENGTH_LONG).show();

                    }
                }
            });
        }


    }
}