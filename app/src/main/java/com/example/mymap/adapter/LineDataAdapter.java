package com.example.mymap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.maps.model.LatLng;
import com.example.mymap.R;
import com.example.mymap.entity.LineDataEntity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by TennyQ on 12/26/20
 */
public class LineDataAdapter extends RecyclerView.Adapter<LineDataAdapter.LineDataViewHolder> {

    private Context context;
    private List<LineDataEntity> dataEntityList;

    private onItemClickListener<List<LatLng>> onItemClickListener;

    public LineDataAdapter(Context context) {
        this.context = context;
    }

    public void setOnItemClickListener(LineDataAdapter.onItemClickListener<List<LatLng>> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public LineDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LineDataViewHolder(LayoutInflater.from(context).inflate(R.layout.line_data_item_layout, null, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LineDataViewHolder holder, int position) {
        holder.bindData(dataEntityList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataEntityList != null ? dataEntityList.size() : 0;
    }

    public void setDataEntityList(List<LineDataEntity> dataEntityList) {
        this.dataEntityList = dataEntityList;
        notifyDataSetChanged();
    }

    class LineDataViewHolder extends RecyclerView.ViewHolder{

        private TextView dateTx;
        private TextView sizeTx;

        public LineDataViewHolder(@NonNull View itemView) {
            super(itemView);

            dateTx = itemView.findViewById(R.id.date_tx);
            sizeTx = itemView.findViewById(R.id.date_size);
        }

        public void bindData(final LineDataEntity lineDataEntity) {
            dateTx.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(lineDataEntity.getTimeTag())));
            sizeTx.setText(String.format("%d个", lineDataEntity.getLatLngList().size()));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null ) {
                        onItemClickListener.onItemClick(getAdapterPosition(), lineDataEntity.getLatLngList());
                    }
                }
            });
        }
    }

    public interface onItemClickListener<T> {
        void onItemClick(int position, T t);
    }

}
