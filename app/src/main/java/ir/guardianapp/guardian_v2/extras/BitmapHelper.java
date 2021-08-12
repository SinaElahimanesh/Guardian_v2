package ir.guardianapp.guardian_v2.extras;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class BitmapHelper {
    public static Bitmap resizeBitmap(String drawableName, int width, int height, Resources resources, String packageName){
        Bitmap imageBitmap = BitmapFactory.decodeResource(resources, resources.getIdentifier(drawableName, "drawable", packageName));
        return Bitmap.createScaledBitmap(imageBitmap, width, height, false);
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(140, 140, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}

