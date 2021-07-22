package ir.guardianapp.guardian_v2.PasswordManager;

import android.graphics.Rect;
import android.text.method.TransformationMethod;
import android.view.View;

public class DoNothingTransformationMethod implements TransformationMethod {
    @Override
    public CharSequence getTransformation(CharSequence source, View view) {
        return source;
    }

    @Override
    public void onFocusChanged(View view, CharSequence sourceText, boolean focused, int direction, Rect previouslyFocusedRect) {

    }
}
