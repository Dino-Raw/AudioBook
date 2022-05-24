package com.example.audiobook.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.example.audiobook.R
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.audiobook.adapters.ListBooksAdapter
import com.example.audiobook.viewmodels.ListBooksViewModel
import kotlinx.android.synthetic.main.fragment_search.*


class SearchFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // fragment_search состоит из 2 fragment:
        // создать fragment_genre
        // добавить fragment_list_books


        val searchView = inflater.inflate(R.layout.fragment_search, container, false)

        val genresData = mutableListOf<String>(
            "Фантастика, фэнтези",
            "Детективы, триллеры",
            "Аудиоспектакли",
            "Бизнес",
            "Биографии, мемуары",
            "Для детей, Аудиосказки",
            "История",
            "Классика",
            "Медицина, здоровье",
            "На иностранных языках",
            "Научно-популярное",
            "Обучение",
            "Поэзия",
            "Приключения",
            "Психология, философия",
            "Разное",
            "Ранобэ",
            "Религия",
            "Роман, проза",
            "Ужасы, мистика",
            "Эзотерика",
            "Юмор, сатира"
        )

        val listGenres: ListView = searchView.findViewById(R.id.list_genre)
        val buttonSearch: Button = searchView.findViewById(R.id.buttonSearch)

        val adapter = ArrayAdapter(
            this.requireContext(),
            android.R.layout.simple_list_item_1,
            genresData
        )

        listGenres.adapter = adapter
        adapter.notifyDataSetChanged()

        listGenres.setOnItemClickListener {
                parent, view, position, id ->

            val bundle = Bundle()
            var url: String = ""

            when(position){
                0 -> url = "genre/fantastika/popular/?period=alltime"
                1 -> url = "genre/detektivy-trillery/popular/?period=alltime"
                2 -> url = "genre/audiospektakli/popular/?period=alltime"
                3 -> url = "genre/biznes/popular/?period=alltime"
                4 -> url = "genre/biografii-memuary/popular/?period=alltime"
                5 -> url = "genre/dlja-detejj/popular/?period=alltime"
                6 -> url = "genre/istorija/popular/?period=alltime"
                7 -> url = "genre/klassika/popular/?period=alltime"
                8 -> url = "genre/medicina-zdorove/popular/?period=alltime"
                9 -> url = "genre/na-inostrannykh-jazykakh/popular/?period=alltime"
                10 -> url = "genre/nauchno-populjarnoe/popular/?period=alltime"
                11 -> url = "genre/obuchenie/popular/?period=alltime"
                12 -> url = "genre/poehzija/popular/?period=alltime"
                13 -> url = "genre/prikljuchenija/popular/?period=alltime"
                14 -> url = "genre/psikhologija-filosofija/popular/?period=alltime"
                15 -> url = "genre/raznoe/popular/?period=alltime"
                16 -> url = "genre/ranobeh/popular/?period=alltime"
                17 -> url = "genre/religija/popular/?period=alltime"
                18 -> url = "genre/roman-proza/popular/?period=alltime"
                19 -> url = "genre/uzhasy-mistika/popular/?period=alltime"
                20 -> url = "genre/ehzoterika/popular/?period=alltime"
                else -> url = "genre/jumor-satira/popular/?period=alltime"
            }

            bundle.putString("url", url)
            bundle.putString("type", "genre")

            searchView
                .findNavController()
                .navigate(R.id.action_navigation_search_to_listBooksFragment, bundle)
        }

        buttonSearch.setOnClickListener{
            val bundle = Bundle()

            bundle.putString("type", "search")

            bundle.putString(
                "url",
                "search/?q=${editTextSearch.text.toString().replace(" ", "%20")}"
            )

            searchView
                .findNavController()
                .navigate(R.id.action_navigation_search_to_listBooksFragment, bundle)
        }

        return searchView
    }
}