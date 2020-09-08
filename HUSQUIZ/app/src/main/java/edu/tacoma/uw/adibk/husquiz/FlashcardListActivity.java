/**
 * This class creates the the activity for the flashcards.
 */
package edu.tacoma.uw.adibk.husquiz;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import edu.tacoma.uw.adibk.husquiz.model.Flashcard;
import edu.tacoma.uw.adibk.husquiz.model.Quiz;

public class FlashcardListActivity extends AppCompatActivity
{
    /** A list of Flashcard objects*/
    private List<Flashcard> mFlashcardList;
    /** A view that this class will use */
    private RecyclerView mRecyclerView;
    /** A string that will identify the quiz */
    private Quiz mQuiz;

    /**
     * This method overrides the onCreate & sets the title as well as set the view
     * @param savedInstanceState the default bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard_list);
//        // Back Button
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Flashcards");
        Intent intent = getIntent();
        mQuiz = (Quiz) intent.getSerializableExtra(FlashcardFragment.ARG_PARAM);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.signout);
        fab.setOnClickListener(new View.OnClickListener()
        {
            /**
             * This method sets the snackbar visual
             * @param view default view
             */
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(FlashcardListActivity.this, LoginActivity.class).putExtra(LoginActivity.SIGNED_OUT, true));
            }
        });


        mRecyclerView = findViewById(R.id.list);
        assert mRecyclerView != null;

    }
    /**
     * This method overwrites onResume & connects the flashcard to the backend server
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (mFlashcardList == null) {
                new FlashcardTask().execute(getString(R.string.get_flashcards));
            }
        }
        else {
            Toast.makeText(this,
                    "No network connection available. Displaying locally stored data",
                    Toast.LENGTH_SHORT).show();
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

    /**
     * Sets up the view for the class
     * @param recyclerView the Recycler view
     */
    private void setupRecyclerView(@NonNull RecyclerView recyclerView)
    {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, mFlashcardList));
    }
    /** This class will create the views & listeners for the Flashcard activity */
    public static class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>
    {
        /** The parent activity for the flashcard */
        private final FlashcardListActivity mParentActivity;
        /** Hold a list of flashcard objects */
        private final List<Flashcard> mValues;
        /** A listener */
        private final View.OnClickListener mOnClickListener = new View.OnClickListener()
        {
            /**
             * This method sets the intent for the quiz activity
             * @param view default view
             */
            @Override
            public void onClick(View view)
            {
                Flashcard item = (Flashcard) view.getTag();
                Context context = view.getContext();
                Intent intent = new Intent(context, FlashcardDetailActivity.class);
                intent.putExtra(FlashcardFragment.ARG_PARAM, item);
                intent.putExtra(FlashcardFragment.QUIZ, mParentActivity.mQuiz);
                context.startActivity(intent);
            }
        };

        SimpleItemRecyclerViewAdapter(FlashcardListActivity parent,
                                      List<Flashcard> items)
        {
            mValues = items;
            mParentActivity = parent;
        }

        /**
         * This class sets the fragment view
         * @param parent default viewgroup
         * @param viewType default int
         * @return the new view
         */
        @Override
        public SimpleItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.flashcard_list_content, parent, false);
            return new SimpleItemRecyclerViewAdapter.ViewHolder(view);
        }

        /**
         * This method sets the question up on the flashcard
         * @param holder defult viewHolder
         * @param position default int
         */
        @Override
        public void onBindViewHolder(final SimpleItemRecyclerViewAdapter.ViewHolder holder, int position)
        {
            holder.mIdView.setText("Question " + (position + 1));

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        /**
         * This method returns the # of questions
         * @return # of questions
         */
        @Override
        public int getItemCount()
        {
            return mValues.size();
        }

        /** Another inner class that sets the text views */
        class ViewHolder extends RecyclerView.ViewHolder
        {
            /** A text view for the flashcard id */
            final TextView mIdView;
            /** A text view for the flashcard content */
            final TextView mContentView;

            ViewHolder(View view)
            {
                super(view);
                mIdView = (TextView) view.findViewById(R.id.id_text);
                mContentView = (TextView) view.findViewById(R.id.content);
            }
        }
    }
    /** This class creates the Flashcard object */
    private class FlashcardTask extends AsyncTask<String, Void, String>
    {
        /**
         * This method downloads the course lists
         * @param urls deafult string
         * @return the course name
         */
        @Override
        protected String doInBackground(String... urls)
        {
            String response = "";
            HttpURLConnection urlConnection = null;
            for (String url : urls) {
                try {
                    URL urlObject = new URL(url);
                    urlConnection = (HttpURLConnection) urlObject.openConnection();

                    InputStream content = urlConnection.getInputStream();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    response = "Unable to download the list of courses, Reason: "
                            + e.getMessage();
                }
                finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;

        }
        /**
         * This method handles what happens when the flashcard is returned or not
         * @param s default string
         */
        @Override
        protected void onPostExecute(String s)
        {
            if (s.startsWith("Unable to"))
            {
                Toast.makeText(getApplicationContext(), "Unable to download" + s, Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            try
            {
                JSONObject jsonObject = new JSONObject(s);

                if (jsonObject.getBoolean("success"))
                {
                    mFlashcardList = Flashcard.parseFlashcardJson(
                            jsonObject.getString("flashcards"), FlashcardListActivity.this.mQuiz.getmQuizID());

                    if (!mFlashcardList.isEmpty())
                    {
                        setupRecyclerView((RecyclerView) mRecyclerView);
                    }
                }

            }
            catch (JSONException e)
            {
                Toast.makeText(getApplicationContext(), "JSON Error: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}