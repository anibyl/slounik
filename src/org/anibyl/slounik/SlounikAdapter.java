package org.anibyl.slounik;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Adapter used to fill the list.
 * <p/>
 * Created by Usievaład Čorny on 26.02.2015 13:28.
 */
public class SlounikAdapter extends ArrayAdapter {
    private LayoutInflater inflater;
    private ArrayList<Article> list;

    public SlounikAdapter(Context context, int resource, int textViewResourceId, ArrayList<Article> list) {
        super(context, resource, textViewResourceId, list);

        inflater = LayoutInflater.from(context);
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.description = (TextView) convertView.findViewById(R.id.description);
            holder.dicName = (TextView) convertView.findViewById(R.id.dicName);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Article article = list.get(position);
        holder.title.setText(article.getTitle());
        holder.description.setText(article.getDescription());
        holder.dicName.setText(article.getDictionary());

        return convertView;
    }

    static class ViewHolder {
        TextView title;
        TextView description;
        TextView dicName;
    }
}
