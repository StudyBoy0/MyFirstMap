package com.example.mymap.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.maps.model.LatLng;
import com.example.mymap.R;
import com.example.mymap.adapter.LineDataAdapter;
import com.example.mymap.callback.CallBack;
import com.example.mymap.entity.LineDataEntity;
import com.example.mymap.utils.SystemUtils;

import java.util.List;

/**
 * Created by TennyQ on 12/26/20
 */
class LineDataDialog {

    private Dialog dialog;
    private RecyclerView recyclerView;
    private LineDataAdapter lineDataAdapter;

    private CallBack<List<LatLng>> callBack;

    public LineDataDialog(CallBack<List<LatLng>> callBack) {
        this.callBack = callBack;
    }

    public void showLineDataDialog(Context context, List<LineDataEntity> lineEntityList) {
        if (dialog == null) {
            dialog = new Dialog(context, R.style.dialog);
            dialog.setContentView(getContentView(context));
            dialog.setCancelable(true);
            Window window = dialog.getWindow();
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = (int) SystemUtils.dp2px(300);
            window.setAttributes(layoutParams);
            window.setGravity(Gravity.BOTTOM);
        }
        lineDataAdapter.setDataEntityList(lineEntityList);
        dialog.show();
    }

    private View getContentView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_list, null, false);

        view.findViewById(R.id.close_tx).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        lineDataAdapter = new LineDataAdapter(context);
        lineDataAdapter.setOnItemClickListener(new LineDataAdapter.onItemClickListener<List<LatLng>>() {
            @Override
            public void onItemClick(int position, List<LatLng> latLngs) {
                if (callBack != null) {
                    if (latLngs.isEmpty()) {
                        callBack.failed("点为空");
                    } else {
                        callBack.success(latLngs);
                    }
                }
            }
        });
        recyclerView.setAdapter(lineDataAdapter);

        return view;
    }


}
