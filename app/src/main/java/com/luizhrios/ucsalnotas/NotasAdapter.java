package com.luizhrios.ucsalnotas;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Luiz on 2016-12-09.
 */
class NotasAdapter extends RecyclerView.Adapter<NotasAdapter.ViewHolder>
{
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */

    private List<Subject> Subjects;
    // Store the context for easy access
    private Context mContext;

    private boolean colors;

    // Pass in the contact array into the constructor
    public NotasAdapter(Context context, List<Subject> subjects, boolean colors)
    {
        mContext = context;
        Subjects = subjects;
        this.colors = colors;
        defaultTV = new TextView(mContext);
    }

    // Easy access to the context object in the recyclerview
    private Context getContext()
    {
        return mContext;
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row

        TextView Subject;
        TextView Faltas;
        TextView AV1;
        TextView AV2;
        TextView AVI;
        TextView MP;
        TextView MF;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView)
        {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            Subject = (TextView) itemView.findViewById(R.id.Subject);
            Faltas = (TextView) itemView.findViewById(R.id.Faltas);
            AV1 = (TextView) itemView.findViewById(R.id.AV1);
            AV2 = (TextView) itemView.findViewById(R.id.AV2);
            AVI = (TextView) itemView.findViewById(R.id.AVI);
            MP = (TextView) itemView.findViewById(R.id.MP);
            MF = (TextView) itemView.findViewById(R.id.MF);
        }
    }

    @Override
    public NotasAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View NotasView = inflater.inflate(R.layout.fragment_notas, parent, false);
        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(NotasView);
        return viewHolder;
    }

    private TextView defaultTV;
    private TextView Subject;
    private TextView Faltas;
    private TextView AV1;
    private TextView AV2;
    private TextView AVI;
    private TextView MP;
    private TextView MF;

    @Override
    public void onBindViewHolder(NotasAdapter.ViewHolder viewHolder, int position)
    {
        // Get the data model based on position
        Subject subject = Subjects.get(position);
        // Set item views based on your views and data model
        Subject = viewHolder.Subject;
        Faltas = viewHolder.Faltas;
        AV1 = viewHolder.AV1;
        AV2 = viewHolder.AV2;
        AVI = viewHolder.AVI;
        MP = viewHolder.MP;
        MF = viewHolder.MF;
        Subject.setText(subject.Nome);
        Faltas.setText(subject.Faltas.toString());
        AV1.setText(subject.AV1 != null ? subject.AV1.toString() : null);
        AV2.setText(subject.AV2 != null ? subject.AV2.toString() : null);
        AVI.setText(subject.AVI != null ? subject.AVI.toString() : null);
        MP.setText(subject.MP != null ? subject.MP.toString() : null);
        MF.setText(subject.MF != null ? subject.MF.toString() : null);
        Colors();
    }

    void setColors(boolean colors)
    {
        this.colors = colors;
    }

    private void Colors()
    {
        String value;
        for (int i = 1; i <= 4; i++)
        {
            TextView TV;
            switch (i)
            {
                default:
                    TV = AV1;
                    break;
                case 2:
                    TV = AV2;
                    break;
                case 3:
                    TV = MP;
                    break;
                case 4:
                    TV = MF;
                    break;
            }
            value = TV.getText().toString();
            if (!value.equals("") && colors)
            {
                if (Double.parseDouble(value) >= 6)
                {
                    TV.setTextColor(Color.parseColor("#32a550"));
                    if (i == 4)
                    {
                        Subject.setTextColor(Color.parseColor("#32a550"));
                    }
                } else
                {
                    if (i != 3 || !AV2.getText().equals(""))
                        TV.setTextColor(Color.RED);
                    else
                        TV.setTextColor(defaultTV.getTextColors());
                    if (i == 4)
                    {
                        Subject.setTextColor(Color.RED);
                    }
                }
            } else
            {
                Subject.setTextColor(Color.BLACK);
                TV.setTextColor(defaultTV.getTextColors());
            }
        }
        value = AVI.getText().toString();
        if (!value.equals(""))
        {
            if (colors)
            {
                if (Double.parseDouble(AVI.getText().toString()) >= 0.5)
                    AVI.setTextColor(Color.parseColor("#32a550"));
                else
                    AVI.setTextColor(Color.RED);
            } else
            {
                Subject.setTextColor(defaultTV.getTextColors());
                AVI.setTextColor(defaultTV.getTextColors());
            }
        }
    }


    @Override
    public int getItemCount()
    {
        return Subjects.size();
    }
}
