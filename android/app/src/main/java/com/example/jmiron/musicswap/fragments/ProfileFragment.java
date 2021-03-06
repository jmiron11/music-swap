package com.example.jmiron.musicswap.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.jmiron.musicswap.R;
import com.example.jmiron.musicswap.adapters.ProfileArtistAdapter;
import com.example.jmiron.musicswap.data.ProfileArtistContainer;
import com.example.jmiron.musicswap.dialogs.AddArtistDialogFragment;
import com.example.jmiron.musicswap.dialogs.NoConnectionDialogFragment;
import com.example.jmiron.musicswap.handlers.LastFmHandler;
import com.example.jmiron.musicswap.handlers.PreferencesHandler;
import com.example.jmiron.musicswap.handlers.ServerHandler;
import com.example.jmiron.musicswap.handlers.UrlImageHandler;
import com.example.jmiron.musicswap.interfaces.ViewPagerFragmentInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class ProfileFragment extends Fragment implements AddArtistDialogFragment.AddArtistDialogFragmentListener, ViewPagerFragmentInterface {
    private ArrayList<ProfileArtistContainer> mArtistArray = new ArrayList<>();
    private ProfileArtistAdapter mArtistAdapter;
    private RecyclerView mArtistView;
    private TextView username;

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!ServerHandler.isConnected())
            ServerHandler.connectToServer();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        // Inflate the layout for this fragment
        mArtistView = (RecyclerView) v.findViewById(R.id.profileArtistList);
        mArtistView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mArtistView.setAdapter(mArtistAdapter); // assign the adapter to the infoview

        /* load the previous profile data */
        mArtistArray.clear();
        mArtistAdapter.notifyDataSetChanged();
        ArrayList<ProfileArtistContainer> prevArtists = PreferencesHandler.getArtists(getActivity());
        if(prevArtists != null)
        {
            for (ProfileArtistContainer artist : prevArtists){
                addArtist(artist);
            }
        }

        ImageButton addArtistBtn = (ImageButton) v.findViewById(R.id.addArtistBtn);
        addArtistBtn.setOnClickListener(onAddArtistClick());

        username = (TextView) v.findViewById(R.id.usersUsername);
        username.setText(PreferencesHandler.getUsername(getActivity()));


        return v;
    }

    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mArtistAdapter = new ProfileArtistAdapter(getActivity(), mArtistArray);
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        PreferencesHandler.saveArtists(getActivity(), mArtistArray);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void addArtist(final ProfileArtistContainer newArtist)
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mArtistArray.add(0, newArtist);
                mArtistAdapter.notifyItemInserted(0);
            }
        });
        String user = username.getText().toString().trim();
        ArrayList<String> artistArray = new ArrayList<>();
        for(ProfileArtistContainer artistIn : mArtistArray)
        {
            artistArray.add(artistIn.artist);
        }
        ServerHandler.saveProfile(user, new JSONArray(artistArray));
    }

    private Button.OnClickListener onAddArtistClick() {
        Button.OnClickListener ret = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddArtistDialogFragment addArtistDialog = new AddArtistDialogFragment();
                addArtistDialog.setListener(ProfileFragment.this);
                addArtistDialog.show(getActivity().getSupportFragmentManager(), "Add Artist");
            }
        };
        return ret;
    }

    @Override
    public void onDialogMessage(String artistName) {
        addArtist(new ProfileArtistContainer(artistName));
        PreferencesHandler.saveArtists(getActivity(), mArtistArray);
    }

    @Override
    public void fragmentSelected() {

    }
    public void fragmentScrolled(){ }
}
