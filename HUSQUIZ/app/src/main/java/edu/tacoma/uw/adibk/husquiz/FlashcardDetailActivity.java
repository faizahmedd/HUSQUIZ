package edu.tacoma.uw.adibk.husquiz;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import edu.tacoma.uw.adibk.husquiz.model.Flashcard;

import static edu.tacoma.uw.adibk.husquiz.FlashcardFragment.QUIZ;

public class FlashcardDetailActivity extends AppCompatActivity
{

    private Flashcard mFlashcard;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard_detail);
        mFlashcard = (Flashcard) getIntent().getSerializableExtra(FlashcardFragment.ARG_PARAM);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.signout);
        fab.setOnClickListener(new View.OnClickListener()
        {
            /**
             * This method sets the onClick to a snackbar
             * @param view default view
             */
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(FlashcardDetailActivity.this, LoginActivity.class).putExtra(LoginActivity.SIGNED_OUT, true));
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (savedInstanceState == null)
        {
            Bundle arguments = new Bundle();
            arguments.putSerializable(FlashcardFragment.ARG_PARAM, mFlashcard);
            FlashcardFragment flashcardFragment = new FlashcardFragment();
            flashcardFragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.flashcard_detail_container, flashcardFragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == android.R.id.home)
        {

            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}