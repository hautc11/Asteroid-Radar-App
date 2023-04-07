package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.adapter.ListAsteroidAdapter
import com.udacity.asteroidradar.api.loadImageUrl
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.repository.AsteroidRepository

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    private val viewModel: MainViewModel by lazy {
        val database = getDatabase(requireContext())
        val asteroidRepo = AsteroidRepository(database)
        ViewModelProvider(this, MainViewModel.Factory(asteroidRepo))[MainViewModel::class.java]
    }
    private val adapter by lazy {
        ListAsteroidAdapter().apply {
            onAsteroidClicked = {
                findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.asteroidRecycler.adapter = adapter
        createOptionMenu()
        viewModel.getPictureOfDay()
        viewModel.refreshAsteroids()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.imageOfDay.observe(viewLifecycleOwner) { pictureResponse ->
            if (pictureResponse.mediaType == "image") {
                context?.let { nonNullContext ->
                    binding.activityMainImageOfTheDay.loadImageUrl(
                        pictureResponse.url,
                        nonNullContext,
                        onSuccessLoad = {
                            binding.statusLoadingWheel.isVisible = false
                        }
                    )
                }
            }
        }

        viewModel.asteroidsView.observe(viewLifecycleOwner) { asteroids ->
            if (asteroids.isNotEmpty()) {
                adapter.submitList(asteroids)
            }
            binding.statusLoadingWheel.isVisible = false
        }
    }

    private fun createOptionMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_overflow_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId) {
                    R.id.show_saved_asteroid -> viewModel.displaySavedAsteroids()
                    R.id.show_today_asteroid -> viewModel.displayTodayAsteroids()
                    R.id.show_week_asteroid -> viewModel.displayWeekAsteroids()
                }
                return true
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
}
