package ir.guardianapp.guardian_v2;

import androidx.appcompat.app.AppCompatActivity;
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
import ir.guardianapp.guardian_v2.database.SharedPreferencesManager;
import ir.guardianapp.guardian_v2.extras.GPSAndInternetChecker;
import ir.guardianapp.guardian_v2.extras.Network;
import ir.guardianapp.guardian_v2.models.User;
import ir.guardianapp.guardian_v2.network.MessageResult;
import ir.guardianapp.guardian_v2.network.ThreadGenerator;

import static java.lang.Thread.sleep;

public class LoginActivity extends AppCompatActivity {

    private ProgressBar loginProgress;
    private boolean hidePassword = true;

    private Handler handler;
    private static boolean canUpdate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // MAKE IT FULL SCREEN
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.appThemeColor));
        }

        // PASSWORD
        Button loginButton = findViewById(R.id.loginButton);
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
                loginButton.performClick();
            }
            return false;
        });

        loginProgress = findViewById(R.id.progressBar);
        TextView phoneNumTextView = findViewById(R.id.phoneNum);
        TextView messageTextLogin = findViewById(R.id.messageTextLogin);
        loginButton.setOnClickListener(v -> {
//            hideSoftKeyboard(this);
            if(phoneNumTextView.getText().length() != 11) {
                messageTextLogin.setText("شماره همراه باید ۱۱ رقم باشد!");
                messageTextLogin.setTextColor(this.getResources().getColor(R.color.colorNegativeError));
            } else if(!phoneNumTextView.getText().toString().startsWith("09")) {
                messageTextLogin.setText("شماره همراه وارد شده صحیح نمی باشد.");
                messageTextLogin.setTextColor(this.getResources().getColor(R.color.colorNegativeError));
            } else if(passwordTextView.getText().length() == 0) {
                messageTextLogin.setText("رمز عبور نمی تواند خالی باشد.");
                messageTextLogin.setTextColor(this.getResources().getColor(R.color.colorNegativeError));
            } else {
                handler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == MessageResult.SUCCESSFUL) {
                            loginProgress.setVisibility(View.INVISIBLE);
                            canUpdate = true;
                            loginProgress.setProgress(loginProgress.getProgress());
                            messageTextLogin.setText("لطفا چند لحظه منتظر بمانید.");
                            messageTextLogin.setTextColor(LoginActivity.this.getResources().getColor(R.color.colorPositiveError));

                            Intent i = new Intent(LoginActivity.this, MainMenuActivity.class);
                            startActivity(i);
                            finish();
                        } else if(msg.what == MessageResult.THIS_PHONE_NOT_REGISTERED) {
                            loginProgress.setVisibility(View.INVISIBLE);
                            canUpdate = true;
                            messageTextLogin.setText("شماره همراه صحیح نمی باشد.");
                            messageTextLogin.setTextColor(LoginActivity.this.getResources().getColor(R.color.colorNegativeError));
                        } else if(msg.what == MessageResult.PHONE_AND_PASSWORD_DOES_NOT_MATCH) {
                            loginProgress.setVisibility(View.INVISIBLE);
                            messageTextLogin.setText("شماره همراه یا رمز عبور صحیح نمی باشد.");
                            messageTextLogin.setTextColor(LoginActivity.this.getResources().getColor(R.color.colorNegativeError));
                        } else if(msg.what == MessageResult.FAILED) {
                            loginProgress.setVisibility(View.INVISIBLE);
                            canUpdate = true;
                            messageTextLogin.setText("لطفا دوباره تلاش کنید.");
                            messageTextLogin.setTextColor(LoginActivity.this.getResources().getColor(R.color.colorNegativeError));
                        }  else {
                            loginProgress.setVisibility(View.INVISIBLE);
                            canUpdate = true;
                            messageTextLogin.setText("سرور پاسخگو نمی باشد؛ لطفا چند دقیقه دیگر تلاش کنید.");
                            messageTextLogin.setTextColor(LoginActivity.this.getResources().getColor(R.color.colorNegativeError));
                        }
                    }
                };

                if (Network.isNetworkAvailable(this)) {   // connected to internet
                    if(canUpdate) {
                        canUpdate = false;
                        loginProgress.setVisibility(View.VISIBLE);
                        MainActivity.executorService.submit(ThreadGenerator.loginUser(phoneNumTextView.getText().toString(), passwordTextView.getText().toString(), handler));
                    }
                } else {
                    messageTextLogin.setText("اتصال شما به اینترنت برقرار نمی باشد.");
                    messageTextLogin.setTextColor(LoginActivity.this.getResources().getColor(R.color.colorNegativeError));
                }
            }
        });


        Button login2registerButton = findViewById(R.id.login2registerButton);
        login2registerButton.setOnClickListener(v -> {
            Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
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
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "در صورت رد کردن درخواست موقعیت مکانی، گاردین نمی تواند از نقشه استفاده کند!", Toast.LENGTH_SHORT).show());
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