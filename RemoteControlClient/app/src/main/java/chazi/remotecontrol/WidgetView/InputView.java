package chazi.remotecontrol.WidgetView;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import chazi.remotecontrol.entity.Widget;
import chazi.remotecontrol.utils.Connect;
import chazi.remotecontrol.utils.ContentCreator;
import chazi.remotecontrol.utils.DensityUtil;

/**
 * Created by 595056078 on 2017/4/30.
 */

public class InputView extends WidgetView {

    private EditText input;
    private Button btn_send;

    public InputView(Context context, Widget widget) {
        super(context, widget);

        input = new EditText(context);
        btn_send = new Button(context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        LayoutParams lp_input = new LayoutParams(widget.getWidthInPx(context), widget.getHeightInPx(context));
        lp_input.setMargins(0, 0, DensityUtil.dip2px(context, 50), 0);
        input.setLayoutParams(lp_input);


        LayoutParams lp_send = new LayoutParams(DensityUtil.dip2px(context, 50), widget.getHeightInPx(context));
        lp_send.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        btn_send.setLayoutParams(lp_send);

        if (!isEdit()) {
            input.addTextChangedListener(watcher);
            btn_send.setOnClickListener(listener);
        }

        addView(input);
        addView(btn_send);
    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.length() > 0) {
                int lastPos = editable.length() - 1;
                String c = String.valueOf(editable.charAt(lastPos));
                if (c.equals("\n")) {
                    editable.delete(lastPos, lastPos + 1);
                    if (editable.length() > 0) {
                        String s = input.getText().toString();
                        Connect.SendMessage(ContentCreator.sendString(s));
                        input.setText("");
                    }
                }
            }
        }
    };

    private OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            String s = input.getText().toString();

            Connect.SendMessage(ContentCreator.sendString(s));

            input.setText("");
        }
    };

    @Override
    public void setEdit(boolean edit) {
        super.setEdit(edit);

        if (!isEdit()) {
            input.addTextChangedListener(watcher);
            btn_send.setOnClickListener(listener);
        } else {
            input.addTextChangedListener(null);
            btn_send.setOnClickListener(null);
        }
    }
}
