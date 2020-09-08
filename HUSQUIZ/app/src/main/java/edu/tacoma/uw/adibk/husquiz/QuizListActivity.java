/** This class creates the activity that is opened after successfully loggin in */
package edu.tacoma.uw.adibk.husquiz;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
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
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import edu.tacoma.uw.adibk.husquiz.model.Course;
import edu.tacoma.uw.adibk.husquiz.model.Quiz;

public class QuizListActivity extends AppCompatActivity
{
    /** A string for the CourseID */
    private Course mCourse;
    /** A list that holds the quiz objects */
    private List<Quiz> mQuizList;
    /** The view for this class */
    private RecyclerView mRecyclerView;

    private JSONObject mQuizJSON;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_list);
        mQuizJSON = new JSONObject();
//        // Back Button
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Quizzes");
        Intent intent = getIntent();
        mCourse = (Course) intent.getSerializableExtra(FlashcardFragment.ARG_PARAM);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.signout);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(QuizListActivity.this, LoginActivity.class).putExtra(LoginActivity.SIGNED_OUT, true));
            }
        });


        mRecyclerView = findViewById(R.id.list);
        assert mRecyclerView != null;

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
        {
            if (mQuizList == null)
            {
                new QuizzesTask().execute(getString(R.string.get_quizzes));
            }
        }
        else
        {
            Toast.makeText(this,
                    "No network connection available. Displaying locally stored data",
                    Toast.LENGTH_SHORT).show();
        }

    }

    /** Sets up the view for the Quiz list */
    private void setupRecyclerView(@NonNull RecyclerView recyclerView)
    {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, mQuizList));
    }

    /** An inner class that creates the listeners & parameters for the quiz list */
    public static class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>
    {
        /** The parent activity for the quiz list */
        private final QuizListActivity mParentActivity;
        /** A list of quiz objects */
        private final List<Quiz> mValues;
        /** The listener for the onClick */
        private final View.OnClickListener mOnClickListener = new View.OnClickListener()
        {
            /**
             * This method sets the intent for the quiz activity
             * @param view default view
             */
            @Override
            public void onClick(View view)
            {
                Quiz item = (Quiz) view.getTag();
                Context context = view.getContext();
                Intent intent = new Intent(context, FlashcardListActivity.class);
                intent.putExtra(FlashcardFragment.ARG_PARAM, item);
                intent.putExtra(FlashcardFragment.COURSE, mParentActivity.mCourse);
                context.startActivity(intent);
            }
        };
        /** Constructor */
        SimpleItemRecyclerViewAdapter(QuizListActivity parent,
                                      List<Quiz> items)
        {
            mValues = items;
            mParentActivity = parent;
        }

        /**
         * This method sets the view
         * @param parent default viewgroup
         * @param viewType default int
         * @return the new view
         */
        @Override
        public SimpleItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.quiz_list_content, parent, false);
            return new SimpleItemRecyclerViewAdapter.ViewHolder(view);
        }

        /**
         * This method sets the text for the quiz characteristics
         * @param holder default viewholder
         * @param position default int
         */
        @Override
        public void onBindViewHolder(final SimpleItemRecyclerViewAdapter.ViewHolder holder, int position)
        {
            holder.mIdView.setText(mValues.get(position).getmQuizID());
            holder.mContentView.setText(mValues.get(position).getmQuizTopic());
            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        /**
         * Returns the # of quizzes
         * @return return # of quzzes
         */
        @Override
        public int getItemCount()
        {
            return mValues.size();
        }

        /**
         * This class sets the new view
         */
        class ViewHolder extends RecyclerView.ViewHolder
        {
            /** Textview widget for quiz id */
            final TextView mIdView;
            /** Textview widget for content id */
            final TextView mContentView;

            ViewHolder(View view)
            {
                super(view);
                mIdView = (TextView) view.findViewById(R.id.id_text);
                mContentView = (TextView) view.findViewById(R.id.content);
            }
        }
    }
    /** An inner class that handles the backend of storing the quiz data */
    private class QuizzesTask extends AsyncTask<String, Void, String>
    {
        /**
         * This method downloads the course lists
         * @param urls default string
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
         * This method handles what happens when the quiz is returned or not
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
                    mQuizList = Quiz.parseQuizJson(
                            jsonObject.getString("quizzes"), mCourse.getmCourseID());

                    if (!mQuizList.isEmpty())
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