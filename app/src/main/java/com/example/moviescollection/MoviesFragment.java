package com.example.moviescollection;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.moviescollection.data.DatabaseDescription.Movie;

public class MoviesFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    //
    public interface MoviesFragmentListener {
        //
        void onMovieSelected(Uri movieUri);

        //
        void onAddMovie();
    }

    private static final int MOVIES_LOADER = 0; // identifies Loader

    private MoviesFragmentListener listener;

    private MoviesAdapter moviesAdapter;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);

        //
        View view = inflater.inflate(
                R.layout.fragment_movies, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        //
        recyclerView.setLayoutManager(
                new LinearLayoutManager(getActivity().getBaseContext()));

        //
        moviesAdapter = new MoviesAdapter(
                new MoviesAdapter.MovieClickListener() {
                    @Override
                    public void onClick(Uri movieUri) {
                        listener.onMovieSelected(movieUri);
                    }
                }
                );
        recyclerView.setAdapter(moviesAdapter);

        //
        recyclerView.addItemDecoration(new ItemDivider(getContext()));

        //
        recyclerView.setHasFixedSize(true);

        //
        FloatingActionButton addButton =
                (FloatingActionButton) view.findViewById(R.id.addButton);
        addButton.setOnClickListener(
                new View.OnClickListener() {
                    //
                    @Override
                    public void onClick(View view) {
                        listener.onAddMovie();
                    }
                }
        );

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (MoviesFragmentListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(MOVIES_LOADER, null, this);
    }

    public void updateMovieList() {
        moviesAdapter.notifyDataSetChanged();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //
        //
        switch (id) {
            case MOVIES_LOADER:
                return new CursorLoader(getActivity(),
                        Movie.CONTENT_URI,
                        null,
                        null,
                        null,
                        Movie.COLUMN_TITLE + " COLLATE NOCASE ASC");
            default:
                return null;
        }
    }

    //
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        moviesAdapter.swapCursor(data);
    }

    //
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        moviesAdapter.swapCursor(null);
    }

}
