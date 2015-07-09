package com.example.jmiron.musicswap.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.jmiron.musicswap.adapters.MessageAdapter;
import com.example.jmiron.musicswap.data.MessageContainer;
import com.example.jmiron.musicswap.dialogs.NoConnectionDialogFragment;
import com.example.jmiron.musicswap.R;
import com.example.jmiron.musicswap.activities.ChatActivity;
import com.example.jmiron.musicswap.activities.MainActivity;
import com.example.jmiron.musicswap.handlers.PreferencesHandler;
import com.example.jmiron.musicswap.handlers.ServerHandler;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {;

    private Socket mSocket;

    private ArrayList<MessageContainer> mMessageArray;
    private MessageAdapter mMessageAdapter;
    private ListView mMessageView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MainFragment.
     */
    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public MainFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(
                R.layout.fragment_main,container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        mMessageView = (ListView) view.findViewById(R.id.mainList);
        mMessageView.setAdapter(mMessageAdapter); // assign the adapter to the infoview

        if(PreferencesHandler.getFirst(getActivity()))
        {
            addInfo(new MessageContainer("Welcome", "THIS IS A MESSAGE", R.drawable.albumartph40, 0));
            PreferencesHandler.setFirstFalse(getActivity());
        }

        /* assign button on click listeners */
        Button chatBtn = (Button) view.findViewById(R.id.btnFindChat);
        chatBtn.setOnClickListener(onChatClick());

        Button swapBtn = (Button) view.findViewById(R.id.btnSuggestArtist);
        swapBtn.setOnClickListener(onSwapClick());

        /* restoring the info view */
//        if(savedInstanceState != null){
//            /* copy old info array data to the new emptied info array */
//            ArrayList<MessageContainer> prevData = savedInstanceState.getParcelableArrayList("info");
//            for (MessageContainer data : prevData){
//                addInfo(data);
//            }
//        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mMessageArray = new ArrayList<>();
        mMessageAdapter = new MessageAdapter(getActivity(), mMessageArray);

        ArrayList<MessageContainer> prevMessages = PreferencesHandler.getMessages(getActivity());
        if(prevMessages != null)
        {
            for (MessageContainer message : prevMessages){
                addInfo(message);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putParcelableArrayList("info", mMessageArray);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        PreferencesHandler.storeMessages(getActivity(), mMessageArray);
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    public void onDestroy(){
        super.onDestroy();
    }

    private void addInfo(MessageContainer newInfo)
    {
        mMessageArray.add(0, newInfo);
    }

    private Button.OnClickListener onChatClick(){
        Button.OnClickListener ret = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ServerHandler.isConnected()) {
                    NoConnectionDialogFragment noConnDialog = new NoConnectionDialogFragment();
                    noConnDialog.show(getActivity().getSupportFragmentManager(), "No Connection");
                }
//                if (getActivity() != null) {
//                    if (MainActivity.mSocket.connected()) {
//                        //TODO: Add username to joining chat
//                        MainActivity.mSocket.emit("chat_user_join");
//                        Intent intent = new Intent(getActivity(), ChatActivity.class);
//                        startActivity(intent);
//                    } else {
//                        NoConnectionDialogFragment noConnDialog = new NoConnectionDialogFragment();
//                        noConnDialog.show(getActivity().getSupportFragmentManager(), "No Connection");
//                    }
//                }
            }
        };
        return ret;
    }

    private Button.OnClickListener onSwapClick(){
        Button.OnClickListener ret = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ServerHandler.isConnected()) {
                    NoConnectionDialogFragment noConnDialog = new NoConnectionDialogFragment();
                    noConnDialog.show(getActivity().getSupportFragmentManager(), "No Connection");
                }
                final String username = PreferencesHandler.getUsername(getActivity());
                final String artist1 = PreferencesHandler.getArtist1(getActivity());
                final String artist2 = PreferencesHandler.getArtist2(getActivity());
                final String artist3 = PreferencesHandler.getArtist3(getActivity());
                ServerHandler.findMatch(username, artist1, artist2, artist3);
            }
        };
        return ret;
    }

}