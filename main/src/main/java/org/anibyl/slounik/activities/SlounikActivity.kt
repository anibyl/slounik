package org.anibyl.slounik.activities

import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import org.anibyl.slounik.R
import org.anibyl.slounik.SlounikApplication
import org.anibyl.slounik.dialogs.newArticleDialog
import org.anibyl.slounik.network.Article
import org.anibyl.slounik.ui.ProgressBar
import javax.inject.Inject

/**
 * The main activity.
 *
 * @author Usievaład Kimajeŭ
 * @created 21.02.2015
 */
class SlounikActivity : AppCompatActivity(), NavigationDrawerFragment.NavigationDrawerCallbacks {
	@Inject lateinit var presenter: SlounikActivityPresenter

	private val articles: List<Article>
		get() = presenter.articles

	lateinit private var progress: ProgressBar
	lateinit private var listView: ListView
	lateinit private var articlesAmount: TextView
	lateinit private var navigationDrawerFragment: NavigationDrawerFragment

	lateinit private var adapter: SlounikAdapter

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		setContentView(R.layout.main)

		SlounikApplication.graph.inject(this)

		progress = findViewById(R.id.progress) as ProgressBar
		listView = findViewById(R.id.listView) as ListView
		articlesAmount = findViewById(R.id.articles_amount) as TextView

		navigationDrawerFragment = supportFragmentManager.findFragmentById(R.id.navigation_drawer)
				as NavigationDrawerFragment

		adapter = SlounikAdapter(this, R.layout.list_item, R.id.list_item_description, articles)
		listView.adapter = adapter
		listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
			newArticleDialog(articles[position]).show(supportFragmentManager, "article_dialog")
		}

		title = presenter.title

		updateArticlesAmount()

		updateProgress()

		navigationDrawerFragment.setup(
				findViewById(R.id.navigation_drawer),
				findViewById(R.id.drawer_layout) as DrawerLayout
		)

		presenter.onActivityCreated(this)
	}

	override fun onDestroy() {
		presenter.onActivityDestroyed()
		super.onDestroy()
	}

	override fun getLastSearchedWord(): String? {
		return presenter.lastSearchedWord
	}

	override fun getSupportActionBar(): ActionBar? {
		return super.getSupportActionBar()
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		if (!navigationDrawerFragment.isDrawerOpen) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			menuInflater.inflate(R.menu.main, menu)
			restoreActionBar()
			return true
		}
		return super.onCreateOptionsMenu(menu)
	}

	override fun setTitle(title: CharSequence?) {
		super.setTitle(title)
		supportActionBar?.title = title
	}

	override fun onSearchClicked(wordToSearch: String) {
		presenter.onSearchClicked(wordToSearch)
	}

	internal fun resetControls() {
		progress.progressiveStop()
		navigationDrawerFragment.setSearchEnabled(true)
	}

	internal fun resetArticles() {
		updateArticlesAmount()
		adapter.notifyDataSetChanged()
	}

	internal fun articlesUpdated() {
		updateArticlesAmount()
		adapter.notifyDataSetChanged()
	}

	internal fun onStartSearching(wordToSearch: String) {
		title = wordToSearch
		progress.progressiveStart()
		navigationDrawerFragment.setSearchEnabled(false)
	}

	private fun restoreActionBar() {
		supportActionBar?.setDisplayShowTitleEnabled(true)
		title = presenter.title
	}

	private fun updateArticlesAmount() {
		articlesAmount.text = if (articles.isEmpty()) "" else articles.size.toString()
	}

	private fun updateProgress() {
		if (presenter.searching) {
			progress.progressiveStart()
		} else {
			progress.progressiveStop()
		}
	}
}