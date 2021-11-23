package com.nextinger.binancer.ui

import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.nextinger.binancer.R
import com.nextinger.binancer.data.objects.ContentState
import com.nextinger.binancer.data.objects.enums.State
import com.nextinger.binancer.databinding.ActivityMainBinding
import com.nextinger.binancer.models.MainViewModel
import com.nextinger.binancer.utils.AnimationUtils
import com.nextinger.binancer.utils.backdrop.BackdropBehavior
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawer: DrawerLayout
    private lateinit var backdropBehavior: BackdropBehavior
    private lateinit var viewModel: MainViewModel

    private var state: State? = null
    // TODO figure out better solution
    private lateinit var contentLayout: View
    private lateinit var noResultLayout: View
    private lateinit var progressLayout: View
    private lateinit var errorLayout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        contentLayout = binding.appBarMain.content.navHostFragment
        noResultLayout = binding.appBarMain.content.noResultLayout
        progressLayout = binding.appBarMain.content.progressLayout
        errorLayout = binding.appBarMain.content.errorLayout

        viewModel.contentState.observe(this, { contentState ->
            handleContentState(contentState)
        })

        drawer = binding.drawerLayout
        val toolbar = binding.appBarMain.toolbar
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_menu)

        toolbar.setNavigationOnClickListener {
            drawer.openDrawer(GravityCompat.START)
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val bottomNavController = navHostFragment.navController
        val bottomAppBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_ticker,
                R.id.navigation_spot_wallet,
                R.id.navigation_order_history,
                R.id.navigation_trade_history
            )
        )

        setupActionBarWithNavController(bottomNavController, bottomAppBarConfiguration)
        binding.appBarMain.bottomNavView.setupWithNavController(bottomNavController)
        // initialize backdrop
        backdropBehavior = binding.appBarMain.content.content.findBehavior()
        with(backdropBehavior) {
            attachBackLayout(R.id.backLayout)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.drop_menu, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        when {
            drawer.isDrawerOpen(GravityCompat.START) -> {
                drawer.closeDrawer(GravityCompat.START)
            }
            backdropBehavior.isOpen() -> {
                backdropBehavior.close(true)
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    /**
     * Set dropdown filter fragment
     */
    fun setDropdownSettingsFragment(fragment: Fragment) {
        if (backdropBehavior.isOpen()) {
            backdropBehavior.close(true)
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.settings_container, fragment)
            .commit()
    }

    /**
     * Find backdrop behavior
     */
    fun <T : CoordinatorLayout.Behavior<*>> View.findBehavior(): T = layoutParams.run {
        if (this !is CoordinatorLayout.LayoutParams) throw IllegalArgumentException("View's layout params should be CoordinatorLayout.LayoutParams")

        (layoutParams as CoordinatorLayout.LayoutParams).behavior as? T
            ?: throw IllegalArgumentException("Layout's behavior is not current behavior")
    }

    /**
     * Handle content state
     */
    private fun handleContentState(contentState: ContentState) {

        if (state == contentState.state) {
            when (state) {
                // update message
                State.LOADING -> {}
                State.NO_RESULT -> {}
                State.ERROR -> {}
                else -> {}
            }
            return
        }

        // handle previous state
        when (state) {
            State.CONTENT -> AnimationUtils.fadeOut(contentLayout)
            State.LOADING -> AnimationUtils.fadeOut(progressLayout)
            State.NO_RESULT -> AnimationUtils.fadeOut(noResultLayout)
            State.ERROR -> AnimationUtils.fadeOut(errorLayout)
            null -> {}
        }

        state = contentState.state

        // handle current state
        when (contentState.state) {
            State.CONTENT -> AnimationUtils.fadeIn(contentLayout)
            State.LOADING -> {
                contentLayout.visibility = View.GONE
                AnimationUtils.fadeIn(progressLayout)
            }
            State.NO_RESULT -> {
                contentLayout.visibility = View.GONE
                AnimationUtils.startGifAnimationOnce(
                    binding.appBarMain.content.noResultImageView,
                    R.raw.anim_confused_travolta_meme,
                    false
                )
                AnimationUtils.fadeIn(noResultLayout)
                binding.appBarMain.content.noResultTextView.text = contentState.message
            }
            State.ERROR -> {
                contentLayout.visibility = View.GONE
                AnimationUtils.startGifAnimationOnce(
                    binding.appBarMain.content.errorImageView,
                    R.raw.anim_sad_kid_meme,
                    true
                )
                AnimationUtils.fadeIn(errorLayout)
                binding.appBarMain.content.errorTextView.text = contentState.message
            }
        }
    }
}