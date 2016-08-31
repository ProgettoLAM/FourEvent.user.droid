package lam.project.foureventuserdroid.fragment;


import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import lam.project.foureventuserdroid.MainActivity;
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

    private static final String NAME = "Portafoglio";

    private TimeLineAdapter mTimeLineAdapter;
    private ProgressBar mProgressBar;

    private LinkedList<Record> mDataList = new LinkedList<>();

    private TextView mTxtBalance;

    private AlertDialog.Builder builder;

    private View mRootView;

    private final static float MIN_VAL = 0.0f;

    public WalletFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_wallet, container, false);

        mProgressBar = (ProgressBar) mRootView.findViewById(R.id.progress_bar);
        mTxtBalance = (TextView) mRootView.findViewById(R.id.user_balance);

        setTitle();
        updateBalance();

        FloatingActionButton fab = (FloatingActionButton) mRootView.findViewById(R.id.fab_wallet);

        final Button.OnClickListener rechargeButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String value = ((Button) v).getText().toString().split(" ")[0];
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

        RecyclerView mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mTimeLineAdapter = new TimeLineAdapter(mDataList);
        mRecyclerView.setAdapter(mTimeLineAdapter);

        setModel();

        return mRootView;

    }

    private void updateBalance() {

        ObjectAnimator animation = ObjectAnimator.ofInt (mProgressBar, "progress", 0, 500);
        animation.setDuration (1000);
        animation.setInterpolator (new DecelerateInterpolator());
        animation.start ();

        //mostro progress bar e nascondo tutto il resto
        mProgressBar.setVisibility(View.VISIBLE);

        mTxtBalance.setVisibility(View.INVISIBLE);
        mRootView.findViewById(R.id.symbol_euro).setVisibility(View.INVISIBLE);

        String url = FourEventUri.Builder.create(FourEventUri.Keys.USER)
                .appendEncodedPath(MainActivity.mCurrentUser.email).getUri();

        CustomRequest getBalanceRequest = new CustomRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            MainActivity.mCurrentUser.balance = BigDecimal.valueOf(response.getDouble(User.Keys.BALANCE)).floatValue();

                            mProgressBar.setVisibility(View.INVISIBLE);

                            mTxtBalance.setVisibility(View.VISIBLE);
                            mRootView.findViewById(R.id.symbol_euro).setVisibility(View.VISIBLE);

                            animateBalance(MIN_VAL);

                        } catch (JSONException e) {

                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        error.printStackTrace();
                    }
                });

        VolleyRequest.get(getContext()).add(getBalanceRequest);
    }

    private void animateBalance(float minValue) {

        if(MainActivity.mCurrentUser.balance > 0) {

            ValueAnimator animator = new ValueAnimator();
            animator.setFloatValues(minValue, MainActivity.mCurrentUser.balance);
            animator.setDuration(500);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                public void onAnimationUpdate(ValueAnimator animation) {

                    float val = Float.parseFloat(""+animation.getAnimatedValue());

                    mTxtBalance.setText(String.format(Locale.ITALY,"%.1f", new BigDecimal(val)));
                }
            });
            animator.start();

        } else {

            mTxtBalance.setText(Float.toString(MainActivity.mCurrentUser.balance));
        }
    }

    private void setTitle () {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(NAME);
    }

    private void setModel() {

        String uri = FourEventUri.Builder.create(FourEventUri.Keys.RECORD)
                .appendEncodedPath(MainActivity.mCurrentUser.email).getUri();

        RecordListRequest recordListRequest = new RecordListRequest(uri, null, new Response.Listener<List<Record>>() {
            @Override
            public void onResponse(List<Record> response) {

                mDataList.clear();
                mDataList.addAll(response);

                Collections.reverse(mDataList);

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

        final float balance = MainActivity.mCurrentUser.balance;

        final ProgressDialog progressDialog = new ProgressDialog(getContext());

        progressDialog.setMessage("Ricarica in corso...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);


        String uri = FourEventUri.Builder.create(FourEventUri.Keys.RECORD)
                .appendEncodedPath(MainActivity.mCurrentUser.email).getUri();

        try {

            JSONObject record = Record.Builder.create(amount,Record.Keys.RECHARGE,MainActivity.mCurrentUser.email)
                    .build().toJson();

            CustomRequest createRecordRequest = new CustomRequest(Request.Method.PUT,
                    uri, record,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {

                                dialog.dismiss();
                                //ritorna l'oggetto che viene parsato e aggiunto
                                Record insertedRecord = Record.fromJson(response);

                                mDataList.addFirst(insertedRecord);

                                mTimeLineAdapter.notifyDataSetChanged();

                                //update balance
                                MainActivity.mCurrentUser.updateBalance(insertedRecord.mAmount);

                                animateBalance(balance);

                                UserManager.get().save(MainActivity.mCurrentUser);


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

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
