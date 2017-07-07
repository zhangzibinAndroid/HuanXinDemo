package com.zzb.qinzhusocial.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hyphenate.easeui.EaseConstant;
import com.zzb.qinzhusocial.R;
import com.zzb.qinzhusocial.activity.ChartActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChartFragment extends Fragment {
    @BindView(R.id.tv_room)
    TextView tvRoom;
    Unbinder unbinder;
    private View view;
    public ChartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chart, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.tv_room)
    public void onViewClicked() {
        Intent intent = new Intent(getActivity(), ChartActivity.class);
        intent.putExtra(EaseConstant.EXTRA_USER_ID, "20975036989441");
        intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_CHATROOM);
        startActivity(intent);
    }
}
