package com.marswilliams.apps.sweettweets.databases;

import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = SweetTweetsDatabase.NAME, version = SweetTweetsDatabase.VERSION)
public class SweetTweetsDatabase {

    public static final String NAME = "TwitterClientDatabase";

    public static final int VERSION = 1;
}
