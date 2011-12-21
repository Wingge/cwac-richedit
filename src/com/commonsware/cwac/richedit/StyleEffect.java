/***
  Copyright (c) 2008-2011 CommonsWare, LLC
  
  Licensed under the Apache License, Version 2.0 (the "License"); you may
  not use this file except in compliance with the License. You may obtain
  a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/    

package com.commonsware.cwac.richedit;

import android.text.Spannable;
import android.text.style.StyleSpan;

public class StyleEffect extends Effect<Boolean> {
  private int style;

  StyleEffect(int style) {
    this.style=style;
  }

  @Override
  boolean existsInSelection(RichEditText editor) {
    Selection selection=new Selection(editor);
    Spannable str=editor.getText();
    boolean result=false;

    for (StyleSpan span : getStyleSpans(str, selection)) {
      if (span.getStyle() == style) {
        result=true;
        break;
      }
    }

    return(result);
  }

  @Override
  Boolean valueInSelection(RichEditText editor) {
    return(existsInSelection(editor));
  }

  @Override
  void applyToSelection(RichEditText editor, Boolean add) {
    Selection selection=new Selection(editor);
    Spannable str=editor.getText();

    for (StyleSpan span : getStyleSpans(str, selection)) {
      if (span.getStyle() == style) {
        str.removeSpan(span);
      }
    }

    if (add) {
      str.setSpan(new StyleSpan(style), selection.start, selection.end,
                  Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
  }

  private StyleSpan[] getStyleSpans(Spannable str, Selection selection) {
    return(str.getSpans(selection.start, selection.end, StyleSpan.class));
  }
}
