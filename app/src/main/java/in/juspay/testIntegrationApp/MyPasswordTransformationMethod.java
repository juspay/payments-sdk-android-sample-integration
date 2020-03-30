package in.juspay.testIntegrationApp;

import android.text.method.PasswordTransformationMethod;
import android.view.View;

import androidx.annotation.NonNull;

public class MyPasswordTransformationMethod extends PasswordTransformationMethod {

    private char aChar;

    public MyPasswordTransformationMethod(char aChar) {
        this.aChar = aChar;
    }

    @Override
    public CharSequence getTransformation(CharSequence source, View view) {
        return new PasswordCharSequence(source, aChar);
    }

    private class PasswordCharSequence implements CharSequence {
        private CharSequence mSource;
        private char aChar;

        public PasswordCharSequence(CharSequence mSource, char aChar) {
            this.mSource = mSource;
            this.aChar = aChar;
        }

        @Override
        public int length() {
            return mSource.length();
        }

        @Override
        public char charAt(int i) {
            return aChar;
        }

        @NonNull
        @Override
        public CharSequence subSequence(int start, int end) {
            return mSource.subSequence(start, end);
        }
    }
}
