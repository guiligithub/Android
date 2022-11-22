/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hyphenate.easeui.utils;

import android.content.Context;
import android.net.Uri;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.style.ImageSpan;

import com.hyphenate.easeui.EaseIM;
import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.model.EaseDefaultEmojiconDatas;
import com.hyphenate.easeui.provider.EaseEmojiconInfoProvider;
import com.hyphenate.easeui.widget.CenterImageSpan;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EaseSmileUtils {
    public static final String DELETE_KEY = "em_delete_delete_expression";
    
	public static final String ee_1 = "[):]";
	public static final String ee_2 = "[:D]";
	public static final String ee_3 = "[;)]";
	public static final String ee_4 = "[:-o]";
	public static final String ee_5 = "[:p]";
	public static final String ee_6 = "[(H)]";
	public static final String ee_7 = "[:@]";
	public static final String ee_8 = "[:s]";
	public static final String ee_9 = "[:$]";
	public static final String ee_10 = "[:(]";
	public static final String ee_11 = "[:'(]";
	public static final String ee_12 = "[:|]"; 
	public static final String ee_13 = "[(a)]";
	public static final String ee_14 = "[8o|]";
	public static final String ee_15 = "[8-|]";
	public static final String ee_16 = "[+o(]";
	public static final String ee_17 = "[<o)]";
	public static final String ee_18 = "[|-)]";
	public static final String ee_19 = "[*-)]";
	public static final String ee_20 = "[:-#]";
	public static final String ee_21 = "[:-*]";
	public static final String ee_22 = "[^o)]";
	public static final String ee_23 = "[8-)]";
	public static final String ee_24 = "[(|)]";
	public static final String ee_25 = "[(u)]";
	public static final String ee_26 = "[(S)]";
	public static final String ee_27 = "[(*)]";
	public static final String ee_28 = "[(#)]";
	public static final String ee_29 = "[(R)]";
	public static final String ee_30 = "[({)]";
	public static final String ee_31 = "[(})]";
	public static final String ee_32 = "[(k)]";
	public static final String ee_33 = "[(F)]";
	public static final String ee_34 = "[(W)]";
	public static final String ee_35 = "[(D)]";
	public static final String ee_36 = "[(a^)]";
	public static final String ee_37 = "[(b)]";
	public static final String ee_38 = "[(c)]";
	public static final String ee_39 = "[(d)]";
	public static final String ee_40 = "[(e)]";
	public static final String ee_41 = "[(f)]";
	public static final String ee_42 = "[(g)]";
	public static final String ee_43 = "[(h)]";
	public static final String ee_44 = "[(i)]";
	public static final String ee_45 = "[(j)]";
	public static final String ee_46 = "[(k^)]";
	public static final String ee_47 = "[(l)]";
	public static final String ee_48 = "[(m)]";
	public static final String ee_49 = "[(n)]";
	public static final String ee_50 = "[(o)]";
	public static final String ee_51 = "[(p)]";
	public static final String ee_52 = "[(q)]";
	public static final String ee_53 = "[(r)]";
	public static final String ee_54 = "[(s)]";
	public static final String ee_55 = "[(t)]";
	public static final String ee_56 = "[(u^)]";
	public static final String ee_57 = "[(v)]";
	public static final String ee_58 = "[(w)]";
	public static final String ee_59 = "[(x)]";
	public static final String ee_60 = "[(y)]";
	public static final String ee_61 = "[(z)]";

	public static final String ee_62 = "[(A)]";
	public static final String ee_63 = "[(B)]";
	public static final String ee_64 = "[(C)]";
	public static final String ee_65 = "[(D^)]";
	public static final String ee_66 = "[(E)]";
	public static final String ee_67 = "[(F^)]";
	public static final String ee_68 = "[(G)]";
	public static final String ee_69 = "[(H^)]";
	public static final String ee_70 = "[(I)]";
	public static final String ee_71 = "[(J)]";
	public static final String ee_72 = "[(K)]";
	public static final String ee_73 = "[(L)]";
	public static final String ee_74 = "[(M)]";
	public static final String ee_75 = "[(N)]";
	public static final String ee_76 = "[(O)]";
	public static final String ee_77 = "[(P)]";
	public static final String ee_78 = "[(Q)]";
	public static final String ee_79 = "[(R^)]";
	public static final String ee_80 = "[(S^)]";
	public static final String ee_81 = "[(T)]";
	public static final String ee_82 = "[(U)]";
	public static final String ee_83 = "[(V)]";
	public static final String ee_84 = "[(W^)]";
	public static final String ee_85 = "[(X)]";
	public static final String ee_86 = "[(Y)]";
	public static final String ee_87 = "[(Z)]";

	public static final String ee_88 = "[(a^A)]";
	public static final String ee_89 = "[(b^B)]";
	public static final String ee_90 = "[(c^C)]";
	public static final String ee_91 = "[(d^D)]";
	public static final String ee_92 = "[(e^E)]";
	public static final String ee_93 = "[(f^F)]";
	public static final String ee_94 = "[(g^G)]";
	public static final String ee_95 = "[(h^H)]";
	public static final String ee_96 = "[(i^I)]";
	public static final String ee_97 = "[(j^J)]";
	public static final String ee_98 = "[(k^K)]";
	public static final String ee_99 = "[(l^L)]";
	public static final String ee_100 = "[(m^M)]";
	public static final String ee_101 = "[(n^N)]";
	public static final String ee_102 = "[(o^O)]";
	public static final String ee_103 = "[(p^P)]";
	public static final String ee_104 = "[(q^Q)]";
	public static final String ee_105 = "[(r^R)]";
	public static final String ee_106 = "[(s^S)]";
	public static final String ee_107 = "[(t^T)]";
	public static final String ee_108 = "[(u^U)]";
	public static final String ee_109 = "[(v^V)]";
	public static final String ee_110 = "[(w^W)]";
	public static final String ee_111 = "[(x^X)]";
	public static final String ee_112 = "[(y^Y)]";
	public static final String ee_113 = "[(z^Z)]";

	public static final String ee_114 = "[A):]";
	public static final String ee_115 = "[A:D]";
	public static final String ee_116 = "[A;)]";
	public static final String ee_117 = "[A:-o]";
	public static final String ee_118 = "[A:p]";
	public static final String ee_119 = "[(H^A)]";
	public static final String ee_120 = "[:@^]";
	public static final String ee_121 = "[:^'(]";
	public static final String ee_122 = "[:|^]";
	public static final String ee_123 = "[(D_)]";

	private static final Factory spannableFactory = Factory
	        .getInstance();
	
	private static final Map<Pattern, Object> emoticons = new HashMap<Pattern, Object>();
	

	static {
	    EaseEmojicon[] emojicons = EaseDefaultEmojiconDatas.getData();
		for (EaseEmojicon emojicon : emojicons) {
			addPattern(emojicon.getEmojiText(), emojicon.getIcon());
		}
	    EaseEmojiconInfoProvider emojiconInfoProvider = EaseIM.getInstance().getEmojiconInfoProvider();
	    if(emojiconInfoProvider != null && emojiconInfoProvider.getTextEmojiconMapping() != null){
	        for(Entry<String, Object> entry : emojiconInfoProvider.getTextEmojiconMapping().entrySet()){
	            addPattern(entry.getKey(), entry.getValue());
	        }
	    }
	    
	}

	/**
	 * add text and icon to the map
	 * @param emojiText-- text of emoji
	 * @param icon -- resource id or local path
	 */
	public static void addPattern(String emojiText, Object icon){
	    emoticons.put(Pattern.compile(Pattern.quote(emojiText)), icon);
	}
	

	/**
	 * replace existing spannable with smiles
	 * @param context
	 * @param spannable
	 * @return
	 */
	public static boolean addSmiles(Context context, Spannable spannable) {
	    boolean hasChanges = false;
	    for (Entry<Pattern, Object> entry : emoticons.entrySet()) {
	        Matcher matcher = entry.getKey().matcher(spannable);
	        while (matcher.find()) {
	            boolean set = true;
	            for (ImageSpan span : spannable.getSpans(matcher.start(),
	                    matcher.end(), ImageSpan.class))
	                if (spannable.getSpanStart(span) >= matcher.start()
	                        && spannable.getSpanEnd(span) <= matcher.end())
	                    spannable.removeSpan(span);
	                else {
	                    set = false;
	                    break;
	                }
	            if (set) {
	                hasChanges = true;
	                Object value = entry.getValue();
	                if(value instanceof String && !((String) value).startsWith("http")){
	                    File file = new File((String) value);
	                    if(!file.exists() || file.isDirectory()){
	                        return false;
	                    }
						CenterImageSpan imageSpan = new CenterImageSpan(context, Uri.fromFile(file));
	                    spannable.setSpan(imageSpan,
	                            matcher.start(), matcher.end(),
	                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	                }else{
						CenterImageSpan imageSpan = new CenterImageSpan(context, (Integer)value);
	                    spannable.setSpan(imageSpan,
	                            matcher.start(), matcher.end(),
	                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	                }
	            }
	        }
	    }
	    
	    return hasChanges;
	}

	public static Spannable getSmiledText(Context context, CharSequence text) {
	    Spannable spannable = spannableFactory.newSpannable(text);
	    addSmiles(context, spannable);
	    return spannable;
	}
	
	public static boolean containsKey(String key){
		boolean b = false;
		for (Entry<Pattern, Object> entry : emoticons.entrySet()) {
	        Matcher matcher = entry.getKey().matcher(key);
	        if (matcher.find()) {
	        	b = true;
	        	break;
	        }
		}
		
		return b;
	}
	
	public static int getSmilesSize(){
        return emoticons.size();
    }
    
	
}
