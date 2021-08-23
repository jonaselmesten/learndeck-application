package card;

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.TextView;
import com.example.learndeck.R;

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
            TextView textView = new TextView(new ContextThemeWrapper(context, R.style.TextPart));
            textView.setText(text);

            view = textView;
        }


        return view;
    }
}
