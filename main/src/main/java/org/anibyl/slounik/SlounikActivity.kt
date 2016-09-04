package org.anibyl.slounik

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import org.anibyl.slounik.core.Preferences
import org.anibyl.slounik.dialogs.newArticleDialog
import org.anibyl.slounik.network.Article
import org.anibyl.slounik.network.ArticlesInfo
import org.anibyl.slounik.network.BatchArticlesLoader
import org.anibyl.slounik.network.Server
import org.anibyl.slounik.network.Skarnik
import org.anibyl.slounik.network.SlounikOrg
import org.anibyl.slounik.ui.ProgressBar
import java.util.ArrayList

/**
 * The main activity.
 *
 * Created by Usievaład Čorny on 21.02.2015 11:00.
 */
class SlounikActivity:AppCompatActivity(), NavigationDrawerFragment.NavigationDrawerCallbacks {
	private var listView:ListView? = null
	private var articlesAmount:TextView? = null
	private var articles:ArrayList<Article>? = null
	var currentArticle:Article? = null
		private set
	private var adapter:SlounikAdapter? = null
	private var navigationDrawerFragment:NavigationDrawerFragment? = null
	private var titleStr:CharSequence? = null
	private var progress:ProgressBar? = null

	private var loader:BatchArticlesLoader? = null
	private var slounikOrg:SlounikOrg? = null
	private var skarnik:Skarnik? = null

	public override fun onCreate(savedInstanceState:Bundle?) {
		super.onCreate(savedInstanceState)

		slounikOrg = SlounikOrg()
		skarnik = Skarnik()

		loader = BatchArticlesLoader(slounikOrg!!, skarnik!!)

		Preferences.initialize(this)
		Server.loadConfig(this, object:Server.Callback() {
			override fun invoke() {
				slounikOrg?.url = Server.mainUrl
				skarnik?.url = Server.skarnikUrl
			}
		})
		if (LanguageSwitcher.initialize(this)) {
			return
		}

		setContentView(R.layout.main)

		progress = findViewById(R.id.progress) as ProgressBar

		listView = findViewById(R.id.listView) as ListView
		articlesAmount = findViewById(R.id.articles_amount) as TextView

		val articles = arrayListOf<Article>()
		this.articles = articles
		adapter = SlounikAdapter(this, R.layout.list_item, R.id.list_item_description, articles)
		listView!!.adapter = adapter
		listView!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
			newArticleDialog(articles!![position]).show(supportFragmentManager, "article_dialog")
		}

		navigationDrawerFragment = supportFragmentManager.findFragmentById(R.id.navigation_drawer) as NavigationDrawerFragment

		setTitle(R.string.app_name)
		titleStr = title

		navigationDrawerFragment?.setUp(
				R.id.navigation_drawer,
				findViewById(R.id.drawer_layout) as DrawerLayout)
	}

	override fun onNavigationDrawerItemSelected(position:Int) {
		val fragmentManager = supportFragmentManager
		fragmentManager.beginTransaction().replace(R.id.container,
				PlaceholderFragment.newInstance(position + 1)).commit()
	}

	override fun onCreateOptionsMenu(menu:Menu):Boolean {
		if (!navigationDrawerFragment!!.isDrawerOpen) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			menuInflater.inflate(R.menu.main, menu)
			restoreActionBar()
			return true
		}
		return super.onCreateOptionsMenu(menu)
	}

	fun restoreActionBar() {
		supportActionBar?.setDisplayShowTitleEnabled(true)
		supportActionBar?.title = titleStr
	}

	fun search(wordToSearch:String) {
		resetArticles()

		if (wordToSearch == "") {
			// TODO Make it visible for everyone.
			Notifier.toast(this, "Nothing to search.", true)
		} else {
			title = wordToSearch
			restoreActionBar()
			progress!!.progressiveStart()
			navigationDrawerFragment!!.setSearchEnabled(false)

			loader!!.loadArticles(wordToSearch, this, object:BatchArticlesLoader.BatchArticlesCallback() {
				override fun invoke(info:ArticlesInfo) {
					val loadedArticles = info.articles
					if (loadedArticles != null) {
						articles!!.addAll(loadedArticles)
					}

					when (info.status) {
						ArticlesInfo.Status.SUCCESS, ArticlesInfo.Status.FAILURE -> resetControls()
					}

					adapter?.notifyDataSetChanged()

					articlesAmount?.text = articles?.size.toString()
				}
			})
		}
	}

	private fun resetControls() {
		progress?.progressiveStop()
		navigationDrawerFragment?.setSearchEnabled(true)
	}

	private fun resetArticles() {
		articlesAmount?.text = ""
		articles?.clear()
		adapter?.notifyDataSetChanged()
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	class PlaceholderFragment:Fragment() {
		override fun onCreateView(inflater:LayoutInflater?, container:ViewGroup?, savedInstanceState:Bundle?):View? {
			return inflater?.inflate(R.layout.fragment_main, container, false)
		}

		override fun onAttach(context:Context?) {
			super.onAttach(context)
			// Do smth with selected getArguments().getInt(ARG_SECTION_NUMBER);
		}

		companion object {
			/**
			 * The fragment argument representing the section number for this
			 * fragment.
			 */
			private val ARG_SECTION_NUMBER = "section_number"

			/**
			 * Returns a new instance of this fragment for the given section
			 * number.
			 */
			fun newInstance(sectionNumber:Int):PlaceholderFragment {
				val fragment = PlaceholderFragment()
				val args = Bundle()
				args.putInt(ARG_SECTION_NUMBER, sectionNumber)
				fragment.arguments = args
				return fragment
			}
		}
	}
}