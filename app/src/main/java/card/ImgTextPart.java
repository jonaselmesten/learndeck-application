package card;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

class ImgTextPart implements CardPart {

    private final String text;

    public ImgTextPart(String json) {
        this.text = json;
    }

    @Override
    public View convertToView(Context context) {
        TextView textView = new TextView(context);
        textView.setText(text);
        return textView;
    }
}
