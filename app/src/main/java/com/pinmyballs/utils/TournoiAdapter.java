package com.pinmyballs.utils;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pinmyballs.R;
import com.pinmyballs.metier.Tournoi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by juliendpy on 17/03/2018.
 */

public class TournoiAdapter extends ArrayAdapter<Tournoi> {

    private static final String TAG = "TournoiAdapter";
    int mResource;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private Context mContext;


    /**
     * Default constructor for the TournoiAdapter
     *
     * @param context
     * @param resource
     * @param objects
     */
    public TournoiAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Tournoi> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //Get tournoi information
        String name = getItem(position).getNom();
        String ville = getItem(position).getVille();
        String enseigne = getItem(position).getEns();
        String adresse = getItem(position).getAdresseComplete();
        Date date1 = new Date();

        Calendar mCalendar = Calendar.getInstance(Locale.getDefault());
        String date = getItem(position).getDate();
        try {
            date1 = new SimpleDateFormat("yyyy/MM/dd").parse(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        String month = new SimpleDateFormat("MMM", Locale.getDefault()).format(date1).toUpperCase();
        String day = new SimpleDateFormat("dd", Locale.getDefault()).format(date1);
        String year = new SimpleDateFormat("yyyy", Locale.getDefault()).format(date1);


        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView Tmonth = (TextView) convertView.findViewById(R.id.Tmonth);
        TextView Tday = (TextView) convertView.findViewById(R.id.Tday);
        TextView Tyear = (TextView) convertView.findViewById(R.id.Tyear);
        TextView Tname = (TextView) convertView.findViewById(R.id.Tname);
        TextView Tville = (TextView) convertView.findViewById(R.id.Tville);
        TextView Tadresse = (TextView) convertView.findViewById(R.id.Tadresse);

        if (date1.before(new Date())) {
            Tmonth.setTextColor(Color.DKGRAY);
            Tday.setTextColor(Color.DKGRAY);
            Tyear.setTextColor(Color.DKGRAY);
            Tname.setTextColor(Color.DKGRAY);
            convertView.setBackgroundColor(Color.parseColor("#F1F1F1F1"));
        }

        Tname.setText(name);
        Tville.setText(ville + ", " + enseigne);
        Tadresse.setText(adresse);
        Tmonth.setText(month);
        Tday.setText(day);
        Tyear.setText(year);


        return convertView;

    }
}
