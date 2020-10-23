package com.e.thedept20;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.e.thedept20.Adapters.SubjectAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AttendenceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AttendenceFragment extends Fragment
{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    static SubjectAdapter subjectAdapter;
    private static ArrayList<String> subjectList;
    BottomNavigationView bottomNavigationView;
    DatabaseHelper mydb;
    TextInputLayout subject;
    Button addSubject;
    TextView tv;
    Context mCtx;
    private RecyclerView subjectRecycler;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AttendenceFragment()
        {
        // Required empty public constructor
        }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AttendenceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AttendenceFragment newInstance(String param1, String param2)
        {
        AttendenceFragment fragment = new AttendenceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
        }

    public static void notifyChange()
        {
        subjectAdapter.notifyDataSetChanged();
        }

    @Override
    public void onCreate(Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            }
        }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
        {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_attendence, container, false);

        subjectRecycler = view.findViewById(R.id.subjectRecyclerView);
        subject = view.findViewById(R.id.subjectNamemas);
        addSubject = view.findViewById(R.id.addSubject);
        subject.getEditText().addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
                {

                }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
                {

                }

            @Override
            public void afterTextChanged(Editable s)
                {
                subject.setError(null);
                }
        });


        return view;
        }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
        {
        super.onActivityCreated(savedInstanceState);
        mydb = new DatabaseHelper(getContext());


        subjectList = new ArrayList<>();
        subjectRecycler.setHasFixedSize(true);
        subjectRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        subjectAdapter = new SubjectAdapter(subjectList, mCtx);
        subjectRecycler.setAdapter(subjectAdapter);
        loadData();
        addSubject.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
                {
                addSubject();
                }
        });
        }

    @Override
    public void onStart()
        {
        super.onStart();

        }

    @Override
    public void onResume()
        {
        super.onResume();

        }

    public void loadData()
        {
        Cursor res = mydb.viewAllData();

        while (res.moveToNext())
            {
            subjectList.add(res.getString(1));
            subjectAdapter.notifyDataSetChanged();
            }
        }

    public void addSubject()
        {
        if (subject.getEditText().getText().toString().isEmpty() || subject.getEditText().getText().toString().equals(""))
            subject.setError("Enter a Subject");
        else
            {
            boolean res = mydb.insetData(subject.getEditText().getText().toString(), "0", "0");
            if (res == true)
                {
                Toast.makeText(getContext(), "Subject added successfully", Toast.LENGTH_SHORT).show();
                } else
                Toast.makeText(getContext(), "Data not added", Toast.LENGTH_SHORT).show();
            try
                {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e)
                {
                // TODO: handle exception
                }

            subjectList.add(subject.getEditText().getText().toString());
            subjectAdapter.notifyDataSetChanged();
            subject.getEditText().setText("");
            subject.clearFocus();
            }
        }

    @Override
    public void onAttach(@NonNull Context context)
        {
        super.onAttach(context);
        mCtx = context;
        }
}