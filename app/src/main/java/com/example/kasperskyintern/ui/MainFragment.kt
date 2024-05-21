package com.example.kasperskyintern.ui

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.kasperskyintern.R
import com.example.kasperskyintern.databinding.FragmentMainBinding
import com.example.kasperskyintern.presentator.MainFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding
        get() = _binding!!
    private val viewModel: MainFragmentViewModel by viewModels()
    private var keyboardUp = MutableStateFlow(false)
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
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        binding.recyclerView.adapter = adapter

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        collectViewModel()

        setKeyboardListener()

        binding.favouritesButton.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_favouritesFragment)
        }

        binding.clearTextButton.setOnClickListener{
            viewModel.text.value = ""
        }

        binding.clearHistoryButton.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(resources.getString(R.string.clearHistoryDialogTitle))
                .setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                    viewModel.clearHistory()
                }
                .setNegativeButton(resources.getString(R.string.no)) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }


        // нужен для того, чтобы при добавлении нового элемента в список, rv прокручивался вверх
        // иначе в ситуации, когда рв заполнен, новый элемент добавляется сверху, но rv остается на месте и новый элемент не виден
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
                    if (it != "")
                        showTranslationWindow(true)
                    else
                        showTranslationWindow(false)
                    binding.translatedText.text = it
                }
            }
            launch {
                keyboardUp.collect {
                    if (!it)
                        textChanged()
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
                    }
            }
            launch {
                viewModel.text.collect {
                    binding.clearTextButton.visibility = if (it != "") View.VISIBLE else View.GONE
                }
            }
        }
    }

    private fun setKeyboardListener() {
        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            if (_binding != null) {
                val r = Rect()
                binding.root.getWindowVisibleDisplayFrame(r)
                val screenHeight = binding.root.rootView.height

                val keypadHeight = screenHeight - r.bottom

                if (keypadHeight > screenHeight * 0.15) {
                    // keyboard is opened
                    if (!keyboardUp.value)
                        keyboardUp.value = true
                } else {
                    // keyboard is closed
                    if (keyboardUp.value)
                        keyboardUp.value = false
                }
            }
        }
    }

    private fun textChanged() {
        binding.textInputEditText.clearFocus()
        viewModel.textChanged()
    }

    private fun showTranslationWindow(show: Boolean) {
        if (show) {
            binding.cardView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            binding.textInputLayout.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            binding.translatedTextLayout.visibility = View.VISIBLE
        } else {
            binding.cardView.layoutParams.height = resources.getDimension(R.dimen.dimen_400dp).toInt()
            binding.textInputLayout.layoutParams.height = 0
            binding.translatedTextLayout.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}