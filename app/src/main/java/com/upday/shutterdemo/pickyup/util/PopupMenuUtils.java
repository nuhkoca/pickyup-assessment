package com.upday.shutterdemo.pickyup.util;

import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;

public class PopupMenuUtils {

    public interface IMenuItemIdListener {
        void onItemIdReceived(int id);
    }

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