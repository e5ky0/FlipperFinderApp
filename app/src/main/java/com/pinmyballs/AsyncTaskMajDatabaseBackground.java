package com.pinmyballs;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.pinmyballs.database.FlipperDatabaseHandler;
import com.pinmyballs.service.GlobalService;

public class AsyncTaskMajDatabaseBackground extends AsyncTask<Object, Void, Boolean> {

    private AppCompatActivity mContext;
    private SharedPreferences mSettings;
    private String retourMaj = null;

    public AsyncTaskMajDatabaseBackground(AppCompatActivity context, SharedPreferences settings){
        mContext = context;
        mSettings = settings;
    }

    @Override
    protected Boolean doInBackground(Object... params) {
        GlobalService globalService = new GlobalService();
        Editor editor = mSettings.edit();
        try {
            String dateDerniereMajString = mSettings.getString(PagePreferences.KEY_PREFERENCES_DATE_LAST_UPDATE, FlipperDatabaseHandler.DATABASE_DATE_MAJ);
            retourMaj = globalService.majBaseAvecNouveaute(mContext, dateDerniereMajString);

            if (retourMaj != null){
                // La màj s'est bien passée, on mémorise la date de mise à jour dans les Préférences.
                DateFormat df = new SimpleDateFormat("yyyy/MM/dd", Locale.FRANCE);
                Date today = Calendar.getInstance().getTime();
                String dateDuJour = df.format(today);
                editor.putString(PagePreferences.KEY_PREFERENCES_DATE_LAST_UPDATE, dateDuJour);
                editor.putString(PagePreferences.KEY_PREFERENCES_DATABASE_VERSION, String.valueOf(FlipperDatabaseHandler.DATABASE_VERSION));
                editor.apply();
            }
        } catch (Exception e) {
            // Erreur trappée. On efface la base, elle sera réinitialisée au prochain appel, et on
            // set la date de mise à jour à la valeur par défaut.
            //EasyTracker.getTracker().sendEvent("ui_error", "MAJ_DB_ERROR", "PagePreferences", 0L);
            mContext.deleteDatabase(FlipperDatabaseHandler.FLIPPER_BASE_NAME);
            editor.putString(PagePreferences.KEY_PREFERENCES_DATE_LAST_UPDATE, FlipperDatabaseHandler.DATABASE_DATE_MAJ);
            editor.commit();
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        // S'il y a eu une exception, on affiche le message d'erreur et on se casse
        if (!result){
            Toast.makeText(mContext,"Echec mise de la mise à jour en arrière-plan", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(mContext,"Mise à jour effectuée", Toast.LENGTH_SHORT).show();
    }
}
