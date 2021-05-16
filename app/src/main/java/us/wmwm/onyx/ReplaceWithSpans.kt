package us.wmwm.onyx

import android.text.SpannableStringBuilder

fun String.replaceWithSpans(s:(String)-> SpannableStringBuilder): SpannableStringBuilder {
    val spans = this.mapIndexedNotNull { index, c ->
        when(c) {
            '|'-> index
            else-> null
        }
    }
    val spannableStringBuilder = SpannableStringBuilder(this)
    (spans.size-1 downTo 0 step 2).map {
        val start = spans[it-1]
        val end = spans[it]
        val sub = this.substring(start+1,end)
        spannableStringBuilder.replace(start,end+1,s.invoke(sub))
    }
    return spannableStringBuilder
}