package org.anibyl.slounik

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.view.MenuItemCompat
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
import android.widget.CheckBox
import org.anibyl.slounik.core.Preferences
import org.anibyl.slounik.util.StubActionBar

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the [design guidelines](https://developer.android.com/design/patterns/navigation-drawer.html#Interaction) for a
 * complete explanation of the behaviors implemented here.
 */
class NavigationDrawerFragment : Fragment() {
	val isDrawerOpen: Boolean
		get() = drawerLayout != null && drawerLayout!!.isDrawerOpen(fragmentContainerView)

	private val actionBar: ActionBar
		get() = callbacks?.getSupportActionBar() ?: StubActionBar()

	/**
	 * A pointer to the current callbacks instance (the Activity).
	 */
	private var callbacks: NavigationDrawerCallbacks? = null

	/**
	 * Helper component that ties the action bar to the navigation drawer.
	 */
	private var drawerToggle: ActionBarDrawerToggle? = null

	private var drawerLayout: DrawerLayout? = null
	private var fragmentContainerView: View? = null

	private var searchItem: MenuItem? = null
	private var checkBoxSlounikOrg: CheckBox? = null
	private var checkBoxSkarnik: CheckBox? = null

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		// Indicate that this fragment would like to influence the set of actions in the action bar.
		setHasOptionsMenu(true)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.drawer, container, false)
	}

	/**
	 * Users of this fragment must call this method to set up the navigation drawer interactions.
	 *
	 * @param fragmentContainerView This fragment in its activity's layout.
	 * @param drawerLayout          The DrawerLayout containing this fragment's UI.
	 */
	fun setUp(fragmentContainerView: View, drawerLayout: DrawerLayout) {
		this.fragmentContainerView = fragmentContainerView
		this.drawerLayout = drawerLayout

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
					activity.supportInvalidateOptionsMenu()
				}
			}

			override fun onDrawerOpened(drawerView: View?) {
				super.onDrawerOpened(drawerView)

				if (isAdded) {
					activity.supportInvalidateOptionsMenu()
				}
			}
		}

		drawerLayout.post { drawerToggle!!.syncState() }

		drawerLayout.addDrawerListener(drawerToggle!!)

		checkBoxSlounikOrg = activity.findViewById(R.id.checkbox_slounik_org) as CheckBox
		checkBoxSlounikOrg!!.isChecked = Preferences.useSlounikOrg
		checkBoxSlounikOrg!!.setOnCheckedChangeListener { buttonView, isChecked ->
			Preferences.useSlounikOrg = isChecked
			checkBoxSkarnik!!.isEnabled = isChecked
		}

		checkBoxSkarnik = activity.findViewById(R.id.checkbox_skarnik) as CheckBox
		checkBoxSkarnik!!.isChecked = Preferences.useSkarnik
		checkBoxSkarnik!!.setOnCheckedChangeListener { buttonView, isChecked ->
			Preferences.useSkarnik = isChecked
			checkBoxSlounikOrg!!.isEnabled = isChecked
		}

		val checkBoxSearchInTitle = activity.findViewById(R.id.checkbox_search_in_title) as CheckBox
		checkBoxSearchInTitle.isChecked = Preferences.searchInTitles
		checkBoxSearchInTitle.setOnCheckedChangeListener { buttonView, isChecked ->
			Preferences.searchInTitles = isChecked
		}
	}

	fun setSearchEnabled(enabled: Boolean) {
		if (searchItem != null) {
			searchItem!!.isEnabled = enabled
		}
	}

	override fun onAttach(context: Context?) {
		super.onAttach(context)

		try {
			callbacks = context as NavigationDrawerCallbacks?
		} catch (e: ClassCastException) {
			throw ClassCastException("Activity must implement NavigationDrawerCallbacks.")
		}

	}

	override fun onDetach() {
		super.onDetach()
		callbacks = null
	}

	override fun onConfigurationChanged(newConfig: Configuration?) {
		super.onConfigurationChanged(newConfig)
		// Forward the new configuration the drawer toggle component.
		drawerToggle!!.onConfigurationChanged(newConfig)
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		// If the drawer is open, show the global app actions in the action bar. See also
		// showGlobalContextActionBar, which controls the top-left area of the action bar.
		if (drawerLayout != null && isDrawerOpen) {
			inflater.inflate(R.menu.main, menu)
			showGlobalContextActionBar()
		}

		searchItem = menu.findItem(R.id.action_search)
		val searchView = MenuItemCompat.getActionView(searchItem) as SearchView
		searchView.queryHint = getString(R.string.search_hint)
		searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
			override fun onQueryTextSubmit(s: String): Boolean {
				(activity as SlounikActivity).search(s)
				MenuItemCompat.collapseActionView(searchItem)
				return false
			}

			override fun onQueryTextChange(s: String): Boolean {
				return false
			}
		})
		searchView.setOnSearchClickListener {
			searchView.setQuery(callbacks?.getLastSearchedWord(), false)
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
	 * Per the navigation drawer design guidelines, updates the action bar to show the global app
	 * 'context', rather than just what's in the current screen.
	 */
	private fun showGlobalContextActionBar() {
		val actionBar = actionBar
		actionBar.setDisplayShowTitleEnabled(true)
		actionBar.setTitle(R.string.app_name)
	}

	/**
	 * Callbacks interface that all activities using this fragment must implement.
	 */
	interface NavigationDrawerCallbacks {
		fun search(wordToSearch: String)

		fun getLastSearchedWord(): String?

		fun getSupportActionBar(): ActionBar?
	}
}
