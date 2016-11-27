package com.youibot.docrobo.robo.main.model;

import com.netease.nim.robo.R;
import com.youibot.docrobo.robo.main.fragment.ContactListFragment;
import com.youibot.docrobo.robo.main.fragment.DrugadminFragment;
import com.youibot.docrobo.robo.main.fragment.MainTabFragment;
import com.youibot.docrobo.robo.main.fragment.MineFragment;
import com.youibot.docrobo.robo.main.fragment.RoboFragment;
import com.youibot.docrobo.robo.main.fragment.SelfmonitorFragment;
import com.youibot.docrobo.robo.main.fragment.SessionListFragment;

import com.youibot.docrobo.robo.main.reminder.ReminderId;

public enum MainTab {
    SELF_MONITOR(0,ReminderId.INVALID,SelfmonitorFragment.class, R.string.main_tab_selfmonitor,R.layout.fragment_selfmonitor),
    DRUG_ADMIN(1,ReminderId.INVALID, DrugadminFragment.class,R.string.main_tab_drugadmin,R.layout.fragment_drugadmin),
    ROBOR(2,ReminderId.INVALID, RoboFragment.class,R.string.robo,R.layout.fragment_robo),
    RECENT_CONTACTS(3, ReminderId.SESSION, SessionListFragment.class, R.string.session, R.layout.session_list),
    CONTACT(4, ReminderId.CONTACT, ContactListFragment.class, R.string.mylink, R.layout.contacts_list),
    MINE(5,ReminderId.INVALID, MineFragment.class, R.string.mine,R.layout.fragment_mine);

    public final int tabIndex;

    public final int reminderId;

    public final Class<? extends MainTabFragment> clazz;

    public final int resId;

    public final int fragmentId;

    public final int layoutId;

    MainTab(int index, int reminderId, Class<? extends MainTabFragment> clazz, int resId, int layoutId) {
        this.tabIndex = index;
        this.reminderId = reminderId;
        this.clazz = clazz;
        this.resId = resId;
        this.fragmentId = index;
        this.layoutId = layoutId;
    }

    public static final MainTab fromReminderId(int reminderId) {
        for (MainTab value : MainTab.values()) {
            if (value.reminderId == reminderId) {
                return value;
            }
        }

        return null;
    }

    public static final MainTab fromTabIndex(int tabIndex) {
        for (MainTab value : MainTab.values()) {
            if (value.tabIndex == tabIndex) {
                return value;
            }
        }

        return null;
    }
}
