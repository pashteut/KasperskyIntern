package com.example.kasperskyintern.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.kasperskyintern.R
import com.example.kasperskyintern.databinding.FragmentMainBinding
import com.example.kasperskyintern.presentator.MainFragmentViewModel
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding
        get() = _binding!!
    private val viewModel: MainFragmentViewModel by viewModels()
    private val adapter by lazy {
        TranslationAdapter (
            onFavouritesButtonClick = { translationItem ->
                viewModel.addToFavouritesButtonClicked(translationItem)
            },
            onDeleteButtonClick = { translationItem ->
                viewModel.deleteButtonClicked(translationItem)
            }
        )
    }
    private var editTextHasFocus = false
    private var appBarLayoutBehavior: AppBarLayout.Behavior? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        binding.recyclerView.adapter = adapter

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        collectViewModel()

        setListeners()

        // для работы функции layoutCanBeScrolled(Boolean)
        val params = binding.appBarLayout.layoutParams as CoordinatorLayout.LayoutParams
        if (params.behavior == null)
            params.behavior = AppBarLayout.Behavior()
        appBarLayoutBehavior = params.behavior as AppBarLayout.Behavior

        // нужен для того, чтобы при добавлении нового элемента в список, rv прокручивался вверх
        // иначе в ситуации, когда рв заполнен, новый элемент добавляется сверху, но rv остается на месте, и новый элемент не виден
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0) {
                    binding.recyclerView.scrollToPosition(0)
                }
            }
        })

        return binding.root
    }

    private fun collectViewModel() {
        viewLifecycleOwner.lifecycleScope.run {
            launch {
                viewModel.translation.collect {
                    if (it == "" && !editTextHasFocus)
                        showTranslationWindow(false)
                    binding.translatedText.text = it
                }
            }
            launch {
                viewModel
                    .translationHistory
                    .flowOn(Dispatchers.IO)
                    .collect {
                        adapter.submitList(it)
                        binding.historyToolbar.visibility =
                            if (it.isEmpty()) View.GONE else View.VISIBLE
                        layoutCanBeScrolled(it.isNotEmpty())
                    }
            }
            launch {
                viewModel.text.collect {
                    binding.clearTextButton.visibility = if (it != "") View.VISIBLE else View.GONE
                }
            }
        }
    }

    private fun setListeners() {
        binding.run {
            favouritesButton.setOnClickListener {
                findNavController().navigate(R.id.action_mainFragment_to_favouritesFragment)
            }

            clearTextButton.setOnClickListener {
                viewModel!!.text.value = ""
            }

            clearHistoryButton.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle(resources.getString(R.string.clearHistoryDialogTitle))
                    .setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                        viewModel!!.clearHistory()
                        binding.appBarLayout.setExpanded(true)
                    }
                    .setNegativeButton(resources.getString(R.string.no)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }

            textInputEditText.setOnFocusChangeListener { _, hasFocus ->
                editTextHasFocus = hasFocus
                if (hasFocus)
                    showTranslationWindow(true)
                else {
                    viewModel!!.textChanged()
                    if (viewModel!!.text.value == "")
                        showTranslationWindow(false)
                }
            }
        }
    }

    private fun showTranslationWindow(show: Boolean) =
        if (show) {
            binding.cardView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            binding.textInputLayout.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            binding.translatedTextLayout.visibility = View.VISIBLE
        } else {
            binding.cardView.layoutParams.height = resources.getDimension(R.dimen.dimen_400dp).toInt()
            binding.textInputLayout.layoutParams.height = 0
            binding.translatedTextLayout.visibility = View.GONE
        }

    private fun layoutCanBeScrolled(scroll: Boolean) {
        appBarLayoutBehavior?.setDragCallback(object : AppBarLayout.Behavior.DragCallback() {
            override fun canDrag(p0: AppBarLayout): Boolean {
                return scroll
            }
        })
        binding.recyclerView.isNestedScrollingEnabled = scroll
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}