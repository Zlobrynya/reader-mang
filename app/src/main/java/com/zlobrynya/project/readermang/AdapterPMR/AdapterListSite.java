package com.zlobrynya.project.readermang.AdapterPMR;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zlobrynya.project.readermang.R;

import java.util.ArrayList;

/**
 * Created by Nikita on 27.10.2016.
 */

public class AdapterListSite extends ArrayAdapter<String> {
    private ArrayList<String> strings;

    public AdapterListSite(Context context, int resource, ArrayList<String> item) {
        super(context, resource,item);
        strings = item;
    }

    private class Holder
    {
        TextView tv;
        ImageButton img;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        final LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);;
        Holder holder=new Holder();
        if (v != null){
            holder = (Holder) v.getTag();
        }else {
            v = vi.inflate(R.layout.item_list_site, null);
            holder.tv = (TextView) v.findViewById(R.id.text_site);
            holder.img = (ImageButton) v.findViewById(R.id.image_button_help);
            holder.img.setTag(position);
            v.setTag(holder);
        }
        //получаем класс из позиции
     //   ClassForList m1 = this.getItem(position);
        //если он есть то получаеи и устанавливаем
        holder.tv.setText(strings.get(position));

        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strMessege = "null";
                int position = (int) v.getTag();
                String nameSite = strings.get(position);
                if (nameSite.contains("Read"))
                    strMessege = v.getContext().getString(R.string.info_readmanga);
                else if (nameSite.contains("Mint"))
                    strMessege = v.getContext().getString(R.string.info_mintmanga);
                else if (nameSite.contains("Self"))
                    strMessege = v.getContext().getString(R.string.info_selfmanga);
                else strMessege = "Magic";

                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                TextView textMessege = (TextView) vi.inflate(R.layout.text_view,null);
                textMessege.setText(strMessege);
                LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                llp.setMargins(10, 0, 0, 0); // llp.setMargins(left, top, right, bottom);
                textMessege.setLayoutParams(llp);

                builder.setTitle("Описание сайта");
                builder.setView(textMessege);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

        return v;
    }
}
