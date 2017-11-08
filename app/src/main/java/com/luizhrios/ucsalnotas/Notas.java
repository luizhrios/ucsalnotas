package com.luizhrios.ucsalnotas;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class Notas extends AppCompatActivity
{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */

    /**
     * The {@link ViewPager} that will host the section contents.
     */

    private ArrayList<Subject> Subjects;

    private NotasAdapter rvNotasAdapter;
    private RecyclerView rvNotas;

    SharedPreferences settings;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notas);
        settings = getPreferences(0);
        editor = settings.edit();
        Subjects = (ArrayList<Subject>) getIntent().getSerializableExtra("Subjects");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        rvNotas = (RecyclerView) findViewById(R.id.rvNotas);
        rvNotasAdapter = new NotasAdapter(this, Subjects, settings.getBoolean("colors", true));
        rvNotas.setAdapter(rvNotasAdapter);
        rvNotas.setLayoutManager(new LinearLayoutManager(this));
        setTitle(getIntent().getStringExtra("nome"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notas, menu);
        menu.findItem(R.id.action_colors).setChecked(settings.getBoolean("colors", true));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_colors)
        {
            if (item.isChecked())
            {
                rvNotasAdapter.setColors((false));
                rvNotasAdapter.notifyDataSetChanged();
            } else
            {
                rvNotasAdapter.setColors(true);
                rvNotasAdapter.notifyDataSetChanged();
            }
            item.setChecked(!item.isChecked());
            editor.putBoolean("colors", item.isChecked());
            editor.commit();
            return true;
        }
        if (id == R.id.action_share)
        {
            Intent test = new Intent(Intent.ACTION_SEND);
            test.setType("image/jpeg");
            File file = new File(getCacheDir(), "notas.png");
            OutputStream fOut = null;
            try
            {
                fOut = new FileOutputStream(file);
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            getRecyclerViewScreenshot(rvNotas).compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            file.setReadable(true, false);
            test.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            startActivity(Intent.createChooser(test, "Compartilhar Notas"));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static Bitmap getRecyclerViewScreenshot(RecyclerView view)
    {
        int size = view.getAdapter().getItemCount();
        RecyclerView.ViewHolder holder = view.getAdapter().createViewHolder(view, 0);
        view.getAdapter().onBindViewHolder(holder, 0);
        holder.itemView.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(), holder.itemView.getMeasuredHeight());
        Bitmap bigBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), holder.itemView.getMeasuredHeight() * size,
                Bitmap.Config.ARGB_8888);
        Canvas bigCanvas = new Canvas(bigBitmap);
        bigCanvas.drawColor(Color.WHITE);
        Paint paint = new Paint();
        int iHeight = 0;
        for (int i = 0; i < size; i++)
        {
            view.getAdapter().onBindViewHolder(holder, i);
            holder.itemView.setDrawingCacheEnabled(true);
            holder.itemView.buildDrawingCache();
            bigCanvas.drawBitmap(holder.itemView.getDrawingCache(), 0f, iHeight, paint);
            iHeight += holder.itemView.getMeasuredHeight();
            holder.itemView.setDrawingCacheEnabled(false);
            holder.itemView.destroyDrawingCache();
        }
        return bigBitmap;
    }

}