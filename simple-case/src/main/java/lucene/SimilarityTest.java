package lucene;

import com.chenlb.mmseg4j.analysis.MaxWordAnalyzer;
import java.io.IOException;
import java.io.StringReader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

/**
 * 测试相似度 自定义
 */
public class SimilarityTest {
    private static final String PATH = "/home/song/someDemo/index";
    private static Analyzer zh = new MaxWordAnalyzer();
    static Directory directory= new RAMDirectory();
    public static void main(String[] args){
        try {
            write();
            search("三国 四国");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
    private static void search(String key) throws IOException, ParseException {
        int hitsPerPage=10;
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        searcher.setSimilarity(new CustomSimilarity());
        Query q1= new QueryParser(Version.LUCENE_45,"title",zh).parse(key);
//        q1.setBoost(10f);
        Query q2=new QueryParser(Version.LUCENE_45,"content",zh).parse(key);
        BooleanQuery q=new BooleanQuery();
        q.add(q1, BooleanClause.Occur.SHOULD);
        q.add(q2, BooleanClause.Occur.SHOULD);
        System.out.println(q.toString());
        TopDocs docs = searcher.search(q, hitsPerPage);
        ScoreDoc[] hits = docs.scoreDocs;
        for(int i=0;i<hits.length;i++){
            Document d=searcher.doc(hits[i].doc);
            System.out.println(searcher.explain(q,hits[i].doc));
            System.out.println(hits[i].score+":"+d.get("title")+"\t"+d.get("content"));
        }
    }
    private static void write() throws IOException {
        IndexWriter writer=getIndexWriter();
        String[] titles = {"三国演义",
                "三国无双",
                "四国演义",
                "水浒"};
        String[] contents={"演动漫高清在线观看",
                "东汉末年，山河动荡刘汉王朝气数将尽。内有十常侍颠倒黑白，祸乱朝纲。外有张氏兄弟高呼“苍天已死，黄巾",
                "无双 ",
                "虽然标题中没有：三国演义,三国演义,三国演义,三国演义,三国演义"
        };
        for(int i=0;i<4;i++){
            displayToken(titles[i],zh);
            displayToken(contents[i],zh);
            writer.addDocument(getDoc(titles[i],contents[i]));
        }
        writer.commit();
        writer.close();
    }
    public static void displayToken(String str, Analyzer analyzer){
        try {
            //第一个参数只是标识性没有实际作用
            TokenStream stream = analyzer.tokenStream("", new StringReader(str));

            //获取各个单词之间的偏移量
            OffsetAttribute offseta = stream.addAttribute(OffsetAttribute.class);
            //获取每个单词信息
            CharTermAttribute chara = stream.addAttribute(CharTermAttribute.class);
            //获取当前分词的类型
            TypeAttribute typea = stream.addAttribute(TypeAttribute.class);
            while(stream.incrementToken()){
                System.out.println(chara+"\t[" + offseta.startOffset()+" - " + offseta.endOffset() + "]\t<" + typea +">");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static Document getDoc(String title,String content){
        Document d=new Document();
        d.add(new TextField("title", title, Field.Store.YES));
        d.add(new TextField("content", content, Field.Store.YES));
        return d;
    }

    private static IndexWriter getIndexWriter() throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_45, zh);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        config.setSimilarity(new CustomSimilarity());
        config.setWriteLockTimeout(3 * 1000);
        return new IndexWriter(directory, config);

    }
}
