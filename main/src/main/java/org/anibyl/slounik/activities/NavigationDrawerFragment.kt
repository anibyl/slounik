package org.anibyl.slounik.activities

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
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
		fun getSupportActionBar(): ActionBar
	}

	@Inject lateinit var preferences: Preferences

	@BindView(R.id.checkbox_slounik_org) lateinit var checkBoxSlounikOrg: CheckBox
	@BindView(R.id.checkbox_skarnik) lateinit var checkBoxSkarnik: CheckBox
	@BindView(R.id.checkbox_rodnyja_vobrazy) lateinit var checkBoxRodnyjaVobrazy: CheckBox
	@BindView(R.id.checkbox_engbel) lateinit var checkBoxEngBel: CheckBox
	@BindView(R.id.checkbox_search_in_title) lateinit var checkBoxSearchInTitle: CheckBox
	@BindView(R.id.drawer_about_button) lateinit var aboutButton: Button

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

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		// Indicate that this fragment would like to influence the set of actions in the action bar.
		setHasOptionsMenu(true)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		val view: View = inflater.inflate(R.layout.drawer, container, false)
		ButterKnife.bind(this, view)
		return view
	}

	override fun onAttach(context: Context) {
		super.onAttach(context)

		try {
			callbacks = context as NavigationDrawerCallbacks
		} catch (e: ClassCastException) {
			throw ClassCastException("Activity must implement NavigationDrawerCallbacks.")
		}
	}

	override fun onConfigurationChanged(newConfig: Configuration) {
		super.onConfigurationChanged(newConfig)
		// Forward the new configuration the drawer toggle component.
		drawerToggle!!.onConfigurationChanged(newConfig)
	}

	fun close() {
		if (drawerLayout != null && fragmentContainerView != null) {
			drawerLayout!!.closeDrawer(fragmentContainerView!!)
		}
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		if (drawerToggle!!.onOptionsItemSelected(item)) {
			return true
		}

		close()

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
			override fun onDrawerClosed(drawerView: View) {
				super.onDrawerClosed(drawerView)

				if (isAdded) {
					activity?.invalidateOptionsMenu()
				}
			}

			override fun onDrawerOpened(drawerView: View) {
				super.onDrawerOpened(drawerView)

				if (isAdded) {
					activity?.invalidateOptionsMenu()
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

		checkBoxEngBel.isChecked = preferences.useEngBel
		checkBoxEngBel.setOnCheckedChangeListener { _, isChecked ->
			preferences.useEngBel= isChecked
		}

		checkBoxSearchInTitle.isChecked = preferences.searchInTitles
		checkBoxSearchInTitle.setOnCheckedChangeListener { _, isChecked ->
			preferences.searchInTitles = isChecked
		}

		aboutButton.setOnClickListener {
			if (activity != null) {
				AboutDialog().show(activity!!.supportFragmentManager, "about_dialog")
			}
		}
	}
}
