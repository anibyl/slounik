package org.anibyl.slounik

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.view.MenuItemCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
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
	/**
	 * A pointer to the current callbacks instance (the Activity).
	 */
	private var mCallbacks: NavigationDrawerCallbacks? = null

	/**
	 * Helper component that ties the action bar to the navigation drawer.
	 */
	private var mDrawerToggle: ActionBarDrawerToggle? = null

	private var mDrawerLayout: DrawerLayout? = null
	private var mFragmentContainerView: View? = null

	private var mCurrentSelectedPosition = 0
	private var mFromSavedInstanceState: Boolean = false
	private var mUserLearnedDrawer: Boolean = false
	private var searchItem: MenuItem? = null
	private var checkBoxSlounikOrg: CheckBox? = null
	private var checkBoxSkarnik: CheckBox? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		// Read in the flag indicating whether or not the user has demonstrated awareness of the
		// drawer. See PREF_USER_LEARNED_DRAWER for details.
		val sp = PreferenceManager.getDefaultSharedPreferences(activity)
		mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false)

		if (savedInstanceState != null) {
			mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION)
			mFromSavedInstanceState = true
		}

		// Select either the default item (0) or the last selected item.
		selectItem(mCurrentSelectedPosition)
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		// Indicate that this fragment would like to influence the set of actions in the action bar.
		setHasOptionsMenu(true)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.drawer, container, false)
	}

	val isDrawerOpen: Boolean
		get() = mDrawerLayout != null && mDrawerLayout!!.isDrawerOpen(mFragmentContainerView!!)

	/**
	 * Users of this fragment must call this method to set up the navigation drawer interactions.
	 *
	 * @param fragmentId   The android:id of this fragment in its activity's layout.
	 * @param drawerLayout The DrawerLayout containing this fragment's UI.
	 */
	fun setUp(fragmentId: Int, drawerLayout: DrawerLayout) {
		mFragmentContainerView = activity.findViewById(fragmentId)
		mDrawerLayout = drawerLayout

		// set a custom shadow that overlays the main content when the drawer opens
		mDrawerLayout!!.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START)
		// set up the drawer's list view with items and click listener

		val actionBar = actionBar
		actionBar.setDisplayHomeAsUpEnabled(true)
		actionBar.setHomeButtonEnabled(true)

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the navigation drawer and the action bar app icon.
		mDrawerToggle = object : ActionBarDrawerToggle(
				activity, /* host Activity */
				mDrawerLayout, /* DrawerLayout object */
				R.string.navigation_drawer_open, /* "open drawer" description for accessibility */
				R.string.navigation_drawer_close  /* "close drawer" description for accessibility */) {
			override fun onDrawerClosed(drawerView: View?) {
				super.onDrawerClosed(drawerView)
				if (!isAdded) {
					return
				}

				activity.supportInvalidateOptionsMenu() // calls onPrepareOptionsMenu()
			}

			override fun onDrawerOpened(drawerView: View?) {
				super.onDrawerOpened(drawerView)
				if (!isAdded) {
					return
				}

				if (!mUserLearnedDrawer) {
					// The user manually opened the drawer; store this flag to prevent auto-showing
					// the navigation drawer automatically in the future.
					mUserLearnedDrawer = true
					val sp = PreferenceManager.getDefaultSharedPreferences(activity)
					sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).commit()
				}

				activity.supportInvalidateOptionsMenu() // calls onPrepareOptionsMenu()
			}
		}

		// If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
		// per the navigation drawer design guidelines.
		if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
			mDrawerLayout!!.openDrawer(mFragmentContainerView)
		}

		// Defer code dependent on restoration of previous instance state.
		mDrawerLayout!!.post { mDrawerToggle!!.syncState() }

		mDrawerLayout!!.addDrawerListener(mDrawerToggle!!)

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

	private fun selectItem(position: Int) {
		mCurrentSelectedPosition = position
		if (mDrawerLayout != null) {
			mDrawerLayout!!.closeDrawer(mFragmentContainerView)
		}
		if (mCallbacks != null) {
			mCallbacks!!.onNavigationDrawerItemSelected(position)
		}
	}

	override fun onAttach(context: Context?) {
		super.onAttach(context)

		try {
			mCallbacks = context as NavigationDrawerCallbacks?
		} catch (e: ClassCastException) {
			throw ClassCastException("Activity must implement NavigationDrawerCallbacks.")
		}

	}

	override fun onDetach() {
		super.onDetach()
		mCallbacks = null
	}

	override fun onSaveInstanceState(outState: Bundle?) {
		super.onSaveInstanceState(outState)
		outState!!.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition)
	}

	override fun onConfigurationChanged(newConfig: Configuration?) {
		super.onConfigurationChanged(newConfig)
		// Forward the new configuration the drawer toggle component.
		mDrawerToggle!!.onConfigurationChanged(newConfig)
	}

	override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
		// If the drawer is open, show the global app actions in the action bar. See also
		// showGlobalContextActionBar, which controls the top-left area of the action bar.
		if (mDrawerLayout != null && isDrawerOpen) {
			inflater!!.inflate(R.menu.main, menu)
			showGlobalContextActionBar()
		}

		searchItem = menu!!.findItem(R.id.action_search)
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

		super.onCreateOptionsMenu(menu, inflater)
	}

	override fun onOptionsItemSelected(item: MenuItem?): Boolean {
		if (mDrawerToggle!!.onOptionsItemSelected(item)) {
			return true
		}

		if (mDrawerLayout != null) {
			mDrawerLayout!!.closeDrawer(mFragmentContainerView)
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

	private val actionBar: ActionBar
		get() = (activity as AppCompatActivity).supportActionBar ?: StubActionBar()

	/**
	 * Callbacks interface that all activities using this fragment must implement.
	 */
	interface NavigationDrawerCallbacks {
		/**
		 * Called when an item in the navigation drawer is selected.
		 */
		fun onNavigationDrawerItemSelected(position: Int)
	}

	companion object {
		/**
		 * Remember the position of the selected item.
		 */
		private val STATE_SELECTED_POSITION = "selected_navigation_drawer_position"

		/**
		 * Per the design guidelines, you should show the drawer on launch until the user manually
		 * expands it. This shared preference tracks this.
		 */
		private val PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned"
	}
}
