package com.huang.android.logistic.Chat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.huang.android.logistic.Model.Communication.CommunicationData;
import com.huang.android.logistic.Model.JobOrder.JobOrderData;
import com.huang.android.logistic.R;
import com.huang.android.logistic.Utility;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by davidwibisono on 2/5/18.
 */

public class ChatAdapter extends ArrayAdapter<CommunicationData> {
    Context context;
    List<CommunicationData> list;

    public ChatAdapter(Context context, int layout, List<CommunicationData> list) {
        super(context, layout,list);
        this.context=context;
        this.list=list;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if(convertView==null){
            LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            CommunicationData current = list.get(position);
            String userName = Utility.utility.getLoggedName(context);
            if (current.sender_full_name.equals(userName))
                view=inflater.inflate(R.layout.chat_self_user,parent,false);
            else
                view=inflater.inflate(R.layout.chat_other_user,parent,false);

            TextView sender = (TextView)view.findViewById(R.id.sender),
                    content = (TextView)view.findViewById(R.id.textview_message),
                    time = (TextView)view.findViewById(R.id.textview_time);

            Utility.utility.setTextView(sender,current.sender_full_name);
            Utility.utility.setTextView(content,current.content);
            Utility.utility.setTextView(time,
                    Utility.formatDateFromstring(Utility.dateDBLongFormat,Utility.LONG_DATE_TIME_FORMAT,current.creation));
        }

        return view;
    }

}
