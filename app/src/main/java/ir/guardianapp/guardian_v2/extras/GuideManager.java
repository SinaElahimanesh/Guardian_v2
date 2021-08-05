package ir.guardianapp.guardian_v2.extras;

import android.app.Activity;
import android.view.View;

import ir.guardianapp.guardian_v2.MainActivity;
import ir.guardianapp.guardian_v2.R;
import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;

public class GuideManager {

    public static void showGuide(Activity activity){
        View[] allGuideViews = new View[6];
        String[] allGuideStrings = new String[6];
        allGuideViews[0] = activity.findViewById(R.id.restButton);
        allGuideViews[1] = activity.findViewById(R.id.parkingButton);
        allGuideViews[2] = activity.findViewById(R.id.alertMessageBox);
        allGuideViews[3] = activity.findViewById(R.id.driving_background);
        allGuideViews[4] = activity.findViewById(R.id.statistic);
        allGuideViews[5] = activity.findViewById(R.id.menuButton);

        allGuideStrings[0] = activity.getString(R.string.showcase_restbutton);
        allGuideStrings[1] = activity.getString(R.string.showcase_parkingbutton);
        allGuideStrings[2] = activity.getString(R.string.showcase_alertmessagetext);
        allGuideStrings[3] = activity.getString(R.string.showcase_drivingbackground);
        allGuideStrings[4] = activity.getString(R.string.showcase_statistics);
        allGuideStrings[5] = activity.getString(R.string.showcase_menubutton);

        GuideView.Builder guideView1 = getGuideForView(allGuideViews[0], allGuideStrings[0], activity);
        GuideView.Builder guideView2 = getGuideForView(allGuideViews[1], allGuideStrings[1], activity);
        GuideView.Builder guideView3 = getGuideForView(allGuideViews[2], allGuideStrings[2], activity);
        GuideView.Builder guideView4 = getGuideForView(allGuideViews[3], allGuideStrings[3], activity);
        GuideView.Builder guideView5 = getGuideForView(allGuideViews[4], allGuideStrings[4], activity);
        GuideView.Builder guideView6 = getGuideForView(allGuideViews[5], allGuideStrings[5], activity);

        guideView1.setGuideListener(getGuideListener(guideView2));
        guideView2.setGuideListener(getGuideListener(guideView3));
        guideView3.setGuideListener(getGuideListener(guideView4));
        guideView4.setGuideListener(getGuideListener(guideView5));
        guideView5.setGuideListener(getGuideListener(guideView6));

        guideView1.build().show();

        MainActivity.setShowGuide(false);
    }

    public static GuideListener getGuideListener(GuideView.Builder builder){
        return view -> builder.build().show();
    }

    public static GuideView.Builder getGuideForView(View view, String message, Activity activity){
        return new GuideView.Builder(activity)
                .setContentText(message)
                .setGravity(Gravity.auto) //optional
                .setDismissType(DismissType.anywhere) //optional - default DismissType.targetView
                .setTargetView(view)
                .setContentTextSize(12)//optional
                .setTitleTextSize(14);
    }
}
