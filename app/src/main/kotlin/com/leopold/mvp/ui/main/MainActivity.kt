package com.leopold.mvp.ui.main

import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.leopold.mvp.ActivityModule
import com.leopold.mvp.App
import com.leopold.mvp.R
import com.leopold.mvp.component.DaggerActivityComponent
import com.leopold.mvp.extensions.setToolbar
import com.leopold.mvp.model.repository.Repository
import com.leopold.mvp.network.error.ErrorResponse
import com.leopold.mvp.presenter.ActivityPresenterModule
import com.leopold.mvp.presenter.BasePresenter
import com.leopold.mvp.presenter.main.MainPresenter
import com.leopold.mvp.ui.PresenterActivity
import com.leopold.mvp.ui.widget.recycler.EndlessLinearRecyclerListener
import com.leopold.mvp.ui.widget.recycler.OnItemClickListener
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

/**
 * @author Leopold
 */
class MainActivity : PresenterActivity<MainPresenter.View>(), MainPresenter.View, OnItemClickListener {
    private lateinit var drawerToggle: ActionBarDrawerToggle
    @Inject
    lateinit var presenter: MainPresenter
    private var adapter: RepositoryRecyclerAdapter? = null

    private val toolbar by lazy { main_toolbar }
    private val refreshLayout by lazy { main_refresh_layout }
    private val recyclerView by lazy { main_recycler_view }
    private val moreProgress by lazy { main_more_progress }

    override fun getPresenter(): BasePresenter<MainPresenter.View>? {
        return presenter
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_main
    }

    override fun inject() {
        DaggerActivityComponent.builder()
                .appComponent(App.getAppComponent(this))
                .activityModule(ActivityModule(this))
                .activityPresenterModule(ActivityPresenterModule())
                .build().inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setToolbar(toolbar, R.string.app_name)

        val layoutManager = LinearLayoutManager(this)
        val endless = object : EndlessLinearRecyclerListener(layoutManager) {
            override fun onLoadMore() {
                presenter.onLoadMore()
            }
        }

        recyclerView.setHasFixedSize(false)
        recyclerView.addOnScrollListener(endless)
        recyclerView.layoutManager = layoutManager
        refreshLayout.setOnRefreshListener { presenter.onRefresh() }

        presenter.searchRepositories()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        drawerToggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.general_empty, R.string.general_empty).apply {
            syncState()
            drawer.addDrawerListener(this)
        }
    }

    override fun onDestroy() {
        recyclerView?.clearOnScrollListeners()
        drawer?.removeDrawerListener(drawerToggle)
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        drawerToggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)
    }

    override fun showProgress() {
        refreshLayout.isRefreshing = true
    }

    override fun showMoreProgress() {
        moreProgress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        refreshLayout.isRefreshing = false
        moreProgress.visibility = View.GONE
    }

    override fun setAdapter(repositories: ArrayList<Repository>) {
        if (adapter == null) {
            adapter = RepositoryRecyclerAdapter(this, repositories).apply {
                setOnItemClickListener(this@MainActivity)
                recyclerView.adapter = this
            }
        } else {
            adapter?.run {
                if (presenter.isMoreLoading()) {
                    this.concat(repositories)
                } else {
                    this.replace(repositories)
                }
            }
        }
    }

    override fun onItemClick(view: View, position: Int) {

    }

    override fun handleNetworkError(error: ErrorResponse) {

    }
}