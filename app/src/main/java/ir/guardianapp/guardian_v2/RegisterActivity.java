package ir.guardianapp.guardian_v2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ir.guardianapp.guardian_v2.PasswordManager.AsteriskPasswordTransformationMethod;
import ir.guardianapp.guardian_v2.PasswordManager.DoNothingTransformationMethod;
import ir.guardianapp.guardian_v2.extras.Network;
import ir.guardianapp.guardian_v2.network.MessageResult;
import ir.guardianapp.guardian_v2.network.ThreadGenerator;

public class RegisterActivity extends AppCompatActivity {

    private ProgressBar registerProgress;
    private boolean hidePassword = true;

    private Handler handler;
    private static boolean canUpdate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // MAKE IT FULL SCREEN
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.appThemeColor));
        }

        // PASSWORD
        Button registerButton = findViewById(R.id.registerButton);
        EditText passwordTextView = findViewById(R.id.password);
        passwordTextView.setTransformationMethod(new AsteriskPasswordTransformationMethod());
        ImageButton showPasswordBtn =  findViewById(R.id.passwordLock);
        showPasswordBtn.setOnClickListener(v -> {
            if(hidePassword) {
                passwordTextView.setTransformationMethod(new DoNothingTransformationMethod());
                showPasswordBtn.setBackgroundResource(R.drawable.padlock1);
                hidePassword = false;
            } else {
                passwordTextView.setTransformationMethod(new AsteriskPasswordTransformationMethod());
                showPasswordBtn.setBackgroundResource(R.drawable.padlock2);
                hidePassword = true;
            }

        });
        passwordTextView.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                registerButton.performClick();
            }
            return false;
        });


        registerProgress = findViewById(R.id.progressBar);
        TextView usernameTextView = findViewById(R.id.username);
        TextView phoneNumTextView = findViewById(R.id.phoneNum);
        TextView messageTextRegister = findViewById(R.id.messageTextRegister);
        registerButton.setOnClickListener(v -> {
//            hideSoftKeyboard(this);
            if(usernameTextView.getText().length() == 0) {
                messageTextRegister.setText("نام کاربر نمی تواند خالی باشد.");
                messageTextRegister.setTextColor(this.getResources().getColor(R.color.colorNegativeError));
            } else if(passwordTextView.getText().length() == 0) {
                messageTextRegister.setText("رمز عبور نمی تواند خالی باشد.");
                messageTextRegister.setTextColor(this.getResources().getColor(R.color.colorNegativeError));
            } if(phoneNumTextView.getText().length() != 11) {
                messageTextRegister.setText("شماره همراه باید ۱۱ رقم باشد!");
                messageTextRegister.setTextColor(this.getResources().getColor(R.color.colorNegativeError));
            } else if(!phoneNumTextView.getText().toString().startsWith("09")) {
                messageTextRegister.setText("شماره همراه وارد شده صحیح نمی باشد.");
                messageTextRegister.setTextColor(this.getResources().getColor(R.color.colorNegativeError));
            } else {
                handler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == MessageResult.SUCCESSFUL) {
                            registerProgress.setVisibility(View.INVISIBLE);
                            canUpdate = true;
                            registerProgress.setProgress(registerProgress.getProgress());
                            messageTextRegister.setText("لطفا چند لحظه منتظر بمانید.");
                            messageTextRegister.setTextColor(RegisterActivity.this.getResources().getColor(R.color.colorPositiveError));

                            Intent i = new Intent(RegisterActivity.this, MainMenuActivity.class);
                            startActivity(i);
                            finish();
                        } else if(msg.what == MessageResult.USERNAME_IS_NOT_UNIQUE) {
                            registerProgress.setVisibility(View.INVISIBLE);
                            canUpdate = true;
                            messageTextRegister.setText("نام کاربری وارد شده تکراری می باشد.");
                            messageTextRegister.setTextColor(RegisterActivity.this.getResources().getColor(R.color.colorNegativeError));
                        } else if(msg.what == MessageResult.PHONE_IS_NOT_UNIQUE) {
                            registerProgress.setVisibility(View.INVISIBLE);
                            canUpdate = true;
                            messageTextRegister.setText("شماره همراه وارد شده تکراری می باشد.");
                            messageTextRegister.setTextColor(RegisterActivity.this.getResources().getColor(R.color.colorNegativeError));
                        } else if(msg.what == MessageResult.FAILED) {
                            registerProgress.setVisibility(View.INVISIBLE);
                            canUpdate = true;
                            messageTextRegister.setText("لطفا دوباره تلاش کنید.");
                            messageTextRegister.setTextColor(RegisterActivity.this.getResources().getColor(R.color.colorNegativeError));
                        } else {
                            registerProgress.setVisibility(View.INVISIBLE);
                            canUpdate = true;
                            messageTextRegister.setText("سرور پاسخگو نمی باشد؛ لطفا چند دقیقه دیگر تلاش کنید.");
                            messageTextRegister.setTextColor(RegisterActivity.this.getResources().getColor(R.color.colorNegativeError));
                        }
                    }
                };
                if (Network.isNetworkAvailable(this)) {   // connected to internet
                    if(canUpdate) {
                        canUpdate = false;
                        registerProgress.setVisibility(View.VISIBLE);
                        MainActivity.executorService.submit(ThreadGenerator.registerUser(usernameTextView.getText().toString(), passwordTextView.getText().toString(), phoneNumTextView.getText().toString(), handler));
                    }
                } else {
                    messageTextRegister.setText("اتصال شما به اینترنت برقرار نمی باشد.");
                    messageTextRegister.setTextColor(RegisterActivity.this.getResources().getColor(R.color.colorNegativeError));
                }
            }
        });

        Button register2loginButton = findViewById(R.id.register2loginButton);
        register2loginButton.setOnClickListener(v -> {
            Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        });

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "در صورت رد کردن درخواست موقعیت مکانی، گاردین نمی تواند از نقشه استفاده کند!", Toast.LENGTH_SHORT).show());
            }
        };
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("در صورت رد کردن درخواست موقعیت مکانی، گاردین نمی تواند از نقشه استفاده کند! لطفا این دسترسی را به برنامه بدهید.")
                .setPermissions(android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .check();
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager.isAcceptingText()){
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(),
                    0
            );
        }
    }
}