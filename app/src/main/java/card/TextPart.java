package card;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.Objects;

class TextPart implements CardPart{

    private final String text;
    private View view;

    public TextPart(String json) {
        this.text = json;
    }

    @Override
    public View convertToView(Context context) {

        if(Objects.isNull(view)) {
            TextView textView = new TextView(context);
            textView.setText(text);

            view = textView;
        }

        return view;
    }
}
