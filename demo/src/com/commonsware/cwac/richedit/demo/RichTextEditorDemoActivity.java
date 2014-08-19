/***
  Copyright (c) 2012 CommonsWare, LLC
  
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

package com.commonsware.cwac.richedit.demo;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.commonsware.cwac.richedit.RichEditText;

public class RichTextEditorDemoActivity extends Activity {
  RichEditText editor=null;
  
  EmoticonHandler mEmoticonHandler;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    setContentView(R.layout.main);
    
    editor=(RichEditText)findViewById(R.id.editor);
    mEmoticonHandler = new EmoticonHandler(editor);
    editor.enableActionModes(true);
    
    
//    editor.addTextChangedListener(watcher);
    
    
    findViewById(R.id.btn_add_img).setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			
			String key = imageNamePrefix+imageIndex;
			mEmoticonHandler.insert(key, R.drawable.ic_launcher);
			imageSet.put(key, "R.drawable.ic_launcher"+imageIndex);
			imageIndex++;
			
//			
//	        Drawable drawable=getResources().getDrawable(R.drawable.ic_launcher);
//	        drawable.setBounds(0,0,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
//	        ImageSpan span=new ImageSpan(drawable);
//	        SpannableString spannableString=new SpannableString("span");
//	        spannableString.setSpan(span, editor.getSelectionStart(), editor.getSelectionEnd(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//	        editor.append(spannableString);
			
		}
	});
    
    findViewById(R.id.btn_save).setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			indexSortedList.clear();
			sortImage(0);
			for (Character chart : indexSortedList) {
				System.out.println("sorted========="+imageSet.get(imageNamePrefix+chart));
			}
			String htmlString = Html.toHtml(editor.getText());
			System.out.println("htmlString========"+htmlString);
		}
	});
    
  }
  
  private static int imageIndex;
  private static Hashtable<String, String> imageSet = new Hashtable<String, String>();
  private static final String imageNamePrefix = "image-";
  private static final int imageNameLength = imageNamePrefix.length();
  private List<Character> indexSortedList = new ArrayList<Character>();
  
  private void sortImage(int startIndex){
	  String wholeText = editor.getText().toString();
	  startIndex = wholeText.indexOf(imageNamePrefix, startIndex);
	  if(startIndex!=-1){
		  indexSortedList.add(wholeText.charAt(startIndex+imageNameLength));
		  sortImage(startIndex+imageNameLength);
	  }
  }
  
  private static class EmoticonHandler implements TextWatcher {

      private final EditText mEditor;
      private final ArrayList<ImageSpan> mEmoticonsToRemove = new ArrayList<ImageSpan>();

      public EmoticonHandler(EditText editor) {
          // Attach the handler to listen for text changes.
          mEditor = editor;
          mEditor.addTextChangedListener(this);
      }

      public void insert(String emoticon, int resource) {
          // Create the ImageSpan
          Drawable drawable = mEditor.getResources().getDrawable(resource);
          drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
          ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);

          // Get the selected text.
          int start = mEditor.getSelectionStart();
          int end = mEditor.getSelectionEnd();
          Editable message = mEditor.getEditableText();

          // Insert the emoticon.
          message.replace(start, end, emoticon);
          message.setSpan(span, start, start + emoticon.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
      }

      @Override
      public void beforeTextChanged(CharSequence text, int start, int count, int after) {
          // Check if some text will be removed.
          if (count > 0) {
              int end = start + count;
              Editable message = mEditor.getEditableText();
              ImageSpan[] list = message.getSpans(start, end, ImageSpan.class);

              for (ImageSpan span : list) {
                  // Get only the emoticons that are inside of the changed
                  // region.
                  int spanStart = message.getSpanStart(span);
                  int spanEnd = message.getSpanEnd(span);
                  if ((spanStart < end) && (spanEnd > start)) {
                      // Add to remove list
                      mEmoticonsToRemove.add(span);
                  }
              }
          }
      }

      @Override
      public void afterTextChanged(Editable text) {
          Editable message = mEditor.getEditableText();

          // Commit the emoticons to be removed.
          for (ImageSpan span : mEmoticonsToRemove) {
              int start = message.getSpanStart(span);
              int end = message.getSpanEnd(span);

              // Remove the span
              message.removeSpan(span);

              // Remove the remaining emoticon text.
              if (start != end) {
                  message.delete(start, end);
              }
          }
          mEmoticonsToRemove.clear();
      }

      @Override
      public void onTextChanged(CharSequence text, int start, int before, int count) {
      }

  }
}
