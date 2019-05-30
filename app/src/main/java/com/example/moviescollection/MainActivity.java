package com.example.moviescollection;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

public class MainActivity extends AppCompatActivity
        implements MoviesFragment.MoviesFragmentListener,
        DetailFragment.DetailFragmentListener,
        AddEditFragment.AddEditFragmentListener {

    public static final String MOVIE_URI = "movie_uri";

    private MoviesFragment moviesFragment;  // display movie list

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null &&
                findViewById(R.id.fragmentContainer) != null) {
            // create MoviesFragment
            moviesFragment = new MoviesFragment();

            // add the fragment to the FrameLayout
            FragmentTransaction transaction =
                    getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragmentContainer, moviesFragment);
            transaction.commit();
        }
        else {
            moviesFragment =
                    (MoviesFragment) getSupportFragmentManager().
                            findFragmentById(R.id.moviesFragment);
        }
    }

    // display DetailFragment for selected movie
    @Override
    public void onMovieSelected(Uri movieUri) {
        if (findViewById(R.id.fragmentContainer) != null) // phone
            displayMovie((movieUri), R.id.fragmentContainer);
        else { // tablet
            // removes top of back stack
            getSupportFragmentManager().popBackStack();

            displayMovie(movieUri, R.id.rightPaneContainer);
        }
    }

    // display AddEditFragment to add a new movie
    @Override
    public void onAddMovie() {
        if (findViewById(R.id.fragmentContainer) != null) // phone
            displayAddEditFragment(R.id.fragmentContainer, null);
        else // tablet
            displayAddEditFragment(R.id.rightPaneContainer, null);
    }

    // display a movie
    private void displayMovie(Uri movieUri, int viewID) {
        DetailFragment detailFragment = new DetailFragment();

        // specify movie's Uri as an argument to the DetailFragment
        Bundle arguments = new Bundle();
        arguments.putParcelable(MOVIE_URI, movieUri);
        detailFragment.setArguments(arguments);

        // use a FragmentTransaction to display the DetailFragment
        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
        transaction.replace(viewID, detailFragment);
        transaction.addToBackStack(null);
        transaction.commit();  // causes DetailFragment to display
    }

    private void displayAddEditFragment(int viewID, Uri movieUri) {
        AddEditFragment addEditFragment = new AddEditFragment();

        //
        if (movieUri != null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(MOVIE_URI, movieUri);
            addEditFragment.setArguments(arguments);
        }

        //
        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
        transaction.replace(viewID, addEditFragment);
        transaction.addToBackStack(null);
        transaction.commit(); // causes AddEditFragment to display
    }

    //
    @Override
    public void onMovieDeleted() {
        //
        getSupportFragmentManager().popBackStack();
        moviesFragment.updateMovieList();
    }

    //
    @Override
    public void onEditMovie(Uri movieUri) {
        if (findViewById(R.id.fragmentContainer) != null) // phone
            displayAddEditFragment(R.id.fragmentContainer, movieUri);
        else // tablet
            displayAddEditFragment(R.id.rightPaneContainer, movieUri);
    }

    //
    @Override
    public void onAddEditCompleted(Uri movieUri) {
        //
        getSupportFragmentManager().popBackStack();
        moviesFragment.updateMovieList();

        if (findViewById(R.id.fragmentContainer) == null) {
            //
            getSupportFragmentManager().popBackStack();

            //
            displayMovie(movieUri, R.id.rightPaneContainer);
        }
    }
}
