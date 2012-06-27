package org.LuceneZemberek.analysis.turkish;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

/**
 * Normalizes token text to lower case. <a name="version"/>
 * <p>
 * You must specify the required {@link Version} compatibility when creating
 * LowerCaseFilter:
 * <ul>
 * <li>As of 3.1, supplementary characters are properly lowercased.
 * </ul>
 */
public final class TurkishFilter extends TokenFilter {
	private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
	private KokBulucu kokBulucu;

	/**
	 * Create a new LowerCaseFilter, that normalizes token text to lower case.
	 * 
	 * @param matchVersion
	 *            See <a href="#version">above</a>
	 * @param in
	 *            TokenStream to filter
	 */
	public TurkishFilter(Version matchVersion, TokenStream in) {
		super(in);
		kokBulucu = new KokBulucu();
	}

	/**
	 * @deprecated Use {@link #LowerCaseFilter(Version, TokenStream)} instead.
	 */
	@Deprecated
	public TurkishFilter(TokenStream in) {
		this(Version.LUCENE_30, in);
	}

	@Override
	public final boolean incrementToken() throws IOException {
		if (input.incrementToken()) {
			String kok = kokBulucu.kok(termAtt.toString());
			termAtt.copyBuffer(kok.toCharArray(), 0, kok.length());
			termAtt.setLength(kok.length());
			return true;
		} else
			return false;
	}
}
