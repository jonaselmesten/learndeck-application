package server;

import deck.Deck;
import deck.DeckUtil;
import deck.IOStatus;
import menu.user.User;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.time.Instant;
import java.util.*;


/**<h1>ServerConnection</h1>
 * Class that handles all the FTP-server actions from users.
 * Used to upload & download files, create directories in the server etc.
 * @author Jonas Elmesten
 */
public class ServerConnection implements AutoCloseable {

    private final static Logger logger = LogManager.getLogger(ServerConnection.class);

    private final static int PORT_NUMBER = 0;
    private final static String SERVER_IP = ""; // TO DO: Can it be stored/fetched in another way?

    private final static String STUDENT_LOG_IN = "learndeckstudent";
    private final static String TEACHER_LOG_IN = "learndeckteacher";
    private final static String PASSWORD = ""; // TO DO: Can it be stored/fetched in another way?

    private final List<Deck> coursesToUpload = new ArrayList<>();
    private final List<String> coursesToRemove = new ArrayList<>();
    private final FTPClient ftpClient;
    private final User user;

    private Map<String, Instant> fileInstant;
    private InputStream inputStream;
    private OutputStream outputStream;
    private String userFolderName;

    public ServerConnection(User user) throws IOException {

        this.user = user;

        ftpClient = new FTPClient();
        ftpClient.connect(SERVER_IP,PORT_NUMBER);

        int replyCode = ftpClient.getReplyCode();

        if(!FTPReply.isPositiveCompletion(replyCode)) {
            logger.info("Server error - Code:" + replyCode);
            return;
        }else
            userLogIn();

        showServerReply(ftpClient);
    }


    /**
     * Log in to the server and change to the correct directory.
     * @throws IOException When log in error.
     */
    private void userLogIn() throws IOException {

        switch(user.getUserType()) {

            case STUDENT: //Student log in.

                if(ftpClient.login(STUDENT_LOG_IN, ""))
                    ftpClient.changeWorkingDirectory(Directories.STUDENT.folderName);
                break;

            case TEACHER: //Teacher log in.

                if(ftpClient.login(TEACHER_LOG_IN, PASSWORD))
                    ftpClient.changeWorkingDirectory(Directories.TEACHER.folderName);
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + user.toString());
        }

        userFolderName = user.getUserType().name() + "_" + user.getUserId();
    }



    /**
     * Add a course to be uploaded when uploadCourses() is called.
     * @param deck Course to add.
     */
    public void addCourseToUpload(Deck deck) {
        coursesToUpload.add(deck);
    }


    /**
     * Get the modification date as an instant object of a file.
     * @param courseName Name of the file.
     * @return Instant of the file.
     * @throws IOException
     */
    private Instant getFileInstant(String courseName) throws IOException {

        //Get all the files at once when called first time.
        if(Objects.isNull(fileInstant)) {

            fileInstant = new HashMap<>();
            ftpClient.enterLocalPassiveMode();

            for(FTPFile file : ftpClient.listFiles())
                fileInstant.put(file.getName(), file.getTimestamp().toInstant());

            return fileInstant.get(courseName);
        }else
            return fileInstant.get(courseName);
    }


    /**
     * IMPORTANT: You need to add courses to upload with: addCourseToUpload() before you call this method.
     * Upload all the courses added.
     * @throws IOException
     */
    public void uploadCourses()   { //TO DO: Check modification times(?), Decide on upload method.

        //Change to the right directory and create users own sub-directory if necessary.
        try {
            changeDirectoryToUser(user);

            if(coursesToUpload.isEmpty())
                return;

            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

        }catch(IOException e) {
            logger.error("Error while trying to change or create directory for user:" + user.toString(), e);
            return;
        }

        for(Deck course : coursesToUpload) {

            //Just try to upload updated/new courses.
            if(course.getIoStatus().equals(IOStatus.UNCHANGED))
                continue;

            try{
                inputStream = new FileInputStream(DeckUtil.getCoursePath(course.getCourseName()).toString());
                outputStream = ftpClient.storeFileStream(course.getCourseName() + ".txt");

                byte[] bytesIn = new byte[4096];
                int bytesRead = 0;

                while((bytesRead = inputStream.read(bytesIn)) != -1)
                    outputStream.write(bytesIn, 0, bytesRead);

            }catch(IOException e) {
                logger.error("Error occured while trying to upload course to server. Course:" + course.getCourseId() + " ID:" + course.getCourseName(), e);
            }

            showServerReply(ftpClient);
        }
    }


    /**
     * Change directory to the current users'.
     * Create a new one if necessary.
     * @param user Current user.
     * @throws IOException When directory could not me created.
     */
    private void changeDirectoryToUser(User user) throws IOException { //TO DO: Add STUDENT action.

        //Try to change directory, create a new one for the user if necessary.
        switch(user.getUserType()) {

            case STUDENT:
                break;

            case TEACHER:

                if(!ftpClient.changeWorkingDirectory(userFolderName)) {

                    logger.info("Server error - Code:" + ftpClient.getReplyCode() + ftpClient.getReplyString());

                    if(ftpClient.getReplyCode() == 550) { //No such directory exists.
                        if(!ftpClient.makeDirectory(userFolderName))
                            throw new IOException("Could not create directory for user:" + user.toString());
                    }else
                        throw new IOException(ftpClient.getReplyString());
                }
                break;
        }
    }


    /** Log all the server replies.*/
    private static void showServerReply(FTPClient ftpClient) {

        String[] serverReplies = ftpClient.getReplyStrings();

        if(!Objects.isNull(serverReplies) && serverReplies.length > 0) {

            for (String aReply : serverReplies) {
                logger.info("SERVER: " + aReply);
            }
        }
    }

    @Override
    public void close() {

        try {
            inputStream.close();

            if (ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addCourseToBeRemoved(String courseName) {
        coursesToRemove.add(courseName);
    }

    public void removeFiles() throws IOException {

        if(coursesToRemove.isEmpty())
            return;

        for(String courseName :  coursesToRemove) {

            if(!ftpClient.deleteFile(courseName))
                logger.debug("Could not delete file:" + courseName);
        }
    }
}
