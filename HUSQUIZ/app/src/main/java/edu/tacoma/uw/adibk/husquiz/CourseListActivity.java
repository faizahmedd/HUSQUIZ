/**
 * This class is the activity that will hold the lists of courses in our App.
 */
package edu.tacoma.uw.adibk.husquiz;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import edu.tacoma.uw.adibk.husquiz.model.Course;

public class CourseListActivity extends AppCompatActivity
{
    /** A list of courses */
    private List<Course> mCourseList;
    /** A recycler view */
    private RecyclerView mRecyclerView;

    /**
     * This method overrides the onCreate & sets the title as well as set the view
     * @param savedInstanceState the default bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        setTitle("Courses");

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
                startActivity(new Intent(CourseListActivity.this, LoginActivity.class).putExtra(LoginActivity.SIGNED_OUT, true));
            }
        });

        mRecyclerView = findViewById(R.id.list);
        assert mRecyclerView != null;

    }

    /**
     * This method overwrites onResume & connects the courseList to the backend server
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (mCourseList == null) {
                new CoursesTask().execute(getString(R.string.get_courses));
            }
        }
        else {
            Toast.makeText(this,
                    "No network connection available. Displaying locally stored data",
                    Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * This method sets up the recycler view with the course list
     * @param recyclerView the view that will be used
     */
    private void setupRecyclerView(@NonNull RecyclerView recyclerView)
    {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, mCourseList));
    }

    /**
     * This inner class will display the courses
     */
    public static class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>
    {
        /** The parent activity for the courses */
        private final CourseListActivity mParentActivity;
        /** Holds a list of values of the courses */
        private final List<Course> mValues;
        /** A onclick listener */
        private final View.OnClickListener mOnClickListener = new View.OnClickListener()
        {
            /**
             * This method sets the onClick to another class
             * @param view default view
             */
            @Override
            public void onClick(View view)
            {
                Course item = (Course) view.getTag();
                Context context = view.getContext();
                Intent intent = new Intent(context, QuizListActivity.class);
                intent.putExtra(FlashcardFragment.ARG_PARAM, item);
                context.startActivity(intent);

            }
        };
        /** Constructor */
        SimpleItemRecyclerViewAdapter(CourseListActivity parent,
                                      List<Course> items)
        {
            mValues = items;
            mParentActivity = parent;
        }

        /**
         * This class returns the new viewholder
         * @param parent default parameter
         * @param viewType default parameter
         * @return the new viewHolder
         */
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.course_list_content, parent, false);
            return new ViewHolder(view);
        }

        /**
         * This method sets the texts to the current courseID
         * @param holder default view
         * @param position default int
         */
        @Override
        public void onBindViewHolder(final ViewHolder holder, int position)
        {
            holder.mIdView.setText(mValues.get(position).getmCourseID());
            holder.mContentView.setText(mValues.get(position).getmCourseName());

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        /**
         * This method returns the number of courses
         * @return number of courses
         */
        @Override
        public int getItemCount()
        {
            return mValues.size();
        }
        /** Another inner class that hold the view for the courses */
        class ViewHolder extends RecyclerView.ViewHolder
        {
            /** A text view for the course ID */
            final TextView mIdView;
            /** A text view for the course content */
            final TextView mContentView;
            /** Constructor */
            ViewHolder(View view)
            {
                super(view);
                mIdView = (TextView) view.findViewById(R.id.id_text);
                mContentView = (TextView) view.findViewById(R.id.content);
            }
        }
    }

    /**
     * This class handles the backend of storing the courses.
     */
    private class CoursesTask extends AsyncTask<String, Void, String>
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
         * This method handles what happens when the course list is returned or not
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
                    mCourseList = Course.parseCourseJson(
                            jsonObject.getString("courses"));

                    if (!mCourseList.isEmpty())
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