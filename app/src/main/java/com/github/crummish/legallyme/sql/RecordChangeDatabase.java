package com.github.crummish.legallyme.sql;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

@Database(entities = {RecordChangeForm.class, RecordChangeInstructions.class}, version = 1, exportSchema = false)
public abstract class RecordChangeDatabase extends RoomDatabase {
    public abstract RecordChangeFormDao recordChangeFormDao();
    public abstract RecordChangeInstructionsDao recordChangeInstructionsDao();

    private static volatile RecordChangeDatabase INSTANCE;

    static RecordChangeDatabase getDatabase(final Context context) {
        if(INSTANCE == null) {
            synchronized (RecordChangeDatabase.class) {
                if(INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            RecordChangeDatabase.class, "record_change_database")
                            .build();
                }
            }
        }

        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback =
        new RoomDatabase.Callback() {
            @Override
            public void onOpen(@NonNull SupportSQLiteDatabase db) {
                super.onOpen(db);
                new PopulateDbAsyncTask(INSTANCE).execute();
            }
        };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private final RecordChangeFormDao formDao;
        private final RecordChangeInstructionsDao instructionsDao;

        PopulateDbAsyncTask(RecordChangeDatabase db) {
            formDao = db.recordChangeFormDao();
            instructionsDao = db.recordChangeInstructionsDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            formDao.deleteAll();
            for(RecordChangeForm r : RecordChangeDatabasePrePopulateHelper.getForms()) {
                formDao.insert(r);
            }

            instructionsDao.deleteAll();
            //for(RecordChangeInstructions r : )

            return null;
        }
    }
}