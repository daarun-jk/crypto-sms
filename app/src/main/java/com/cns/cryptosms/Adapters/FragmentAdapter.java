package com.cns.cryptosms.Adapters;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.cns.cryptosms.R;
import com.cns.cryptosms.AppTabs.ChatsFragment;
import com.cns.cryptosms.AppTabs.ContactsFragment;

public class FragmentAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public FragmentAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new ContactsFragment();
        } else {
            return new ChatsFragment();
        }
    }

    // Number of tabs
    @Override
    public int getCount() {
        return 2;
    }

    // Titles for tabs
    @Override
    public CharSequence getPageTitle(int position) {
        // Getting title from position
        switch (position) {
            case 0:
                return mContext.getString(R.string.contacts_tab_title);
            case 1:
                return mContext.getString(R.string.chats_tab_title);
            default:
                return null;
        }
    }

}
