package org.anibyl.slounik.util

import android.graphics.drawable.Drawable
import android.support.v7.app.ActionBar
import android.view.View
import android.widget.SpinnerAdapter

/**
 * Created by anibyl on 18.8.16.
 */
class StubActionBar:ActionBar() {
	private val tab = StubTab()

	override fun setListNavigationCallbacks(adapter:SpinnerAdapter?, callback:OnNavigationListener?) {
	}

	override fun setSelectedNavigationItem(position:Int) {
	}

	override fun setDisplayOptions(options:Int) {
	}

	override fun setDisplayOptions(options:Int, mask:Int) {
	}

	override fun removeOnMenuVisibilityListener(listener:OnMenuVisibilityListener?) {
	}

	override fun getCustomView():View {
		return StubView(null!!)
	}

	override fun setCustomView(view:View?) {
	}

	override fun setCustomView(view:View?, layoutParams:LayoutParams?) {
	}

	override fun setCustomView(resId:Int) {
	}

	override fun hide() {
	}

	override fun getSubtitle():CharSequence {
		return ""
	}

	override fun setTitle(title:CharSequence?) {
	}

	override fun setTitle(resId:Int) {
	}

	override fun getTitle():CharSequence {
		return ""
	}

	override fun setDisplayShowHomeEnabled(showHome:Boolean) {
	}

	override fun setDisplayHomeAsUpEnabled(showHomeAsUp:Boolean) {
	}

	override fun setDisplayUseLogoEnabled(useLogo:Boolean) {
	}

	override fun getTabCount():Int {
		return 0
	}

	override fun getDisplayOptions():Int {
		return 0
	}

	override fun setLogo(resId:Int) {
	}

	override fun setLogo(logo:Drawable?) {
	}

	override fun getHeight():Int {
		return 0
	}

	override fun show() {
	}

	override fun isShowing():Boolean {
		return false
	}

	override fun newTab():Tab {
		return tab
	}

	override fun setBackgroundDrawable(d:Drawable?) {
	}

	override fun setNavigationMode(mode:Int) {
	}

	override fun removeTabAt(position:Int) {
	}

	override fun getTabAt(index:Int):Tab {
		return tab
	}

	override fun addTab(tab:Tab?) {
	}

	override fun addTab(tab:Tab?, setSelected:Boolean) {
	}

	override fun addTab(tab:Tab?, position:Int) {
	}

	override fun addTab(tab:Tab?, position:Int, setSelected:Boolean) {
	}

	override fun setIcon(resId:Int) {
	}

	override fun setIcon(icon:Drawable?) {
	}

	override fun removeAllTabs() {
	}

	override fun getNavigationItemCount():Int {
		return 0
	}

	override fun addOnMenuVisibilityListener(listener:OnMenuVisibilityListener?) {
	}

	override fun removeTab(tab:Tab?) {
	}

	override fun setSubtitle(subtitle:CharSequence?) {
	}

	override fun setSubtitle(resId:Int) {
	}

	override fun setDisplayShowTitleEnabled(showTitle:Boolean) {
	}

	override fun getSelectedTab():Tab {
		return tab
	}

	override fun selectTab(tab:Tab?) {
	}

	override fun getNavigationMode():Int {
		return 0
	}

	override fun setDisplayShowCustomEnabled(showCustom:Boolean) {
	}

	override fun getSelectedNavigationIndex():Int {
		return 0
	}

	internal class StubTab:Tab() {
		override fun getIcon():Drawable {
			return StubDrawable()
		}

		override fun select() {
		}

		override fun setIcon(icon:Drawable?):Tab {
			return this
		}

		override fun setIcon(resId:Int):Tab {
			return this
		}

		override fun setContentDescription(resId:Int):Tab {
			return this
		}

		override fun setContentDescription(contentDesc:CharSequence?):Tab {
			return this
		}

		override fun getContentDescription():CharSequence {
			return ""
		}

		override fun getCustomView():View {
			return StubView(null!!)
		}

		override fun getPosition():Int {
			return 0
		}

		override fun setCustomView(view:View?):Tab {
			return this
		}

		override fun setCustomView(layoutResId:Int):Tab {
			return this
		}

		override fun getText():CharSequence {
			return ""
		}

		override fun setText(text:CharSequence?):Tab {
			return this
		}

		override fun setText(resId:Int):Tab {
			return this
		}

		override fun setTabListener(listener:TabListener?):Tab {
			return this
		}

		override fun setTag(obj:Any?):Tab {
			return this
		}

		override fun getTag():Any {
			return ""
		}
	}
}