package com.upday.shutterdemo.pickyup.utils;

import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;

import com.upday.shutterdemo.pickyup.callback.IMenuItemIdListener;

public class PopupMenuUtils {

    private static PopupMenuUtils INSTANCE;

    private PopupMenuUtils() {
        if (INSTANCE == null) {
            INSTANCE = new PopupMenuUtils();
        }
    }

    public static class Builder implements PopupMenu.OnMenuItemClickListener {

        private IMenuItemIdListener iMenuItemIdListener;

        public Builder listener(IMenuItemIdListener iMenuItemIdListener) {
            this.iMenuItemIdListener = iMenuItemIdListener;

            return this;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int itemThatWasClicked = item.getItemId();

            this.iMenuItemIdListener.onItemIdReceived(itemThatWasClicked);

            return true;
        }

        public PopupMenuUtils build() {
            return INSTANCE;
        }
    }
}