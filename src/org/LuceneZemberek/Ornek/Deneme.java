package org.LuceneZemberek.Ornek;

import java.io.IOException;

import org.LuceneZemberek.analysis.turkish.TurkishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class Deneme {
	public static void main(String[] args) throws IOException, ParseException {
	    TurkishAnalyzer analyzer = new TurkishAnalyzer(Version.LUCENE_35);

	    Directory index = new RAMDirectory();

	    IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_35, analyzer);

	    IndexWriter w = new IndexWriter(index, config);
	    addDoc(w, "Bu Kitabý Okuyorum");
	    addDoc(w, "Bu Kitaplar Tam Okumalýk");
	    addDoc(w, "O kitap Bu Kitap Þu Pikap");
	    addDoc(w, "Kitabýn 5. sayfasý");
	    w.close();

	    String querystr = args.length > 0 ? args[0] : "KiTap";

	    Query q = new QueryParser(Version.LUCENE_35, "title", analyzer).parse(querystr);

	    int hitsPerPage = 10;
	    IndexReader reader = IndexReader.open(index);
	    IndexSearcher searcher = new IndexSearcher(reader);
	    TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
	    searcher.search(q, collector);
	    ScoreDoc[] hits = collector.topDocs().scoreDocs;
	   
	    System.out.println("Found " + hits.length + " hits.");
	    for(int i=0;i<hits.length;++i) {
	      int docId = hits[i].doc;
	      Document d = searcher.doc(docId);
	      System.out.println((i + 1) + ". " + d.get("title"));
	    }


	    searcher.close();
	  }

	  private static void addDoc(IndexWriter w, String value) throws IOException {
	    Document doc = new Document();
	    doc.add(new Field("title", value, Field.Store.YES, Field.Index.ANALYZED));
	    w.addDocument(doc);
	  }
}
