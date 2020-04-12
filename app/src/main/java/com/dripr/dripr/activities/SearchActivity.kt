package com.dripr.dripr.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dripr.dripr.R
import com.dripr.dripr.entities.Event
import com.dripr.dripr.entities.Token
import com.dripr.dripr.entities.User
import com.dripr.dripr.others.Utils.Companion.onError
import com.dripr.dripr.viewmodels.EventsViewModel
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity() {

    private val peopleBtn = arrayListOf(
        R.id.peopleSelectOne,
        R.id.peopleSelectTwo,
        R.id.peopleSelectMoreThanTwo,
        R.id.peopleSelectMoreThanFive,
        R.id.peopleSelectMoreThanTen,
        R.id.peopleSelectMoreThanThirty
    )

    private val categoryBtn = arrayListOf(
        R.id.categorySelectConcertsAndShows,
        R.id.categorySelectGastronomy,
        R.id.categorySelectProfessionnal,
        R.id.categorySelectScienceAndTech,
        R.id.categorySelectHealthAndWellbeing,
        R.id.categorySelectPerformingArts,
        R.id.categorySelectTravelAndOutdoor,
        R.id.categorySelectCommunityAndCultural,
        R.id.categorySelectSportAndFitness,
        R.id.categorySelectFashionAndBeauty,
        R.id.categorySelectFamilyAndEducation,
        R.id.categorySelectPassionsAndLeisure,
        R.id.categorySelectHomeAndLifestyle,
        R.id.categorySelectCharity,
        R.id.categorySelectReligionAndSpirituality,
        R.id.categorySelectPartyAndSeasonal,
        R.id.categorySelectPoliticsAndGovernment,
        R.id.categorySelectMoviesAndEntertainment,
        R.id.categorySelectOther
    )

    private lateinit var eventsViewModel: EventsViewModel
    private var selectedCategory: String? = null
    private val btnCategoriesMap = HashMap<Int, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // View Model
        eventsViewModel = ViewModelProvider(this).get(EventsViewModel::class.java)

        // Logic
        val categories = resources.getStringArray(R.array.eventsCategories)
        for (i in categories.indices) btnCategoriesMap[categoryBtn[i]] = categories[i]

        // Listeners
        btnSearch.setOnClickListener { search() }
    }


    private fun search() {
        val options = HashMap<String, String>()
        if(selectedCategory != null) options["category"] = selectedCategory!!

        val token: Token = User.getFromDevice(this).tokens.last()!!
        val eventsLiveData: LiveData<List<Event>> = eventsViewModel.search(token, options) {
            onError(this.applicationContext, it)
        }

        eventsLiveData.observe(this, Observer {
            val events = it
            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra("SEARCH_EVENTS", ArrayList(events))
            startActivity(intent)
            finish()
        })
    }

    fun btPeopleToggleClick(view: View?) {
        if (view is Button) {
            peopleBtn.forEach { id ->
                val btn = findViewById<Button>(id)
                if(btn.id == view.id) {
                    if (view.isSelected) {
                        view.setTextColor(Color.WHITE)
                        view.isSelected = false
                    } else {
                        view.setTextColor(Color.BLACK)
                        view.isSelected = true
                    }
                } else {
                    btn.setTextColor(Color.WHITE)
                    btn.isSelected = false
                }
            }
        }
    }

    fun btCategoryToggleClick(view: View?) {
        if (view is Button) {
            categoryBtn.forEach { id ->
                val btn = findViewById<Button>(id)
                if (btn.id == view.id) {
                    if (view.isSelected) {
                        view.setTextColor(Color.WHITE)
                        view.isSelected = false
                    } else {
                        view.setTextColor(Color.BLACK)
                        view.isSelected = true

                        selectedCategory = btnCategoriesMap[btn.id]
                    }
                } else {
                    btn.setTextColor(Color.WHITE)
                    btn.isSelected = false
                }
            }
        }
    }
}