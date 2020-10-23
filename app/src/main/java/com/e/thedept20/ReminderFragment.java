package com.e.thedept20;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.e.thedept20.Home.DashboardActivity;
import com.e.thedept20.Utils.TodoNotificationService;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;


import fr.ganfra.materialspinner.MaterialSpinner;

import static android.content.Context.MODE_PRIVATE;

public class ReminderFragment extends AppDefaultFragment
{
    public static final String EXIT = "com.avjindersekhon.exit";
    String theme;
    private TextView mtoDoTextTextView;
    private Button mRemoveToDoButton;
    private MaterialSpinner mSnoozeSpinner;
    private String[] snoozeOptionsArray;
    private StoreRetrieveData storeRetrieveData;
    private ArrayList<ToDoItem> mToDoItems;
    private ToDoItem mItem;
    private TextView mSnoozeTextView;
    //AnalyticsApplication app;

    public static ReminderFragment newInstance()
        {
        return new ReminderFragment();
        }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
        {
        super.onViewCreated(view, savedInstanceState);
        //app = (AnalyticsApplication) getActivity().getApplication();
        // app.send(this);

        theme = getActivity().getSharedPreferences(MainFragment.THEME_PREFERENCES, MODE_PRIVATE).getString(MainFragment.THEME_SAVED, MainFragment.LIGHTTHEME);

        storeRetrieveData = new StoreRetrieveData(getContext(), MainFragment.FILENAME);
        mToDoItems = MainFragment.getLocallyStoredData(storeRetrieveData);

        ((AppCompatActivity) getActivity()).setSupportActionBar((Toolbar) view.findViewById(R.id.toolbar));


        Intent i = getActivity().getIntent();
        UUID id = (UUID) i.getSerializableExtra(TodoNotificationService.TODOUUID);
        mItem = null;
        for (ToDoItem toDoItem : mToDoItems)
            {
            if (toDoItem.getIdentifier().equals(id))
                {
                mItem = toDoItem;
                break;
                }
            }

        snoozeOptionsArray = getResources().getStringArray(R.array.snooze_options);

        mRemoveToDoButton = view.findViewById(R.id.toDoReminderRemoveButton);
        mtoDoTextTextView = view.findViewById(R.id.toDoReminderTextViewBody);
        mSnoozeTextView = view.findViewById(R.id.reminderViewSnoozeTextView);
        mSnoozeSpinner = view.findViewById(R.id.todoReminderSnoozeSpinner);

//        mtoDoTextTextView.setBackgroundColor(item.getTodoColor());
        mtoDoTextTextView.setText(mItem.getToDoText());


        mRemoveToDoButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
                {
                //  app.send(this, "Action", "Todo Removed from Reminder Activity");
                mToDoItems.remove(mItem);
                changeOccurred();
                saveData();
                closeApp();
//                finish();
                }
        });


//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, snoozeOptionsArray);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_text_view, snoozeOptionsArray);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        mSnoozeSpinner.setAdapter(adapter);
//        mSnoozeSpinner.setSelection(0);
        }

    @Override
    protected int layoutRes()
        {
        return R.layout.fragment_reminder;
        }

    private void closeApp()
        {
        Intent i = new Intent(getContext(), DashboardActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        i.putExtra(EXIT, true);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainFragment.SHARED_PREF_DATA_SET_CHANGED, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(EXIT, true);
        editor.apply();
        startActivity(i);

        }

    public boolean onCreateOptionsMenu(Menu menu)
        {
        getActivity().getMenuInflater().inflate(R.menu.menu_reminder, menu);
        return true;
        }

    private void changeOccurred()
        {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainFragment.SHARED_PREF_DATA_SET_CHANGED, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(MainFragment.CHANGE_OCCURED, true);
//        editor.commit();
        editor.apply();
        }

    private Date addTimeToDate(int mins)
        {
        // app.send(this, "Action", "Snoozed", "For " + mins + " minutes");
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, mins);
        return calendar.getTime();
        }

    private int valueFromSpinner()
        {
        switch (mSnoozeSpinner.getSelectedItemPosition())
            {
            case 0:
                return 10;
            case 1:
                return 30;
            case 2:
                return 60;
            default:
                return 0;
            }
        }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
        {
        switch (item.getItemId())
            {
            case R.id.toDoReminderDoneMenuItem:
                Date date = addTimeToDate(valueFromSpinner());
                mItem.setToDoDate(date);
                mItem.setHasReminder(true);
                Log.d("OskarSchindler", "Date Changed to: " + date);
                changeOccurred();
                saveData();
                closeApp();
                //foo
                return true;
            default:
                return super.onOptionsItemSelected(item);
            }
        }

    private void saveData()
        {
        try
            {
            storeRetrieveData.saveToFile(mToDoItems);
            } catch (JSONException | IOException e)
            {
            e.printStackTrace();
            }
        }
}
