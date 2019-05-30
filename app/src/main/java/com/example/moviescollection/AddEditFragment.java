package com.example.moviescollection;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.example.moviescollection.data.DatabaseDescription;
import com.example.moviescollection.data.DatabaseDescription.Movie;

public class AddEditFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    public interface AddEditFragmentListener {
        void onAddEditCompleted(Uri movieUri);
    }

    private static final int MOVIE_LOADER = 0;

    private AddEditFragmentListener listener;
    private Uri movieUri;
    private boolean addingNewMovie = true;

    private TextInputLayout titleTextInputLayout;
    private TextInputLayout yearTextInputLayout;
    private TextInputLayout ratingTextInputLayout;
    private TextInputLayout lengthTextInputLayout;
    private TextInputLayout directorTextInputLayout;
    private TextInputLayout scoreTextInputLayout;
    private FloatingActionButton saveMovieFAB;

    private CoordinatorLayout coordinatorLayout;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (AddEditFragmentListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);

        View view =
                inflater.inflate(R.layout.fragment_add_edit, container, false);
        titleTextInputLayout =
                (TextInputLayout) view.findViewById(R.id.titleTextInputLayout);
        titleTextInputLayout.getEditText().addTextChangedListener(
                titleChangedListener);
        yearTextInputLayout =
                (TextInputLayout) view.findViewById(R.id.yearTextInputLayout);
        ratingTextInputLayout =
                (TextInputLayout) view.findViewById(R.id.ratingTextInputLayout);
        lengthTextInputLayout =
                (TextInputLayout) view.findViewById(R.id.lengthTextInputLayout);
        directorTextInputLayout =
                (TextInputLayout) view.findViewById(R.id.directorTextInputLayout);
        scoreTextInputLayout =
                (TextInputLayout) view.findViewById(R.id.scoreTextInputLayout);

        //
        saveMovieFAB = (FloatingActionButton) view.findViewById(
                R.id.saveFloatingActionButton);
        saveMovieFAB.setOnClickListener(saveMovieButtonClicked);
        updateSaveButtonFAB();

        coordinatorLayout = (CoordinatorLayout) getActivity().findViewById(
                R.id.coordinatorLayout);

        Bundle arguments = getArguments();

        if (arguments != null) {
            addingNewMovie = false;
            movieUri = arguments.getParcelable(MainActivity.MOVIE_URI);
        }

        if(movieUri != null)
            getLoaderManager().initLoader(MOVIE_LOADER, null, this);

        return view;
    }

    private final TextWatcher titleChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            updateSaveButtonFAB();
        }

        @Override
        public void afterTextChanged(Editable s) { }
    };

    private void updateSaveButtonFAB() {
        String input =
                titleTextInputLayout.getEditText().getText().toString();

        if (input.trim().length() != 0)
            saveMovieFAB.show();
        else
            saveMovieFAB.hide();
    }

    private final View.OnClickListener saveMovieButtonClicked =
            new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ((InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                            getView().getWindowToken(),0);
            saveMovie();
        }
    };

    // save movie info to database
    private void saveMovie() {
        // create ContentValues object containing movie's key-value pairs
        ContentValues contentValues = new ContentValues();
        contentValues.put(Movie.COLUMN_TITLE,
                titleTextInputLayout.getEditText().getText().toString());
        contentValues.put(Movie.COLUMN_YEAR,
                yearTextInputLayout.getEditText().getText().toString());
        contentValues.put(Movie.COLUMN_RATING,
                ratingTextInputLayout.getEditText().getText().toString());
        contentValues.put(Movie.COLUMN_LENGTH,
                lengthTextInputLayout.getEditText().getText().toString());
        contentValues.put(Movie.COLUMN_DIRECTOR,
                directorTextInputLayout.getEditText().getText().toString());
        contentValues.put(Movie.COLUMN_SCORE,
                scoreTextInputLayout.getEditText().getText().toString());

        if (addingNewMovie) {
            Uri newMovieUri = getActivity().getContentResolver().insert(
                    Movie.CONTENT_URI, contentValues);

            if (newMovieUri != null) {
                Snackbar.make(coordinatorLayout,
                        R.string.movie_added, Snackbar.LENGTH_LONG).show();
                listener.onAddEditCompleted(newMovieUri);
            }
            else {
                Snackbar.make(coordinatorLayout,
                        R.string.movie_not_added, Snackbar.LENGTH_LONG).show();
            }
        }
        else {
            int updatedRows = getActivity().getContentResolver().update(
                    movieUri, contentValues, null, null);

            if (updatedRows > 0) {
                listener.onAddEditCompleted(movieUri);
                Snackbar.make(coordinatorLayout,
                        R.string.movie_updated, Snackbar.LENGTH_LONG).show();
            }
            else {
                Snackbar.make(coordinatorLayout,
                        R.string.movie_not_updated, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {
            case MOVIE_LOADER:
                return new CursorLoader(getActivity(),
                        movieUri,
                        null,
                        null,
                        null,
                        null);
            default:
                return null;
        }
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            // get the column index for each data item
            int titleIndex = data.getColumnIndex(DatabaseDescription.Movie.COLUMN_TITLE);
            int yearIndex = data.getColumnIndex(DatabaseDescription.Movie.COLUMN_YEAR);
            int ratingIndex = data.getColumnIndex(DatabaseDescription.Movie.COLUMN_RATING);
            int lengthIndex = data.getColumnIndex(DatabaseDescription.Movie.COLUMN_LENGTH);
            int directorIndex = data.getColumnIndex(DatabaseDescription.Movie.COLUMN_DIRECTOR);
            int scoreIndex = data.getColumnIndex(DatabaseDescription.Movie.COLUMN_SCORE);

            // fill EditTexts with the retrieved data
            titleTextInputLayout.getEditText().setText(
                    data.getString(titleIndex));
            yearTextInputLayout.getEditText().setText(
                    data.getString(yearIndex));
            ratingTextInputLayout.getEditText().setText(
                    data.getString(ratingIndex));
            lengthTextInputLayout.getEditText().setText(
                    data.getString(lengthIndex));
            directorTextInputLayout.getEditText().setText(
                    data.getString(directorIndex));
            scoreTextInputLayout.getEditText().setText(
                    data.getString(scoreIndex));

            updateSaveButtonFAB();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}
