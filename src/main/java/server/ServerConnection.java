package server;

import deck.Deck;
import deck.DeckUtil;
import deck.IOStatus;
import menu.user.User;
import menu.user.UserType;
import org.apache.commons.net.ftp.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**<h1>ServerConnection</h1>
 * Class that handles all the FTP-server actions from users.
 * Used to upload & download files, create directories in the server etc.
 * @author Jonas Elmesten
 */
public class ServerConnection implements AutoCloseable {

    private final static Logger logger = LogManager.getLogger(ServerConnection.class);

    private final static int PORT_NUMBER = 65500;
    private final static String SERVER_IP = "192.168.0.126"; // TO DO: Can it be stored/fetched in another way?

    private final static String STUDENT_LOG_IN = "learndeckstudent";
    private final static String TEACHER_LOG_IN = "learndeckteacher";
    private final static String PASSWORD = "8yu4n6z8456!"; // TO DO: Can it be stored/fetched in another way?

    private final FTPClient ftpClient;
    private final User user;

    private Map<String, Instant> fileInstant;
    private String userFolderName;

    /**
     * Establishes a connection and logs in the user.
     * @param user Current user.
     * @throws IOException Occurs when a connection couldn't be established or if the user log in failed - Either way this renders the created object useless.
     */
    public ServerConnection(User user) throws IOException {

        this.user = user;

        ftpClient = new FTPClient();
        ftpClient.connect(SERVER_IP,PORT_NUMBER);

        logServerReply();
        DeckUtil.createDefaultDirectory();

        if(FTPReply.isPositiveCompletion(ftpClient.getReplyCode()))
            userLogIn();
        else {

            logger.info("A connection could not be established to the server.");

            close();
            throw new FTPConnectionClosedException("A connection could not be established.");
        }
    }

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

        userFolderName = user.getUserType().name().concat("_") + user.getUserId();
    }

    //Get the modification date as an instant object of a file.
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

    //TO DO: Check file modification date.
    //TO DO: Add last update to database.
    public void downloadCourseFile(String courseName, UserType userType, String directory) throws IOException {

        String pathToFileFTP = directory.concat("\\").concat(courseName).concat(".txt");
        File file = new File(DeckUtil.getDeckPath() + "\\".concat(courseName).concat(".txt"));

        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

        changeDirectoryTo(userType);

        //Will write over already saved files on PC.
        try(OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {

            boolean success = ftpClient.retrieveFile(pathToFileFTP, outputStream);

            if(success)
                logger.info(user.getUserType() + " - " + user.getUserId() + " downloaded " + courseName + " to PC.");
            else
                logger.debug(user.getUserType() + " - " + user.getUserId() + " couldn't download " + courseName + " to PC.");

            logServerReply();
        }
    }

    /**
     * Upload a course to the FTP-server.
     * @throws IOException When error occurred while trying to upload file.
     */
    public void uploadCourseFile(Deck course) throws IOException { //TO DO: Check modification times(?), Decide on upload method.

        changeDirectoryToUser();

        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

        //Just try to upload updated/new courses.
        if(course.getIoStatus().equals(IOStatus.UNCHANGED))
            return;

        try(InputStream inputStream = new FileInputStream(DeckUtil.getCoursePath(course.getCourseName()).toString())) {

            boolean success  = ftpClient.storeFile(course.getCourseName().concat(".txt"), inputStream);

            if(success)
                logger.info(user.getUserType() + " - " + user.getUserId() + " uploaded " + course.getCourseName() + " to server.");
            else
                logger.debug(user.getUserType() + " - " + user.getUserId() + " couldn't upload " + course.getCourseName() + " to server.");

            logServerReply();
        }
    }


    /**
     * Remove all courses that has been added with addCourseToBeRemoved();
     * @throws IOException When error occurred while trying to remove courses.
     */
    public void removeCourseFile(Deck course) throws IOException {

        if(course.getIoStatus().equals(IOStatus.NEW))
            return;

        changeDirectoryToUser();

        if(!ftpClient.deleteFile(course.getCourseName().concat(".txt")))
            logger.error("Could not delete file:" + course.getCourseName());

        logServerReply();
    }


     //Change directory to the current users'.
    private void changeDirectoryToUser() throws IOException {

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

    //Goes back to root - Can then chose either Student or Teacher main folder.
    private void changeDirectoryTo(UserType userType) throws IOException {

        ftpClient.changeToParentDirectory();

        switch(userType) {

            case STUDENT:
                ftpClient.changeWorkingDirectory(Directories.STUDENT.folderName);
                break;
            case TEACHER:
                ftpClient.changeWorkingDirectory(Directories.TEACHER.folderName);
        }
    }

    private void logServerReply() {

        String[] serverReplies = ftpClient.getReplyStrings();

        if(!Objects.isNull(serverReplies) && serverReplies.length > 0) {

            for (String reply : serverReplies) {
                logger.info("Server reply: " + reply);
            }
        }
    }

    @Override
    public void close() {

        try {
            if (ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
        } catch (IOException e) {
            logger.error("Could not log out/disconnect  form server.", e);
        }
    }

}
