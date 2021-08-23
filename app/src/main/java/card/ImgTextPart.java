package card;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import exceptions.ResourceException;
import file.Directory;
import file.FileSystem;

import java.io.File;
import java.util.Objects;

class ImgTextPart implements CardPart {

    private String text;
    private File img;
    private View view;

    public ImgTextPart(String json) throws ResourceException {

        JsonElement jsonTree = JsonParser.parseString(json);

        if(jsonTree.isJsonObject()){
            JsonObject jsonObject = jsonTree.getAsJsonObject();
            JsonElement text = jsonObject.get("text");
            JsonElement img = jsonObject.get("img");

            this.text = text.getAsString();
            this.img = FileSystem.getResource(img.getAsString(), Directory.EXT_CACHE_DIR);
        }
    }

    @Override
    public View convertToView(Context context) {

        if(Objects.isNull(view)) {
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

            ImageView imageView = new ImageView(context);
            Bitmap bitMap = BitmapFactory.decodeFile(img.getAbsolutePath());
            imageView.setImageBitmap(bitMap);

            TextView textView = new TextView(context);
            textView.setText(text);

            layout.addView(imageView);
            layout.addView(textView);

            this.view = layout;
        }

        return view;
    }
}
