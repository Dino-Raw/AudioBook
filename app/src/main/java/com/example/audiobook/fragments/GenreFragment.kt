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
    //lateinit var url : String

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
                0 -> url = "genre/fantastika/popular/?period=alltime&page="
                1 -> url = "genre/detektivy-trillery/popular/?period=alltime&page="
                2 -> url = "genre/audiospektakli/popular/?period=alltime&page="
                3 -> url = "genre/biznes/popular/?period=alltime&page="
                4 -> url = "genre/biografii-memuary/popular/?period=alltime&page="
                5 -> url = "genre/dlja-detejj/popular/?period=alltime&page="
                6 -> url = "genre/istorija/popular/?period=alltime&page="
                7 -> url = "genre/klassika/popular/?period=alltime&page="
                8 -> url = "genre/medicina-zdorove/popular/?period=alltime&page="
                9 -> url = "genre/na-inostrannykh-jazykakh/popular/?period=alltime&page="
                10 -> url = "genre/nauchno-populjarnoe/popular/?period=alltime&page="
                11 -> url = "genre/obuchenie/popular/?period=alltime&page="
                12 -> url = "genre/poehzija/popular/?period=alltime&page="
                13 -> url = "genre/prikljuchenija/popular/?period=alltime&page="
                14 -> url = "genre/psikhologija-filosofija/popular/?period=alltime&page="
                15 -> url = "genre/raznoe/popular/?period=alltime&page="
                16 -> url = "genre/ranobeh/popular/?period=alltime&page="
                17 -> url = "genre/religija/popular/?period=alltime&page="
                18 -> url = "genre/roman-proza/popular/?period=alltime&page="
                19 -> url = "genre/uzhasy-mistika/popular/?period=alltime&page="
                20 -> url = "genre/ehzoterika/popular/?period=alltime&page="
                21 -> url = "genre/jumor-satira/popular/?period=alltime&page="
            }

            val bundle = Bundle()

            bundle.putString("type", "genre")
            bundle.putString("url", url)

            SearchFragment.childFragment = ListBooksFragment(url, "genre")
            SearchFragment.childFragment.arguments = bundle

            refresh()
        }

        buttonSearch.setOnClickListener{
            url = "search/?q=${textSearch.text.toString().replace(" ", "%20")}&page="

            val bundle = Bundle()

            bundle.putString("type", "search")
            bundle.putString("url", url)

            SearchFragment.childFragment = ListBooksFragment(url, "search")
            SearchFragment.childFragment.arguments = bundle

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