package org.anibyl.slounik.activities

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import butterknife.BindView
import butterknife.ButterKnife
import org.anibyl.slounik.R
import org.anibyl.slounik.SlounikApplication
import org.anibyl.slounik.core.Preferences
import org.anibyl.slounik.dialogs.AboutDialog
import javax.inject.Inject

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the [design guidelines](https://developer.android.com/design/patterns/navigation-drawer.html#Interaction) for a
 * complete explanation of the behaviors implemented here.
 */
class NavigationDrawerFragment : Fragment() {
	/**
	 * Callbacks interface that all activities using this fragment must implement.
	 */
	interface NavigationDrawerCallbacks {
		fun onSearchClicked(wordToSearch: String)

		fun getLastSearchedWord(): String?

		fun getSupportActionBar(): ActionBar
	}

	@Inject lateinit var preferences: Preferences

	@BindView(R.id.checkbox_slounik_org) lateinit var checkBoxSlounikOrg: CheckBox
	@BindView(R.id.checkbox_skarnik) lateinit var checkBoxSkarnik: CheckBox
	@BindView(R.id.checkbox_rodnyja_vobrazy) lateinit var checkBoxRodnyjaVobrazy: CheckBox
	@BindView(R.id.checkbox_search_in_title) lateinit var checkBoxSearchInTitle: CheckBox
	@BindView(R.id.drawer_about_button) lateinit var aboutButton: Button

	internal val isDrawerOpen: Boolean
		get() = drawerLayout != null && drawerLayout!!.isDrawerOpen(fragmentContainerView)

	private val actionBar: ActionBar
		get() = callbacks.getSupportActionBar()

	/**
	 * A pointer to the current callbacks instance (the Activity).
	 */
	private lateinit var callbacks: NavigationDrawerCallbacks

	/**
	 * Helper component that ties the action bar to the navigation drawer.
	 */
	private var drawerToggle: ActionBarDrawerToggle? = null

	private var drawerLayout: DrawerLayout? = null
	private var fragmentContainerView: View? = null

	private var searchItem: MenuItem? = null

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		// Indicate that this fragment would like to influence the set of actions in the action bar.
		setHasOptionsMenu(true)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view: View = inflater.inflate(R.layout.drawer, container, false)
		ButterKnife.bind(this, view)
		return view
	}

	override fun onAttach(context: Context?) {
		super.onAttach(context)

		try {
			callbacks = context as NavigationDrawerCallbacks
		} catch (e: ClassCastException) {
			throw ClassCastException("Activity must implement NavigationDrawerCallbacks.")
		}
	}

	override fun onConfigurationChanged(newConfig: Configuration?) {
		super.onConfigurationChanged(newConfig)
		// Forward the new configuration the drawer toggle component.
		drawerToggle!!.onConfigurationChanged(newConfig)
	}

	override fun onPrepareOptionsMenu(menu: Menu) {
		searchItem = menu.findItem(R.id.action_search)
		val searchView: SearchView = searchItem?.actionView as SearchView
		searchView.queryHint = getString(R.string.search_hint)
		searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
			override fun onQueryTextSubmit(s: String): Boolean {
				callbacks.onSearchClicked(s)
				searchItem?.collapseActionView()
				return true
			}

			override fun onQueryTextChange(s: String): Boolean {
				return false
			}
		})
		searchView.setOnSearchClickListener {
			searchView.setQuery(callbacks.getLastSearchedWord(), false)
		}
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		// If the drawer is open, show the global app actions in the action bar. See also
		// showGlobalContextActionBar, which controls the top-left area of the action bar.
		if (drawerLayout != null && isDrawerOpen) {
			inflater.inflate(R.menu.main, menu)
			showGlobalContextActionBar()
		}

		super.onCreateOptionsMenu(menu, inflater)
	}

	override fun onOptionsItemSelected(item: MenuItem?): Boolean {
		if (drawerToggle!!.onOptionsItemSelected(item)) {
			return true
		}

		if (drawerLayout != null) {
			drawerLayout!!.closeDrawer(fragmentContainerView)
		}

		return super.onOptionsItemSelected(item)
	}

	/**
	 * Users of this fragment must call this method to set up the navigation drawer interactions.
	 *
	 * @param fragmentContainerView This fragment in its activity's layout.
	 * @param drawerLayout          The DrawerLayout containing this fragment's UI.
	 */
	internal fun setup(fragmentContainerView: View, drawerLayout: DrawerLayout) {
		this.fragmentContainerView = fragmentContainerView
		this.drawerLayout = drawerLayout

		SlounikApplication.graph.inject(this)

		// set a custom shadow that overlays the main content when the drawer opens
		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START)
		// set up the drawer's list view with items and click listener

		val actionBar = actionBar
		actionBar.setDisplayHomeAsUpEnabled(true)
		actionBar.setHomeButtonEnabled(true)

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the navigation drawer and the action bar app icon.
		drawerToggle = object : ActionBarDrawerToggle(
				activity,
				drawerLayout,
				R.string.navigation_drawer_open,
				R.string.navigation_drawer_close
		) {
			override fun onDrawerClosed(drawerView: View?) {
				super.onDrawerClosed(drawerView)

				if (isAdded) {
					activity.invalidateOptionsMenu()
				}
			}

			override fun onDrawerOpened(drawerView: View?) {
				super.onDrawerOpened(drawerView)

				if (isAdded) {
					activity.invalidateOptionsMenu()
				}
			}
		}

		drawerLayout.post { drawerToggle!!.syncState() }

		drawerLayout.addDrawerListener(drawerToggle!!)

		// TODO Create list with disabling functionality.
		checkBoxSlounikOrg.isChecked = preferences.useSlounikOrg
		checkBoxSlounikOrg.setOnCheckedChangeListener { _, isChecked ->
			preferences.useSlounikOrg = isChecked
		}

		checkBoxSkarnik.isChecked = preferences.useSkarnik
		checkBoxSkarnik.setOnCheckedChangeListener { _, isChecked ->
			preferences.useSkarnik = isChecked
		}

		checkBoxRodnyjaVobrazy.isChecked = preferences.useRodnyjaVobrazy
		checkBoxRodnyjaVobrazy.setOnCheckedChangeListener { _, isChecked ->
			preferences.useRodnyjaVobrazy = isChecked
		}

		checkBoxSearchInTitle.isChecked = preferences.searchInTitles
		checkBoxSearchInTitle.setOnCheckedChangeListener { _, isChecked ->
			preferences.searchInTitles = isChecked
		}

		aboutButton.setOnClickListener {
			AboutDialog().show(activity.supportFragmentManager, "about_dialog")
		}
	}

	internal fun setSearchEnabled(enabled: Boolean) {
		if (searchItem != null) {
			searchItem!!.isEnabled = enabled
		}
	}

	/**
	 * Per the navigation drawer design guidelines, updates the action bar to show the global app
	 * 'context', rather than just what's in the current screen.
	 */
	private fun showGlobalContextActionBar() {
		val actionBar = actionBar
		actionBar.setDisplayShowTitleEnabled(true)
		actionBar.setTitle(R.string.app_name)
	}
}
