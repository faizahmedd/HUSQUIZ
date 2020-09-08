package edu.tacoma.uw.adibk.husquiz.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Course implements Serializable
{

    private String mCourseID;
    private String mCourseName;
    private List<Quiz> mQuizzes;

    private static final String COURSE_ID = "courseid";
    private static final String COURSE_NAME = "coursename";

    public Course(String ID, String CourseName)
    {
        this.mCourseID = ID;
        this.mCourseName = CourseName;
        this.mQuizzes = new ArrayList<>();
    }

    public static List<Course> parseCourseJson(String courseJson) throws JSONException
    {
        List<Course> courseList = new ArrayList<>();
        if (courseJson != null)
        {
            JSONArray arr = new JSONArray(courseJson);
            for (int i = 0; i < arr.length(); i++)
            {
                JSONObject obj = arr.getJSONObject(i);
                Course course = new Course(obj.getString(Course.getCourseId()), obj.getString(Course.getCourseName()));
                courseList.add(course);
            }
        }
        return courseList;
    }

    public String getmCourseID()
    {
        return mCourseID;
    }

    public void setmCourseID(String mCourseID)
    {
        this.mCourseID = mCourseID;
    }

    public String getmCourseName()
    {
        return mCourseName;
    }

    public void setmCourseName(String mCourseName)
    {
        this.mCourseName = mCourseName;
    }

    public static String getCourseId()
    {
        return COURSE_ID;
    }

    public static String getCourseName()
    {
        return COURSE_NAME;
    }
}
