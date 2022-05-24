package com.example.audiobook.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import com.example.audiobook.R


class GenreFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val genreView = inflater.inflate(R.layout.fragment_genre, container, false)
        val listGenres: ListView = genreView.findViewById(R.id.list_genre)
        val buttonSearch: Button = genreView.findViewById(R.id.buttonSearch)
        val textSearch: EditText = genreView.findViewById(R.id.editTextSearch)
        var url = ""
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

        val genresAdapter = ArrayAdapter(
            this.requireContext(),
            android.R.layout.simple_list_item_1,
            genresData
        )

        listGenres.adapter = genresAdapter
        genresAdapter.notifyDataSetChanged()

        listGenres.setOnItemClickListener {
                parent, view, position, id ->
            when(position)
            {
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
                21 -> url = "genre/jumor-satira/popular/?period=alltime"
            }

            SearchFragment.childFragment = ListBooksFragment(url, "genre")
            refresh()
        }

        buttonSearch.setOnClickListener{
            url = "search/?q=${textSearch.text.toString().replace(" ", "%20")}"
            SearchFragment.childFragment = ListBooksFragment(url, "search")
            refresh()
        }

        return genreView
    }

    private fun refresh()
    {
        parentFragment?.onDetach()
        parentFragment?.onAttach(requireContext())
    }

}