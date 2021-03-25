package org.anibyl.slounik.activities

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import org.anibyl.slounik.R
import org.anibyl.slounik.data.Article

/**
 * Adapter used to fill the list.
 *
 * @author Sieva Kimajeŭ
 * @created 2015-02-26
 */
class SlounikAdapter(context: Context, resource: Int, textViewResourceId: Int, private val list: List<Article>)
	: ArrayAdapter<Article>(context, resource, textViewResourceId, list) {
	private val inflater: LayoutInflater = LayoutInflater.from(context)

	override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
		val holder: ViewHolder
		val view: View
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

		if (position <= list.size - 1) {
			val article = list[position]
			holder.title.text = article.title?.trim()
			holder.description.text = article.spannedDescription?.replace(Regex("\n"), " ")?.trim()
			holder.dicName.text = article.dictionary?.trim()
		} else {
			// There was an IndexOutOfBoundsException in production on 2019-09-22.
			holder.title.text = "";
			holder.description.text = "";
			holder.dicName.text = "";
		}

		return view
	}

	internal class ViewHolder(val title: TextView, val description: TextView, val dicName: TextView)
}