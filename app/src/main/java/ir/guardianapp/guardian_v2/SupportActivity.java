package ir.guardianapp.guardian_v2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class SupportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        // MAKE IT FULL SCREEN
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.appThemeColor));
        }

        // website
        TextView link = findViewById(R.id.websiteText);
        String linkText = "<a href='http://guardianapp.ir'>http://guardianapp.ir</a>";
        link.setText(Html.fromHtml(linkText));
        link.setMovementMethod(LinkMovementMethod.getInstance());

        // email / telegram / whatsapp
        Button emailBtn = findViewById(R.id.emailButton);
        emailBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri data = Uri.parse("mailto:support@guardianapp.ir?subject=" + Uri.encode("مشکل در استفاده از برنامه"));
            intent.setData(data);
            startActivity(intent);
        });

        Button telegramBtn = findViewById(R.id.telegramButton);
        telegramBtn.setOnClickListener(v -> {
            Intent telegram = new Intent(Intent.ACTION_VIEW , Uri.parse("https://telegram.me/GuardianApp_Support"));
            startActivity(telegram);
        });

        Button whatsappBtn = findViewById(R.id.whatsappButton);
        whatsappBtn.setOnClickListener(v -> {
            String number = "+98 9305006036";
            String url = "https://api.whatsapp.com/send?phone="+number;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Intent i = new Intent(SupportActivity.this, MainDrivingActivity.class);
            startActivity(i);
            finish();
        });
    }
}