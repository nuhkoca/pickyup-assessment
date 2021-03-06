package com.upday.shutterdemo.pickyup.util;

import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.FrameLayout;

import com.upday.shutterdemo.pickyup.R;

public class SnackbarUtils {

    public enum Length {
        SHORT,
        LONG
    }

    private static SnackbarUtils INSTANCE;

    private SnackbarUtils() {
        if (INSTANCE == null) {
            INSTANCE = new SnackbarUtils();
        }
    }

    public static class Builder {
        View mView;
        CharSequence mMessage;
        Length mLength;

        public Builder setView(View view) {
            this.mView = view;
            return this;
        }

        private View getView() {
            return this.mView;
        }

        public Builder setMessage(CharSequence message) {
            this.mMessage = message;
            return this;
        }

        private CharSequence getMessage() {
            return this.mMessage;
        }

        public Builder setLength(Length length) {
            this.mLength = length;
            return this;
        }

        private Length getLength() {
            return this.mLength;
        }

        public Builder show(CharSequence resId) {
            final Snackbar snackbar = Snackbar.make(
                    getView(),
                    getMessage(),
                    getLength() == Length.SHORT ? Snackbar.LENGTH_SHORT : Snackbar.LENGTH_LONG);

            snackbar.setAction(resId, v -> snackbar.dismiss());

            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) snackbar.getView().getLayoutParams();


            if (!getView().getContext().getResources().getBoolean(R.bool.isTablet)) {
                layoutParams.setMargins(0, 0, 0, 168);
            } else {
                layoutParams.setMargins(0, 0, 0, 112);
            }

            snackbar.getView().setLayoutParams(layoutParams);

            snackbar.show();

            return this;
        }

        public SnackbarUtils build() {
            return INSTANCE;
        }
    }
}