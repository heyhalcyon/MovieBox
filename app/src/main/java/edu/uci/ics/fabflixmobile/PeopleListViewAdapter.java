package edu.uci.ics.fabflixmobile;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PeopleListViewAdapter extends ArrayAdapter<Person> {
    private ArrayList<Person> people;

    public PeopleListViewAdapter(ArrayList<Person> people, Context context) {
        super(context, R.layout.layout_listview_row, people);
        this.people = people;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.layout_listview_row, parent, false);

        Person person = people.get(position);

        TextView titleView = (TextView)view.findViewById(R.id.title);
        TextView subtitleView = (TextView)view.findViewById(R.id.subtitle);

        titleView.setText(person.getName());
        subtitleView.setText(person.getBirthYear().toString());

        return view;
    }
}
