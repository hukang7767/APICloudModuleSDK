package com.alpha.modulegnoga.measurement;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.alpha.modulegnoga.R;

import java.util.ArrayList;

public class MeasurementItemAdapter extends RecyclerView.Adapter<MeasurementItemAdapter.viewHolder>
{

    private ArrayList<Param> params;


     public class viewHolder extends  RecyclerView.ViewHolder
     {
         public TextView paramName , paramSymbol, paramValue , paramUnits;

         public viewHolder( View view)
         {
             super(view);
             paramName = (TextView) view.findViewById(R.id.report_item_content);
             paramSymbol = (TextView) view.findViewById(R.id.report_item_symbol);
             paramValue = (TextView) view.findViewById(R.id.report_item_value);
             paramUnits = (TextView) view.findViewById(R.id.report_item_units);
         }
     }

     public MeasurementItemAdapter(ArrayList<Param> params)
     {
         this.params = params;
     }


    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.measurement_item,parent,false);
        return new viewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position)
    {
         Param param = params.get(position);
         String param_name = param.getName();
         String param_value = param.getValue();
         String param_content = param.getContent();
         String param_units = param.getUnits();
         holder.paramName.setText(param_content);
         holder.paramSymbol.setText(param_name);
         holder.paramValue.setText(param_value);
         holder.paramUnits.setText(param_units);

    }

    @Override
    public int getItemCount() {
        return params.size();
    }
}
