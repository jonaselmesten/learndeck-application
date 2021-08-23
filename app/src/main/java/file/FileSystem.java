package file;

import android.content.Context;
import android.util.Log;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.options.StorageDownloadFileOptions;
import exceptions.ResourceException;

import java.io.File;

public class FileSystem {

    private static String EXT_CACHE_DIR;
    private static String CACHE_DIR;

    public static void setDirectories(Context context) {
        EXT_CACHE_DIR = context.getExternalCacheDir().getPath();
        CACHE_DIR = context.getCacheDir().getPath();
    }

    private FileSystem() {}

    /**
     * Retrieves a resource from dir if its already downloaded, or else  it downloads
     * it and then saved it in the folder for later use.
     * @param fileName Key of object to fetch from AWS or cache.
     * @param directory Path where the resource will be saved/searched for.
     * @return Path to the file in disk.
     * @throws ResourceException If the resource can't be retrieved.
     */
    public static File getResource(String fileName, Directory directory) throws ResourceException {

        File path = new File(  getDirPath(directory) + "/" + fileName);
        Log.i("Resource", "Getting resource: " + fileName);


        if(path.exists()) {
            Log.i("Resource", "Found in disk");
            return path;

        } else {
            Log.i("Resource", "Resource not in disk, starting download from cloud...");
            Amplify.Storage.downloadFile(
                    fileName,
                    new File(path.getAbsolutePath()),
                    StorageDownloadFileOptions.defaultInstance(),
                    result -> Log.i("Resource", "Successfully downloaded and saved to cache"),
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

    private static String getDirPath(Directory directory) {
        switch (directory) {
            case EXT_CACHE_DIR:
                return EXT_CACHE_DIR;
            case CACHE_DIR:
                return CACHE_DIR;
        }
        return "";
    }

}
