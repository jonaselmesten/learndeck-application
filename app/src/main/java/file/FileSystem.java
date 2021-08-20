package file;

import android.app.Activity;
import android.util.Log;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.options.StorageDownloadFileOptions;
import exceptions.ResourceException;

import java.io.File;
import java.lang.ref.WeakReference;

public class FileSystem {

    private static WeakReference<Activity> activityReference;

    private FileSystem() {}

    public static void updateActivity(Activity activity) {
        activityReference = new WeakReference<Activity>(activity);
    }

    /**
     * Retrieves a resource from dir if its already downloaded, or else  it downloads
     * it and then saved it in the folder for later use.
     * @param fileName Key of object to fetch from AWS or cache.
     * @param dirPath Path where the resource will be saved/searched for.
     * @return Path to the file in disk.
     * @throws ResourceException If the resource can't be retrieved.
     */
    public static File getResource(String fileName, String dirPath) throws ResourceException {

        File path = new File(  dirPath + "/" + fileName);

        if(path.exists()) {
            Log.i("Resource", "Resource already saved in cache: " + fileName);
            return path;

        } else {
            Amplify.Storage.downloadFile(
                    fileName,
                    new File(path.getAbsolutePath()),
                    StorageDownloadFileOptions.defaultInstance(),
                    result -> Log.i("Resource", "Successfully downloaded to cache: " + result.getFile().getName()),
                    error -> Log.e("Resource",  "Download Failure", error)
            );
        }

        //Failed to get/download resource.
        if(!path.exists())
            throw new ResourceException("Could not get resource: " + fileName);

        return path;
    }

    /**
     * Deletes a file from disk.
     * @param fileName File to be deleted.
     * @param dirPath Directory path.
     * @return True if successful.
     * @throws ResourceException If the file can't be found and deleted.
     */
    public static boolean deleteResourceFromDisk(String fileName, String dirPath) throws ResourceException {

        File path = new File(  dirPath + "/" + fileName);

        if(path.exists())
            return path.delete();
        else
            throw new ResourceException("Resouce not found in directory: " + dirPath);
    }

}
