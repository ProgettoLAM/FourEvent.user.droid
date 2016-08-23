package lam.project.foureventuserdroid.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vipul.hp_hp.timelineview.TimelineView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lam.project.foureventuserdroid.R;
import lam.project.foureventuserdroid.fragment.TimeLine.TimeLineAdapter;
import lam.project.foureventuserdroid.model.Record;
import lam.project.foureventuserdroid.model.User;
import lam.project.foureventuserdroid.utils.connection.CustomRequest;
import lam.project.foureventuserdroid.utils.connection.FourEventUri;
import lam.project.foureventuserdroid.utils.connection.RecordListRequest;
import lam.project.foureventuserdroid.utils.connection.VolleyRequest;
import lam.project.foureventuserdroid.utils.shared_preferences.UserManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class WalletFragment extends Fragment {

    private static AlertDialog dialog;

    private User mCurrentUser;

    private static final String NAME = "Portafoglio";

    private FloatingActionButton fab;

    private RecyclerView mRecyclerView;

    private TimeLineAdapter mTimeLineAdapter;

    private List<Record> mDataList = new ArrayList<>();

    private TextView mTxtBalance;

    private AlertDialog.Builder builder;


    public WalletFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_wallet, container, false);

        this.mCurrentUser = getArguments().getParcelable(User.Keys.USER);

        setTitle();

        mTxtBalance = (TextView) rootView.findViewById(R.id.user_balance);

        //mTxtBalance.setText(Float.toString(mCurrentUser.balance));

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab_wallet);

        final Button.OnClickListener rechargeButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String value = ((Button) v).getText().toString();
                Float amount = Float.parseFloat(value);
                try {
                    recharge(amount);

                }
                catch (JSONException ex) {
                    Log.d("Error", ex+"");
                }
            }
        };



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO insert record

                builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Ricarica il portafoglio");

                View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_recharge, (ViewGroup) getView(), false);

                ((Button)viewInflated.findViewById(R.id.button_1_recharge)).setOnClickListener(rechargeButtonListener);
                ((Button)viewInflated.findViewById(R.id.button_2_recharge)).setOnClickListener(rechargeButtonListener);
                ((Button)viewInflated.findViewById(R.id.button_3_recharge)).setOnClickListener(rechargeButtonListener);

                builder.setView(viewInflated);

                builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                dialog = builder.show();
            }
        });

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mTimeLineAdapter = new TimeLineAdapter(mDataList);
        mRecyclerView.setAdapter(mTimeLineAdapter);

        setModel();

        return rootView;

    }

    private void setTitle () {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(NAME);
    }


    private void setModel() {

        String uri = FourEventUri.Builder.create(FourEventUri.Keys.RECORD)
                .appendEncodedPath(mCurrentUser.email).getUri();

        RecordListRequest recordListRequest = new RecordListRequest(uri, null, new Response.Listener<List<Record>>() {
            @Override
            public void onResponse(List<Record> response) {

                mDataList.clear();
                mDataList.addAll(response);

                mTimeLineAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                System.out.println(error.toString());
            }
        });

        VolleyRequest.get(getContext()).add(recordListRequest);
    }

    private void recharge(Float amount) throws JSONException {

        final ProgressDialog progressDialog = new ProgressDialog(getContext());

        progressDialog.setMessage("Ricarica in corso...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);


        String uri = FourEventUri.Builder.create(FourEventUri.Keys.RECORD)
                .appendEncodedPath(mCurrentUser.email).getUri();

        CustomRequest createRecordRequest = new CustomRequest(Request.Method.PUT,
            uri, createBody(amount, WalletKeys.RECHARGE, null),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            //ritorna l'oggetto che viene parsato e aggiunto
                            Record insertedRecord = Record.fromJson(response);

                            mDataList.add(insertedRecord);
                            mTimeLineAdapter.notifyDataSetChanged();

                            //update balance
                            mCurrentUser.updateBalance(insertedRecord.mAmount);
                            mTxtBalance.setText(Float.toString(mCurrentUser.balance));
                            
                            UserManager.get().save(mCurrentUser);

                            dialog.dismiss();

                        } catch (JSONException e) {

                            e.printStackTrace();
                        }

                        progressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        progressDialog.dismiss();
                    }
                });

        VolleyRequest.get().add(createRecordRequest);
    }

    private JSONObject createBody(float amount, String type, String event) throws JSONException {

        JSONObject requestody = new JSONObject();

        requestody.put(WalletKeys.AMOUNT,amount);
        requestody.put(WalletKeys.TYPE,type);

        if(event != null) {

            requestody.put(WalletKeys.EVENT,event);
        }

        return requestody;
    }

    private static class WalletKeys {

        public static final String TYPE = "type";
        public static final String AMOUNT = "amount";
        public static final String EVENT = "event";

        public static final String RECHARGE = "Ricarica conto";
    }
}
