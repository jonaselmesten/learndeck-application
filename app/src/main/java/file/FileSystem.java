package file;

import activity.DeckActivity;
import android.util.Log;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.options.StorageDownloadFileOptions;
import exceptions.ResourceException;

import java.io.File;

public class FileSystem {

    private FileSystem() {}

    public File getResource(String fileName) throws ResourceException {

        File path = new File(  getApplicationContext().getExternalCacheDir().getPath() + "/" + fileName);

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

}
