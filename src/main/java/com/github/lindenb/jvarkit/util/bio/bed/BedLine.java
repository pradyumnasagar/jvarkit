/*
The MIT License (MIT)

Copyright (c) 2016 Pierre Lindenbaum

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.


*/
package com.github.lindenb.jvarkit.util.bio.bed;

import java.util.Arrays;
import java.util.Objects;

import com.github.lindenb.jvarkit.lang.JvarkitException;

import htsjdk.samtools.QueryInterval;
import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.samtools.util.Interval;
import htsjdk.tribble.Feature;

public class BedLine
	implements Feature
	{
	private final String tokens[];
	
	public BedLine(final String tokens[])
		{
		this.tokens=tokens;
		}
	@Override
	@Deprecated
	public final String getChr() {
		return getContig();
		}
	
	@Override
	public String getContig() {
		return this.tokens[0];
		}
	
	@Override
	public int getStart() {
		return Integer.parseInt(this.tokens[1]) + 1; /* +1 because the Feature uses a +1 position */
		}
	
	@Override
	public int getEnd() {
		return (this.tokens.length<3 ?getStart(): Integer.parseInt(this.tokens[2]));
		}
	
	/** shortcut to <code>new Interval(getContig(), getStart(), getEnd())</code> */
	public Interval toInterval() {
		return new Interval(getContig(), getStart(), getEnd());
	}

	/** convert this bed line to QueryInterval using the provided SAMSequenceDictionary */
	public QueryInterval toQueryInterval(final SAMSequenceDictionary dict) {
		final int tid = Objects.requireNonNull(dict, "SAMSequenceDictionary is null!").getSequenceIndex(getContig());
		if( tid == -1) {
			throw new JvarkitException.ContigNotFoundInDictionary(getContig(), dict);
		}
		return new QueryInterval(tid, getStart(), getEnd());
	}
	
	
	public String get(final int index)
		{
		return (index<this.tokens.length?this.tokens[index]:null);
		}
	
	public String join(final CharSequence delimiter) {
		return String.join(delimiter, this.tokens);
	}
	public String join() {
		return join("\t");
	}

	
	public int getColumnCount()
		{
		return tokens.length;
		}
	public static boolean isBedHeader(final String line)
		{
		return line.startsWith("#") || line.startsWith("track") || line.startsWith("browser");
		}

	@Override
	public int hashCode() {
		return Arrays.hashCode(this.tokens);
		}
	
	@Override
	public boolean equals(final Object obj) {
		if(obj==this) return true;
		if(obj==null || !(obj instanceof BedLine)) return false;
		return Arrays.equals(this.tokens, BedLine.class.cast(obj).tokens);
		}
	
	@Override
	public String toString() {
		return this.join();
		}
	
	}
