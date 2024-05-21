package com.example.kasperskyintern.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.kasperskyintern.R
import com.example.kasperskyintern.databinding.FragmentFavouritesBinding
import com.example.kasperskyintern.presentator.FavouritesFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class FavouritesFragment : Fragment() {
    private var _binding: FragmentFavouritesBinding? = null
    private val binding get() = _binding!!
    private val viewModel : FavouritesFragmentViewModel by viewModels()
    private val adapter by lazy {
        TranslationAdapter(
            onFavouritesButtonClick = { translationItem ->
                viewModel.addToFavouritesButtonClicked(translationItem)
            },
            onDeleteButtonClick = { translationItem ->
                viewModel.deleteButtonClicked(translationItem)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouritesBinding.inflate(inflater, container, false)

        binding.favouritesRecyclerView.adapter = adapter

        setToolbar()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.favouriteTranslationHistory
                .flowOn(Dispatchers.IO)
                .collect {
                    adapter.submitList(it)
                    withContext(Dispatchers.Main) {
                        binding.emptyView.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
                        binding.favouritesRecyclerView.isNestedScrollingEnabled = it.isNotEmpty()
                    }
                }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            goToMainFragment()
        }

        return binding.root
    }

    private fun setToolbar(){
        (activity as AppCompatActivity).run {
            setSupportActionBar(binding.favouritesToolbar)
            supportActionBar?.run {
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowHomeEnabled(true)
                setDisplayShowTitleEnabled(false)
            }
        }

        binding.favouritesToolbar.setNavigationOnClickListener{
            goToMainFragment()
        }
    }

    private fun goToMainFragment(){
        findNavController().navigate(R.id.action_favouritesFragment_to_mainFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}