package edu.tacoma.uw.adibk.husquiz.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Quiz implements Serializable
{
    private String mQuizID;
    private String mQuizTopic;
    private String mCourseID;

    private static final String QUIZ_ID = "quizid";
    private static final String QUIZ_TOPIC = "quiztopic";
    private static final String COURSE_ID = "courseid";

    private ArrayList<Flashcard> mFlashcards;

    public Quiz(String quizID, String quizTopic, String courseID)
    {
        this.mQuizID = quizID;
        this.mQuizTopic = quizTopic;
        this.mCourseID = courseID;
        mFlashcards = new ArrayList<>();
    }

    public static List<Quiz> parseQuizJson(String quizJson, String courseID) throws JSONException
    {
        List<Quiz> quizList = new ArrayList<>();
        if (quizJson != null)
        {
            JSONArray arr = new JSONArray(quizJson);
            for (int i = 0; i < arr.length(); i++)
            {
                JSONObject obj = arr.getJSONObject(i);
                Quiz quiz = new Quiz(obj.getString(Quiz.getQuizID()), obj.getString(Quiz.getQuizTopic()), obj.getString(Quiz.getCourseID()));
                if (quiz.mCourseID.equals(courseID))
                    quizList.add(quiz);
            }
        }
        return quizList;
    }


    public static String getQuizId()
    {
        return QUIZ_ID;
    }

    public ArrayList<Flashcard> getmFlashcards()
    {
        return mFlashcards;
    }

    public void setmFlashcards(ArrayList<Flashcard> mFlashcards)
    {
        this.mFlashcards = mFlashcards;
    }

    public String getmQuizTopic()
    {
        return mQuizTopic;
    }

    public void setmQuizTopic(String mQuizTopic)
    {
        this.mQuizTopic = mQuizTopic;
    }

    public static String getQuizTopic()
    {
        return QUIZ_TOPIC;
    }

    public String getmQuizID()
    {
        return mQuizID;
    }

    public void setmQuizID(String mQuizID)
    {
        this.mQuizID = mQuizID;
    }

    public String getmCourseID()
    {
        return mCourseID;
    }

    public void setmCourseID(String mCourseID)
    {
        this.mCourseID = mCourseID;
    }

    public static String getQuizID()
    {
        return QUIZ_ID;
    }

    public static String getCourseID()
    {
        return COURSE_ID;
    }
}
