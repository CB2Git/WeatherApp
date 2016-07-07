package com.jay.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class HanYu2Pinyin {
	/**
	 * 获取传入的汉语起始拼音字母
	 * 
	 * @param hanyu
	 * @return
	 */
	public static char getFirstPinYin(String hanyu) {
		char c = hanyu.charAt(0);
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
		String[] stringArray = null;
		try {
			stringArray = PinyinHelper.toHanyuPinyinStringArray(c, format);
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
		}
		String min = stringArray[0];
		for (int i = 1; i < stringArray.length; i++) {
			if (comparePinYin(min, stringArray[i]) > 0) {
				min = stringArray[i];
			}
		}
		return min.charAt(0);
	}

	/**
	 * 比较两个汉语的起始拼音的大小
	 * 
	 * @param hanyu1
	 * @param hanyu2
	 * @return <0 前面的比较小，0一样，>0 前面比较大
	 */
	public static int comparePinYin(String pinyin1, String pinyin2) {
		char pinyin11 = pinyin1.charAt(0);
		char pinyin22 = pinyin2.charAt(0);
		return pinyin11 - pinyin22;
	}
}
