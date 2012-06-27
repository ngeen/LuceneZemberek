package org.LuceneZemberek.analysis.turkish;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WordlistLoader;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.Version;

@SuppressWarnings("deprecation")
public class TurkishAnalyzer extends StopwordAnalyzerBase {

	/** Default maximum allowed token length */
	public static final int DEFAULT_MAX_TOKEN_LENGTH = 255;

	private int maxTokenLength = DEFAULT_MAX_TOKEN_LENGTH;

	/**
	 * Specifies whether deprecated acronyms should be replaced with HOST type.
	 * See {@linkplain "https://issues.apache.org/jira/browse/LUCENE-1068"}
	 */
	private final boolean replaceInvalidAcronym;

	public static final Set<?> TURKISH_STOP_WORDS_SET;

	static {
		final List<String> stopWords = Arrays.asList("a", "acaba", "ama",
				"ancak", "az", "b", "bazen", "bazý", "bile", "bir", "biri",
				"bu", "buna", "bunda", "bundan", "bunu", "bunun", "çok",
				"çünkü", "da", "daha", "de", "deðil", "diye", "dolayý", "en",
				"fakat", "falan", "felan", "filan", "gene", "gibi", "hâlâ",
				"hani", "hatta", "hem", "henüz", "hep", "hepsi", "hepsine",
				"hepsini", "her", "hiç", "hiçbiri", "hiçbirine", "hiçbirini",
				"için", "ile", "ise", "iþte", "kaç", "kadar", "ki", "kim",
				"kime", "kimi", "kimin", "kimisi", "madem", "mý", "mý", "mi",
				"mu", "mu", "mü", "mü", "nasýl", "ne", "nesi", "o", "ona",
				"onu", "onun", "oysa", "oysaki", "öbürü", "ön", "öyle", "sen",
				"þayet", "þey", "þeyden", "þeye", "þeyi", "þeyler", "þöyle",
				"þu", "þuna", "þunda", "þundan", "þunlar", "þunu", "þunun",
				"tabi", "üzere", "ve", "veya", "veyahut", "ya", "yada", "yani",
				"yine", "zaten", "zira");
		
		final CharArraySet stopSet = new CharArraySet(Version.LUCENE_CURRENT,
				stopWords.size(), false);
		stopSet.addAll(stopWords);
		TURKISH_STOP_WORDS_SET = CharArraySet.unmodifiableSet(stopSet);
	}

	/**
	 * An unmodifiable set containing some common English words that are usually
	 * not useful for searching.
	 */
	public static final Set<?> STOP_WORDS_SET = TURKISH_STOP_WORDS_SET;

	/**
	 * Builds an analyzer with the given stop words.
	 * 
	 * @param matchVersion
	 *            Lucene version to match See
	 *            {@link <a href="#version">above</a>}
	 * @param stopWords
	 *            stop words
	 */
	public TurkishAnalyzer(Version matchVersion, Set<?> stopWords) {
		super(matchVersion, stopWords);
		replaceInvalidAcronym = matchVersion.onOrAfter(Version.LUCENE_24);
	}

	/**
	 * Builds an analyzer with the default stop words ({@link #STOP_WORDS_SET}).
	 * 
	 * @param matchVersion
	 *            Lucene version to match See
	 *            {@link <a href="#version">above</a>}
	 */
	public TurkishAnalyzer(Version matchVersion) {
		this(matchVersion, STOP_WORDS_SET);
	}

	/**
	 * Builds an analyzer with the stop words from the given file.
	 * 
	 * @see WordlistLoader#getWordSet(Reader, Version)
	 * @param matchVersion
	 *            Lucene version to match See
	 *            {@link <a href="#version">above</a>}
	 * @param stopwords
	 *            File to read stop words from
	 * @deprecated Use {@link #StandardAnalyzer(Version, Reader)} instead.
	 */
	@Deprecated
	public TurkishAnalyzer(Version matchVersion, File stopwords)
			throws IOException {
		this(matchVersion, WordlistLoader.getWordSet(
				IOUtils.getDecodingReader(stopwords, IOUtils.CHARSET_UTF_8),
				matchVersion));
	}

	/**
	 * Builds an analyzer with the stop words from the given reader.
	 * 
	 * @see WordlistLoader#getWordSet(Reader, Version)
	 * @param matchVersion
	 *            Lucene version to match See
	 *            {@link <a href="#version">above</a>}
	 * @param stopwords
	 *            Reader to read stop words from
	 */
	public TurkishAnalyzer(Version matchVersion, Reader stopwords)
			throws IOException {
		this(matchVersion, WordlistLoader.getWordSet(stopwords, matchVersion));
	}

	/**
	 * Set maximum allowed token length. If a token is seen that exceeds this
	 * length then it is discarded. This setting only takes effect the next time
	 * tokenStream or reusableTokenStream is called.
	 */
	public void setMaxTokenLength(int length) {
		maxTokenLength = length;
	}

	/**
	 * @see #setMaxTokenLength
	 */
	public int getMaxTokenLength() {
		return maxTokenLength;
	}

	@Override
	protected TokenStreamComponents createComponents(final String fieldName,
			final Reader reader) {
		final StandardTokenizer src = new StandardTokenizer(matchVersion,
				reader);
		src.setMaxTokenLength(maxTokenLength);
		src.setReplaceInvalidAcronym(replaceInvalidAcronym);
		TokenStream tok = new StandardFilter(matchVersion, src);
		tok = new LowerCaseFilter(matchVersion, tok);
		tok = new StopFilter(matchVersion, tok, stopwords);
		tok = new TurkishFilter(matchVersion, tok);
		return new TokenStreamComponents(src, tok) {
			@Override
			protected boolean reset(final Reader reader) throws IOException {
				src.setMaxTokenLength(TurkishAnalyzer.this.maxTokenLength);
				return super.reset(reader);
			}
		};
	}
}
