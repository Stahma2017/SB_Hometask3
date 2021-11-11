package ru.skillbranch.skillarticles.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.viewmodels.articles.ArticlesViewModel

class ChoseCategoryDialog : DialogFragment() {
    companion object {
        const val CHOOSE_CATEGORY_KEY = "CHOOSE_CATEGORY_KEY"
        const val SELECTED_CATEGORIES = "SELECTED_CATEGORIES"
    }

    private val selected = mutableListOf<String>()
    private val args: ChoseCategoryDialogArgs by navArgs()

    private val categoryAdapter = CategoryAdapter { categoryId: String, isChecked: Boolean ->
        if (isChecked) selected.add(categoryId)
        else selected.remove(categoryId)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        selected.clear()
        selected.addAll(
            savedInstanceState?.getStringArray("checked") ?: args.selectedCategories
        )

        val categoryItems = args.categories.map { it.toItem(selected.contains(it.categoryId)) }

        categoryAdapter.submitList(categoryItems)

        //inflate list
        val listView = LayoutInflater.from(context).inflate(R.layout.fragment_choose_category_dialog, null) as RecyclerView

        //list settings
        with(listView) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoryAdapter
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("Chse category")
            .setPositiveButton("Apply") { _, _ ->
                setFragmentResult(CHOOSE_CATEGORY_KEY, bundleOf(SELECTED_CATEGORIES to selected.toList()))
            }
            .setNegativeButton("Reset") { _, _ ->
                setFragmentResult(CHOOSE_CATEGORY_KEY, bundleOf(SELECTED_CATEGORIES to emptyList<String>()))
            }
            .setView(listView)
            .create()


        // TODO save checked state and implement custom items
        /*val categories = args.categories.toList().map { "${it.title} (${it.articlesCount})" }.toTypedArray()
        val checked = BooleanArray(args.categories.size) {
            args.selectedCategories.contains(args.categories[it].categoryId)
        }

        val adb = AlertDialog.Builder(requireContext())
            .setTitle("Chose category")
            .setPositiveButton("Apply") { _, _ ->
                viewModel.applyCategories(selected)
            }
            .setNegativeButton("Reset") { _, _ ->
                viewModel.applyCategories(emptyList())
            }
            .setMultiChoiceItems(categories, checked) { dialog, which, isChecked ->
                if (isChecked) selected.add(args.categories[which].categoryId)
                else selected.remove(args.categories[which].categoryId)
            }

        return adb.create()*/
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putStringArray("checked", selected.toTypedArray())
        super.onSaveInstanceState(outState)
    }
}