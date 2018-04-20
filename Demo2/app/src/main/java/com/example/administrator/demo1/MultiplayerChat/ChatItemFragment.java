package com.example.administrator.demo1.MultiplayerChat;

import android.content.Context;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.administrator.demo1.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ChatItemFragment extends Fragment {


    private static final String ARG_COLUMN_COUNT = "column-count";

    private int mColumnCount = 1;
    private io.socket.client.Socket csocket;
    private String username;
    private List<Message> mValue;
    private OnListFragmentInteractionListener mListener;
    private RecyclerView recycleV;
    private MyChatItemRecyclerViewAdapter viewAdapter;
    private Button chatButton;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChatItemFragment() {
    }


    /*public static ChatItemFragment newInstance() {
        ChatItemFragment fragment = new ChatItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataset();// For testing purpose

    }
    private void initDataset() {
        for (int i = 0; i < 10; i++) {
            Message.Builder mb = new Message.Builder();
            mb.message("user "+i);
            mb.message("test message # "+i);
            mValue.add(i,mb.build());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatitem_list, container, false);

        /*// Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            recyclerView.setAdapter(new MyChatItemRecyclerViewAdapter(mValue, mListener));
        }*/


        Context context = view.getContext();
        // BEGIN_INCLUDE(initializeRecyclerView)
        recycleV = view.findViewById(R.id.list);
        viewAdapter = new MyChatItemRecyclerViewAdapter(mValue,mListener);
        // Set CustomAdapter as the adapter for RecyclerView.
        recycleV.setAdapter(viewAdapter);
        // END_INCLUDE(initializeRecyclerView)
        chatButton = (Button) view.findViewById(R.id.imageButton);
        recycleV.setLayoutManager(new LinearLayoutManager(context));
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    public void updateChatView(int position){

    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        //void onListFragmentInteraction(DummyItem item);
    }
}
