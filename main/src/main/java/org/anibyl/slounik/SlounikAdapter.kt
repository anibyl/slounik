package org.anibyl.slounik

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import org.anibyl.slounik.network.Article

import java.util.ArrayList

/**
 * Adapter used to fill the list.
 *
 * @author Usievaład Kimajeŭ
 * @created 26.02.2015
 */
class SlounikAdapter(context:Context, resource:Int, textViewResourceId:Int, private val list:ArrayList<Article>)
:ArrayAdapter<Article>(context, resource, textViewResourceId, list) {
	private val inflater:LayoutInflater

	init {
		inflater = LayoutInflater.from(context)
	}

	override fun getView(position:Int, convertView:View?, parent:ViewGroup):View {
		val holder:ViewHolder
		val view:View
		if (convertView == null) {
			view = inflater.inflate(R.layout.list_item, null)
			holder = ViewHolder(
				view.findViewById(R.id.list_item_title) as TextView,
				view.findViewById(R.id.list_item_description) as TextView,
				view.findViewById(R.id.list_item_dictionary) as TextView
			)

			view.tag = holder
		} else {
			view = convertView
			holder = convertView.tag as ViewHolder
		}

		val article = list[position]
		holder.title.text = article.title
		holder.description.text = article.description
		holder.dicName.text = article.dictionary

		return view
	}

	internal class ViewHolder(val title:TextView, val description:TextView, val dicName:TextView)
}
