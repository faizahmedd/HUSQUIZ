package edu.tacoma.uw.adibk.husquiz;

import org.junit.Before;
import org.junit.Test;

import edu.tacoma.uw.adibk.husquiz.model.Course;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class CourseUnitTest
{
    private Course course;
    @Before
    public void initialize()
    {
        course = new Course("1", "Algorithms");
    }

    /**
     * Tests the Course constructor to make sure it is not null
     */
    @Test
    public void testCourseConstructor()
    {
        assertNotNull(course);
    }

    /**
     * Tests the get method for the ID
     */
    @Test
    public void testGetmCourseID()
    {
        assertEquals("1", course.getmCourseID());
    }

    /**
     * Tests the get Method for the Course Name
     */
    @Test
    public void testGetmCourseName()
    {
        assertEquals("Algorithms", course.getmCourseName());
    }
}