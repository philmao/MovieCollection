// DetailFragment.java
// Fragment subclass that displays one movie's detail
package com.example.moviescollection;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.moviescollection.data.DatabaseDescription.Movie;

public class DetailFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    public interface DetailFragmentListener {

        void onMovieDeleted();

        void onEditMovie(Uri movieUri);
    }

    private static final int MOVIE_LOADER = 0;

    private DetailFragmentListener listener;
    private Uri movieUri;

    private TextView titleTextView;
    private TextView yearTextView;
    private TextView ratingTextView;
    private TextView lengthTextView;
    private TextView directorTextView;
    private TextView scoreTextView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (DetailFragmentListener) context;
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

        Bundle arguments = getArguments();

        if(arguments != null)
            movieUri = arguments.getParcelable(MainActivity.MOVIE_URI);

        View view =
                inflater.inflate(R.layout.fragment_detail, container, false);

        titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        yearTextView = (TextView) view.findViewById(R.id.yearTextView);
        ratingTextView = (TextView) view.findViewById(R.id.ratingTextView);
        lengthTextView = (TextView) view.findViewById(R.id.lengthTextView);
        directorTextView = (TextView) view.findViewById(R.id.directorTextView);
        scoreTextView = (TextView) view.findViewById(R.id.scoreTextView);

        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_details_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                listener.onEditMovie(movieUri);
                return true;
            case R.id.action_delete:
                deleteMovie();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteMovie() {
        confirmDelete.show(getFragmentManager(), "confirm delete");
    }

    private final DialogFragment confirmDelete =
            new DialogFragment() {
                @Override
                public Dialog onCreateDialog(Bundle bundle) {
                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(getActivity());

                    builder.setTitle(R.string.confirm_title);
                    builder.setMessage(R.string.confirm_message);

                    builder.setPositiveButton(R.string.button_delete,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(
                                        DialogInterface dialog, int button) {
                                    getActivity().getContentResolver().delete(
                                            movieUri, null, null);
                                    listener.onMovieDeleted();
                                }
                            }
                    );

                    builder.setNegativeButton(R.string.button_cancel, null);
                    return builder.create();
                }
            };

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader;
        switch (id) {
            case MOVIE_LOADER:
                cursorLoader = new CursorLoader(getActivity(),
                    movieUri,
                    null,
                    null,
                    null,
                    null);
                break;
            default:
                cursorLoader = null;
                break;
        }

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            // get the column index for each data item
            int titleIndex = data.getColumnIndex(Movie.COLUMN_TITLE);
            int yearIndex = data.getColumnIndex(Movie.COLUMN_YEAR);
            int ratingIndex = data.getColumnIndex(Movie.COLUMN_RATING);
            int lengthIndex = data.getColumnIndex(Movie.COLUMN_LENGTH);
            int directorIndex = data.getColumnIndex(Movie.COLUMN_DIRECTOR);
            int scoreIndex = data.getColumnIndex(Movie.COLUMN_SCORE);

            // fill TextViews with the retrieved data
            titleTextView.setText(data.getString(titleIndex));
            yearTextView.setText(data.getString(yearIndex));
            ratingTextView.setText(data.getString(ratingIndex));
            lengthTextView.setText(data.getString(lengthIndex));
            directorTextView.setText(data.getString(directorIndex));
            scoreTextView.setText(data.getString(scoreIndex));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}
