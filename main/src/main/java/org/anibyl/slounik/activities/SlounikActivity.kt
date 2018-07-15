package org.anibyl.slounik.activities

import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
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

	@BindView(R.id.toolbar) lateinit var toolbar: Toolbar
	@BindView(R.id.search_edit_text) lateinit var searchEditText: EditText
	@BindView(R.id.search_clear_button) lateinit var searchClearButton: ImageButton
	@BindView(R.id.search_button) lateinit var searchButton: ImageButton
	@BindView(R.id.progress) lateinit var progress: ProgressBar
	@BindView(R.id.listView) lateinit var listView: ListView
	@BindView(R.id.articles_amount) lateinit var articlesAmount: TextView

	private val articles: List<Article>
		get() = presenter.articles

	private lateinit var navigationDrawerFragment: NavigationDrawerFragment

	private lateinit var adapter: SlounikAdapter

	private var largeArticlesAmountFont:Boolean = true

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		setContentView(R.layout.main)

		SlounikApplication.graph.inject(this)

		ButterKnife.bind(this)

		setSupportActionBar(toolbar)

		searchEditText.setOnEditorActionListener { _, actionId, _ ->
			when (actionId) {
				EditorInfo.IME_ACTION_SEARCH -> {
					onSearchClicked(searchEditText.text.toString())
					true
				}
				else -> false
			}
		}

		searchEditText.setOnFocusChangeListener { _, hasFocus ->
			searchClearButton.visibility = if (hasFocus && searchEditText.text.isNotEmpty()) View.VISIBLE else View.INVISIBLE
		}

		searchEditText.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(s: Editable?) {
				searchClearButton.visibility = if (searchEditText.text.isNotEmpty()) View.VISIBLE else View.INVISIBLE
			}

			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
			override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
		})

		searchEditText.setOnClickListener {
			navigationDrawerFragment.close()
		}

		searchClearButton.setOnClickListener {
			searchEditText.text.clear()
			navigationDrawerFragment.close()
		}

		searchButton.setOnClickListener {
			onSearchClicked(searchEditText.text.toString())
		}

		navigationDrawerFragment = supportFragmentManager.findFragmentById(R.id.navigation_drawer)
				as NavigationDrawerFragment

		adapter = SlounikAdapter(this, R.layout.list_item, R.id.list_item_description, articles)
		listView.adapter = adapter
		listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
			newArticleDialog(articles[position]).show(supportFragmentManager, "article_dialog")
		}

		updateArticlesAmount()

		updateProgress()

		navigationDrawerFragment.setup(
				findViewById(R.id.navigation_drawer),
				findViewById(R.id.drawer_layout)
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

	override fun getSupportActionBar(): ActionBar {
		return super.getSupportActionBar()!!
	}

	internal fun searchStarted(word: String) {
		searchEditText.setText(word)
		progress.progressiveStart()
		setSearchEnabled(false)
	}

	internal fun searchEnded() {
		progress.progressiveStop()
		setSearchEnabled(true)
	}

	internal fun articlesUpdated() {
		updateArticlesAmount()
		adapter.notifyDataSetChanged()
	}

	private fun onSearchClicked(wordToSearch: String) {
		listView.requestFocus()
		navigationDrawerFragment.close()
		presenter.onSearchClicked(wordToSearch)
	}

	private fun updateArticlesAmount() {
		articlesAmount.text = articles.size.toString()

		when {
			!largeArticlesAmountFont && articles.size in 0..999 -> {
				articlesAmount.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.font_large_200))
				largeArticlesAmountFont = true
			}
			largeArticlesAmountFont && articles.size > 999 -> {
				articlesAmount.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.font_large_150))
				largeArticlesAmountFont = false
			}
		}
	}

	private fun updateProgress() {
		if (presenter.searching) {
			progress.progressiveStart()
		} else {
			progress.progressiveStop()
		}
	}

	private fun setSearchEnabled(enabled: Boolean) {
		searchEditText.isEnabled = enabled
		searchClearButton.isEnabled = enabled
		searchButton.isEnabled = enabled
	}
}
