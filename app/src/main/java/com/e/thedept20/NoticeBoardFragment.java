package com.e.thedept20;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.e.thedept20.Adapters.Notices;
import com.e.thedept20.Adapters.NoticeBoardViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NoticeBoardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NoticeBoardFragment extends Fragment
{

    RecyclerViewEmptySupport NewsRecyclerview;
    private FirebaseAuth mAuth;
    private String currentuserID;
    private FirebaseRecyclerOptions<Notices> options;
    private FirebaseRecyclerAdapter<Notices, NoticeBoardViewHolder> adapter;
    private Context mContext;
    private DatabaseReference UserRef, Noticeref, LikesRef, FriendsRef;
    private LinearLayout linearLayout;
    private Context context;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NoticeBoardFragment()
        {
        // Required empty public constructor
        }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NoticeBoardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NoticeBoardFragment newInstance(String param1, String param2)
        {
        NoticeBoardFragment fragment = new NoticeBoardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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

        View view=inflater.inflate(R.layout.fragment_notice_board, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentuserID = mAuth.getCurrentUser().getUid();

        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        Noticeref = FirebaseDatabase.getInstance().getReference().child("Notices");


        linearLayout=view.findViewById(R.id.toDoEmptyView5);
        FriendsRef = FirebaseDatabase.getInstance().getReference().child(currentuserID);
        NewsRecyclerview = view.findViewById(R.id.news_rv);
        NewsRecyclerview.setEmptyView(view.findViewById(R.id.toDoEmptyView5));
        NewsRecyclerview.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(container.getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        NewsRecyclerview.setLayoutManager(linearLayoutManager);
        context=getContext();
        mContext = getContext();
        DisplayAllUsersPosts();

        return view;
        }

    public void DisplayAllUsersPosts()
        {
        Query sortpostinDescendingorder = Noticeref.orderByChild("Counter");
        options = new FirebaseRecyclerOptions.Builder<Notices>()
                .setQuery(sortpostinDescendingorder, Notices.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Notices, NoticeBoardViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull final NoticeBoardViewHolder holder, int position, @NonNull final Notices model)
                {

                holder.ProfileImage.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_transition_animation));
                holder.layout.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_scale_animation));


                final String PostKey = getRef(position).getKey();
                holder.Date.setText(model.getDate());
                holder.Description.setText(model.getDescription());

                Glide.with(getContext())
                        .load(model.getProfileImage())
                        .apply(RequestOptions.skipMemoryCacheOf(false))
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                        .into(holder.ProfileImage);


                holder.fullname.setText(model.getFullname());



                }

            @NonNull
            @Override
            public NoticeBoardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
                return new NoticeBoardViewHolder(view);
                }

        };

        NewsRecyclerview.setAdapter(adapter);
        adapter.startListening();
        }
}