package database;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;

/**<h1>Query result</h1>
 *
 * This class contains nested static classes that can hold different data returned from some of the methods in the class "DatabaseUtil".<br/>
 * <br />
 * Classes that can be instantiated:<br/>
 * TeacherCourse<br/>
 * StudentInfo<br/>
 * StudentCourseInfo<br/>
 * CardReview<br/>
 * CourseStats<br/>
 * @author Jonas Elmesten
 */
public class QueryResult {

    private QueryResult() {};

    public static class TeacherCourse {

        private final String COURSE_NAME;
        private final int COURSE_ID;

        public TeacherCourse(String courseName, int courseId) {
            COURSE_NAME = courseName;
            COURSE_ID = courseId;
        }

        public String getCourseName() {
            return COURSE_NAME;
        }

        public int getCourseId() {
            return COURSE_ID;
        }
    }

    public static class StudentInfo {

        private final int STUDENT_ID;
        private final String FIRST_NAME;
        private final String LAST_NAME;
        private final Date birthDate;

        public StudentInfo(int studentId, String firstName, String lastName, Date birthDate) {
            this.STUDENT_ID = studentId;
            this.FIRST_NAME = firstName;
            this.LAST_NAME = lastName;
            this.birthDate = birthDate;
        }

        public Date getBirthDate() {
            return birthDate;
        }

        public String getLastName() {
            return LAST_NAME;
        }

        public String getFirstName() {
            return FIRST_NAME;
        }

        public int getStudentId() {
            return STUDENT_ID;
        }
    }

    public static class StudentCourseInfo {

        private final String COURSE_NAME;
        private final int TEACHER_ID;
        private final int TOTAL_CARDS;
        private final int DUE_CARDS;
        private final int NEW_CARDS;
        private final Instant modificationDate;

        public StudentCourseInfo(String courseName, int teacherId, int totalCards, int dueCards, int newCards, Timestamp modificationDate) {
            COURSE_NAME = courseName;
            TEACHER_ID = teacherId;
            TOTAL_CARDS = totalCards;
            DUE_CARDS = dueCards;
            NEW_CARDS = newCards;
            System.out.println(modificationDate);
            this.modificationDate = modificationDate.toInstant();
        }

        public String getCourseName() {
            return COURSE_NAME;
        }

        public int getTotalCards() {
            return TOTAL_CARDS;
        }

        public int getDueCards() {
            return DUE_CARDS;
        }

        public int getNewCards() {
            return NEW_CARDS;
        }

        public int getTeacherId() {
            return TEACHER_ID;
        }

        public Instant getModificationDate() {
            return modificationDate;
        }
    }

    public static class CardReview {

        private final int CARD_ID;
        private final int COURSE_ID;
        private final int STUDENT_ID;
        private final String NEXT_REVIEW;
        private final int[] buttonValues;

        public CardReview(int cardId, int courseId, int studentId, String nextReview, int[] buttonValues) {
            CARD_ID = cardId;
            COURSE_ID = courseId;
            STUDENT_ID = studentId;
            NEXT_REVIEW = nextReview;
            this.buttonValues = buttonValues;
        }

        public int[] getReviewStats() {
            return Arrays.copyOf(buttonValues, 4);
        }

        public String getNextReview() {
            return NEXT_REVIEW;
        }

        public int getStudentId() {
            return STUDENT_ID;
        }

        public int getCourseId() {
            return COURSE_ID;
        }

        public int getCardId() {
            return CARD_ID;
        }
    }

    public static class CourseStats {

        private final int[] cardButtonStat;
        private final double[] difficultyStat;

        public CourseStats(int[] cardButtonStat, double[] difficultyStat) {
            this.cardButtonStat = cardButtonStat;
            this.difficultyStat = difficultyStat;
        }

        public int getHardCount() {
            return cardButtonStat[0];
        }

        public int getMediumCount() {
            return cardButtonStat[1];
        }

        public int getEasyCount() {
            return cardButtonStat[2];
        }

        public int getVeryEasyCount() {
            return cardButtonStat[3];
        }

        public double getAvgDifficulty() {
            return difficultyStat[0];
        }

        public double getEasiestDifficulty() {
            return difficultyStat[1];
        }

        public double getHardestDifficulty() {
            return difficultyStat[2];
        }

        public int getCardViewCount() {
            return cardButtonStat[0] + cardButtonStat[1] + cardButtonStat[2] + cardButtonStat[3];
        }
    }

}
