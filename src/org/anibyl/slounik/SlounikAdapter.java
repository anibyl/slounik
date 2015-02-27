package org.anibyl.slounik;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Adapter used to fill the list.
 *
 * Created by Usievaład Čorny on 26.2.15 13.28.
 */
public class SlounikAdapter<String> extends ArrayAdapter {
    private LayoutInflater inflater;
    private ListEntry[] list;

    public SlounikAdapter(Activity activity, int resource, int textViewResourceId, ListEntry[] list) {
        super(activity, resource, textViewResourceId, list);

        inflater = LayoutInflater.from(activity);
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

        holder.title.setText(list[position].getTitle());
        holder.description.setText(list[position].getDescription());
        holder.dicName.setText(list[position].getDictionary());

        return convertView;
    }

    static class ViewHolder {
        TextView title;
        TextView description;
        TextView dicName;
    }
}
